package org.leralix.towns_and_nations_dynmap.Storage;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerSet;
import org.leralix.towns_and_nations_dynmap.Style.AreaStyle;
import org.leralix.towns_and_nations_dynmap.TownsAndNations_Dynmap;
import org.leralix.towns_and_nations_dynmap.event.RegionRenderEvent;
import org.leralix.towns_and_nations_dynmap.event.TownRenderEvent;
import org.tan.TownsAndNations.DataClass.newChunkData.ClaimedChunk2;
import org.tan.TownsAndNations.DataClass.newChunkData.RegionClaimedChunk;
import org.tan.TownsAndNations.DataClass.newChunkData.TownClaimedChunk;
import org.tan.TownsAndNations.DataClass.territoryData.RegionData;
import org.tan.TownsAndNations.DataClass.territoryData.TownData;
import org.tan.TownsAndNations.TownsAndNations;

import java.util.*;

public class ChunkManager {

    private final Map<String, AreaMarker> AreaMap = new HashMap<>();
    private final MarkerSet set;
    private final AreaStyle townAreaStyle;
    private final AreaStyle regionAreaStyle;
    private final TownsAndNations_Dynmap plugin = TownsAndNations_Dynmap.getPlugin();
    private final Map<String, AreaMarker> existingAreaMarkers = plugin.getAreaMarkers();


    enum direction {XPLUS, ZPLUS, XMINUS, ZMINUS};

    public ChunkManager(MarkerSet set) {
        this.set = set;

        this.townAreaStyle = new AreaStyle(TownsAndNations.getPlugin().getConfig(), "town_fieldStyle");
        this.regionAreaStyle = new AreaStyle(TownsAndNations.getPlugin().getConfig(), "region_fieldStyle");
    }

    public void updateTown(TownData townData, Map<String, AreaMarker> newWorldNameAreaMarkerMap) {

        int poly_index = 0; /* Index of polygon for when a town has multiple shapes. */

        Collection<TownClaimedChunk> townClaimedChunks = townData.getClaims();
        if(townClaimedChunks.isEmpty())
            return;

        String infoWindowPopup = TownDescriptionStorage.get(townData.getID()).getChunkDescription();

        HashMap<String, TileFlags> worldNameShapeMap = new HashMap<>();
        LinkedList<TownClaimedChunk> claimedChunksToDraw = new LinkedList<>();

        World currentWorld = null;
        TileFlags currentShape = null;


        for (TownClaimedChunk townClaimedChunk : townClaimedChunks) {
            if (townClaimedChunk.getWorld() != currentWorld) {
                String worldName = townClaimedChunk.getWorld().getName();
                currentShape = worldNameShapeMap.get(worldName);
                if (currentShape == null) {
                    currentShape = new TileFlags();
                    worldNameShapeMap.put(worldName, currentShape);
                }
                currentWorld = townClaimedChunk.getWorld();
            }
            currentShape.setFlag(townClaimedChunk.getX(), townClaimedChunk.getZ(), true); /* Set flag for block */
            claimedChunksToDraw.addLast(townClaimedChunk);
        }

        while(claimedChunksToDraw != null) {
            LinkedList<TownClaimedChunk> ourTownBlocks = null;
            LinkedList<TownClaimedChunk> townBlockLeftToDraw = null;
            TileFlags ourShape = null;
            int minx = Integer.MAX_VALUE;
            int minz = Integer.MAX_VALUE;
            for(TownClaimedChunk claimedChunk : claimedChunksToDraw) {
                int tbX = claimedChunk.getX();
                int tbZ = claimedChunk.getZ();
                if(ourShape == null) {   /* If not started, switch to world for this block first */
                    if(claimedChunk.getWorld() != currentWorld) {
                        currentWorld = claimedChunk.getWorld();
                        currentShape = worldNameShapeMap.get(currentWorld.getName());
                    }
                }
                /* If we need to start shape, and this block is not part of one yet */
                if((ourShape == null) && currentShape.getFlag(tbX, tbZ)) {
                    ourShape = new TileFlags();  /* Create map for shape */
                    ourTownBlocks = new LinkedList<>();
                    floodFillTarget(currentShape, ourShape, tbX, tbZ);   /* Copy shape */
                    ourTownBlocks.add(claimedChunk); /* Add it to our node list */
                    minx = tbX; minz = tbZ;
                }
                /* If shape found, and we're in it, add to our node list */
                else if((ourShape != null) && (claimedChunk.getWorld() == currentWorld) &&
                        (ourShape.getFlag(tbX, tbZ))) {
                    ourTownBlocks.add(claimedChunk);
                    if(tbX < minx) {
                        minx = tbX; minz = tbZ;
                    }
                    else if((tbX == minx) && (tbZ < minz)) {
                        minz = tbZ;
                    }
                }
                else {  /* Else, keep it in the list for the next polygon */
                    if(townBlockLeftToDraw == null)
                        townBlockLeftToDraw = new LinkedList<>();
                    townBlockLeftToDraw.add(claimedChunk);
                }
            }
            claimedChunksToDraw = townBlockLeftToDraw; /* Replace list (null if no more to process) */
            if(ourShape != null) {
                try {
                    poly_index = traceTownOutline(townData, newWorldNameAreaMarkerMap, poly_index, infoWindowPopup, currentWorld.getName(), ourShape, minx, minz);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }


    }

    public void updateRegion(RegionData regionData, Map<String, AreaMarker> newWorldNameAreaMarkerMap) {

        int poly_index = 0; /* Index of polygon for when a town has multiple shapes. */

        Collection<RegionClaimedChunk> townClaimedChunks = regionData.getClaims();
        if(townClaimedChunks.isEmpty())
            return;

        String infoWindowPopup = RegionDescriptionStorage.get(regionData.getID()).getChunkDescription();

        HashMap<String, TileFlags> worldNameShapeMap = new HashMap<>();
        LinkedList<RegionClaimedChunk> claimedChunksToDraw = new LinkedList<>();

        World currentWorld = null;
        TileFlags currentShape = null;


        for(RegionClaimedChunk regionClaimedChunk : townClaimedChunks) {
            if(regionClaimedChunk.getWorld() != currentWorld) { /* Not same world */
                String worldName = regionClaimedChunk.getWorld().getName();
                currentShape = worldNameShapeMap.get(worldName);  /* Find existing */
                if(currentShape == null) {
                    currentShape = new TileFlags();
                    worldNameShapeMap.put(worldName, currentShape);   /* Add fresh one */
                }
                currentWorld = regionClaimedChunk.getWorld();
            }
            currentShape.setFlag(regionClaimedChunk.getX(), regionClaimedChunk.getZ(), true); /* Set flag for block */
            claimedChunksToDraw.addLast(regionClaimedChunk);
        }

        while(claimedChunksToDraw != null) {
            LinkedList<RegionClaimedChunk> ourTownBlocks = null;
            LinkedList<RegionClaimedChunk> townBlockLeftToDraw = null;
            TileFlags ourShape = null;
            int minx = Integer.MAX_VALUE;
            int minz = Integer.MAX_VALUE;
            for(RegionClaimedChunk claimedChunk : claimedChunksToDraw) {
                int tbX = claimedChunk.getX();
                int tbZ = claimedChunk.getZ();
                if(ourShape == null) {   /* If not started, switch to world for this block first */
                    if(claimedChunk.getWorld() != currentWorld) {
                        currentWorld = claimedChunk.getWorld();
                        currentShape = worldNameShapeMap.get(currentWorld.getName());
                    }
                }
                /* If we need to start shape, and this block is not part of one yet */
                if((ourShape == null) && currentShape.getFlag(tbX, tbZ)) {
                    ourShape = new TileFlags();  /* Create map for shape */
                    ourTownBlocks = new LinkedList<>();
                    floodFillTarget(currentShape, ourShape, tbX, tbZ);   /* Copy shape */
                    ourTownBlocks.add(claimedChunk); /* Add it to our node list */
                    minx = tbX; minz = tbZ;
                }
                /* If shape found, and we're in it, add to our node list */
                else if((ourShape != null) && (claimedChunk.getWorld() == currentWorld) &&
                        (ourShape.getFlag(tbX, tbZ))) {
                    ourTownBlocks.add(claimedChunk);
                    if(tbX < minx) {
                        minx = tbX; minz = tbZ;
                    }
                    else if((tbX == minx) && (tbZ < minz)) {
                        minz = tbZ;
                    }
                }
                else {  /* Else, keep it in the list for the next polygon */
                    if(townBlockLeftToDraw == null)
                        townBlockLeftToDraw = new LinkedList<>();
                    townBlockLeftToDraw.add(claimedChunk);
                }
            }
            claimedChunksToDraw = townBlockLeftToDraw; /* Replace list (null if no more to process) */
            if(ourShape != null) {
                try {
                    poly_index = traceTownOutline(regionData, newWorldNameAreaMarkerMap, poly_index, infoWindowPopup, currentWorld.getName(), ourShape, minx, minz);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }


    }

    public void add(ClaimedChunk2 claimedChunk) {

        String markerID = claimedChunk.getX() + "_" + claimedChunk.getZ() + "_" + claimedChunk.getWorldUUID();
        String worldName = Bukkit.getWorld(UUID.fromString(claimedChunk.getWorldUUID())).getName();
        double[] x = new double[] { claimedChunk.getX()*16, claimedChunk.getX()*16 + 16 };
        double[] z = new double[] { claimedChunk.getZ()*16, claimedChunk.getZ()*16 + 16 };
        int color = TownsAndNations.getAPI().getChunkColor(claimedChunk);


        AreaMarker areamarker = set.createAreaMarker(markerID, "------------------------", false, worldName, x, z, false);


        if(claimedChunk instanceof TownClaimedChunk townChunk) {
            String description = TownDescriptionStorage.get(townChunk.getOwnerID()).getChunkDescription();

            int strokeWeight = townAreaStyle.getBaseStrokeWeight();
            double strokeOpacity = townAreaStyle.getStrokeOpacity();
            double fillOpacity = townAreaStyle.getFillOpacity();

            areamarker.setDescription(description);
            areamarker.setLineStyle(strokeWeight, strokeOpacity, color);
            areamarker.setFillStyle(fillOpacity, color);
        }
        else if (claimedChunk instanceof RegionClaimedChunk regionClaimedChunk) {
            String description = RegionDescriptionStorage.get(regionClaimedChunk.getOwnerID()).getChunkDescription();

            int strokeWeight = regionAreaStyle.getBaseStrokeWeight();
            double strokeOpacity = regionAreaStyle.getStrokeOpacity();
            double fillOpacity = regionAreaStyle.getFillOpacity();

            areamarker.setDescription(description);
            areamarker.setLineStyle(strokeWeight, strokeOpacity, color);
            areamarker.setFillStyle(fillOpacity, color);
        }



        AreaMap.put(markerID, areamarker);
    }



    private int floodFillTarget(TileFlags src, TileFlags dest, int x, int y) {
        int cnt = 0;
        ArrayDeque<int[]> stack = new ArrayDeque<int[]>();
        stack.push(new int[] { x, y });

        while (stack.isEmpty() == false) {
            int[] nxt = stack.pop();
            x = nxt[0];
            y = nxt[1];
            if (src.getFlag(x, y)) { /* Set in src */
                src.setFlag(x, y, false); /* Clear source */
                dest.setFlag(x, y, true); /* Set in destination */
                cnt++;
                if (src.getFlag(x + 1, y))
                    stack.push(new int[] { x + 1, y });
                if (src.getFlag(x - 1, y))
                    stack.push(new int[] { x - 1, y });
                if (src.getFlag(x, y + 1))
                    stack.push(new int[] { x, y + 1 });
                if (src.getFlag(x, y - 1))
                    stack.push(new int[] { x, y - 1 });
            }
        }
        return cnt;
    }
    private int traceTownOutline(RegionData regionData, Map<String, AreaMarker> newWorldNameMarkerMap, int poly_index,
                                 String infoWindowPopup, String worldName, TileFlags ourShape, int minx, int minz) throws Exception {

        double[] x;
        double[] z;
        /* Trace outline of blocks - start from minx, minz going to x+ */
        int init_x = minx;
        int init_z = minz;
        int cur_x = minx;
        int cur_z = minz;
        direction dir = direction.XPLUS;
        ArrayList<int[]> linelist = new ArrayList<>();
        linelist.add(new int[] { init_x, init_z } ); // Add start point
        while((cur_x != init_x) || (cur_z != init_z) || (dir != direction.ZMINUS)) {
            switch(dir) {
                case XPLUS: /* Segment in X+ direction */
                    if(!ourShape.getFlag(cur_x+1, cur_z)) { /* Right turn? */
                        linelist.add(new int[] { cur_x+1, cur_z }); /* Finish line */
                        dir = direction.ZPLUS;  /* Change direction */
                    }
                    else if(!ourShape.getFlag(cur_x+1, cur_z-1)) {  /* Straight? */
                        cur_x++;
                    }
                    else {  /* Left turn */
                        linelist.add(new int[] { cur_x+1, cur_z }); /* Finish line */
                        dir = direction.ZMINUS;
                        cur_x++; cur_z--;
                    }
                    break;
                case ZPLUS: /* Segment in Z+ direction */
                    if(!ourShape.getFlag(cur_x, cur_z+1)) { /* Right turn? */
                        linelist.add(new int[] { cur_x+1, cur_z+1 }); /* Finish line */
                        dir = direction.XMINUS;  /* Change direction */
                    }
                    else if(!ourShape.getFlag(cur_x+1, cur_z+1)) {  /* Straight? */
                        cur_z++;
                    }
                    else {  /* Left turn */
                        linelist.add(new int[] { cur_x+1, cur_z+1 }); /* Finish line */
                        dir = direction.XPLUS;
                        cur_x++; cur_z++;
                    }
                    break;
                case XMINUS: /* Segment in X- direction */
                    if(!ourShape.getFlag(cur_x-1, cur_z)) { /* Right turn? */
                        linelist.add(new int[] { cur_x, cur_z+1 }); /* Finish line */
                        dir = direction.ZMINUS;  /* Change direction */
                    }
                    else if(!ourShape.getFlag(cur_x-1, cur_z+1)) {  /* Straight? */
                        cur_x--;
                    }
                    else {  /* Left turn */
                        linelist.add(new int[] { cur_x, cur_z+1 }); /* Finish line */
                        dir = direction.ZPLUS;
                        cur_x--; cur_z++;
                    }
                    break;
                case ZMINUS: /* Segment in Z- direction */
                    if(!ourShape.getFlag(cur_x, cur_z-1)) { /* Right turn? */
                        linelist.add(new int[] { cur_x, cur_z }); /* Finish line */
                        dir = direction.XPLUS;  /* Change direction */
                    }
                    else if(!ourShape.getFlag(cur_x-1, cur_z-1)) {  /* Straight? */
                        cur_z--;
                    }
                    else {  /* Left turn */
                        linelist.add(new int[] { cur_x, cur_z }); /* Finish line */
                        dir = direction.XMINUS;
                        cur_x--; cur_z--;
                    }
                    break;
            }
        }
        /* Build information for specific area */
        String polyid = regionData.getName() + "__" + poly_index;
        int sz = linelist.size();
        x = new double[sz];
        z = new double[sz];
        for(int i = 0; i < sz; i++) {
            int[] line = linelist.get(i);
            x[i] = (double)line[0] * (double)16;
            z[i] = (double)line[1] * (double)16;
        }
        /* Find existing one */
        AreaMarker areaMarker = existingAreaMarkers.remove(polyid); /* Existing area? */
        if(areaMarker == null) {
            areaMarker = set.createAreaMarker(polyid, regionData.getName(), false, worldName, x, z, false);
            if(areaMarker == null) {
                areaMarker = set.findAreaMarker(polyid);
                if (areaMarker == null) {
                    throw new Exception("Error adding area marker " + polyid);
                }
            }
        }
        else {
            areaMarker.setCornerLocations(x, z); /* Replace corner locations */
            areaMarker.setLabel(regionData.getName());   /* Update label */
        }
        /* Set popup */
        areaMarker.setDescription(infoWindowPopup);
        /* Set line and fill properties */
        addStyle(regionData, areaMarker);

        /* Fire an event allowing other plugins to alter the AreaMarker */
        RegionRenderEvent renderEvent = new RegionRenderEvent(regionData, areaMarker);
        Bukkit.getPluginManager().callEvent(renderEvent);
        areaMarker = renderEvent.getAreaMarker();

        /* Add to map */
        newWorldNameMarkerMap.put(polyid, areaMarker);
        poly_index++;
        return poly_index;
    }
    private int traceTownOutline(TownData town, Map<String, AreaMarker> newWorldNameMarkerMap, int poly_index,
                                        String infoWindowPopup, String worldName, TileFlags ourShape, int minx, int minz) throws Exception {

        double[] x;
        double[] z;
        /* Trace outline of blocks - start from minx, minz going to x+ */
        int init_x = minx;
        int init_z = minz;
        int cur_x = minx;
        int cur_z = minz;
        direction dir = direction.XPLUS;
        ArrayList<int[]> linelist = new ArrayList<>();
        linelist.add(new int[] { init_x, init_z } ); // Add start point
        while((cur_x != init_x) || (cur_z != init_z) || (dir != direction.ZMINUS)) {
            switch(dir) {
                case XPLUS: /* Segment in X+ direction */
                    if(!ourShape.getFlag(cur_x+1, cur_z)) { /* Right turn? */
                        linelist.add(new int[] { cur_x+1, cur_z }); /* Finish line */
                        dir = direction.ZPLUS;  /* Change direction */
                    }
                    else if(!ourShape.getFlag(cur_x+1, cur_z-1)) {  /* Straight? */
                        cur_x++;
                    }
                    else {  /* Left turn */
                        linelist.add(new int[] { cur_x+1, cur_z }); /* Finish line */
                        dir = direction.ZMINUS;
                        cur_x++; cur_z--;
                    }
                    break;
                case ZPLUS: /* Segment in Z+ direction */
                    if(!ourShape.getFlag(cur_x, cur_z+1)) { /* Right turn? */
                        linelist.add(new int[] { cur_x+1, cur_z+1 }); /* Finish line */
                        dir = direction.XMINUS;  /* Change direction */
                    }
                    else if(!ourShape.getFlag(cur_x+1, cur_z+1)) {  /* Straight? */
                        cur_z++;
                    }
                    else {  /* Left turn */
                        linelist.add(new int[] { cur_x+1, cur_z+1 }); /* Finish line */
                        dir = direction.XPLUS;
                        cur_x++; cur_z++;
                    }
                    break;
                case XMINUS: /* Segment in X- direction */
                    if(!ourShape.getFlag(cur_x-1, cur_z)) { /* Right turn? */
                        linelist.add(new int[] { cur_x, cur_z+1 }); /* Finish line */
                        dir = direction.ZMINUS;  /* Change direction */
                    }
                    else if(!ourShape.getFlag(cur_x-1, cur_z+1)) {  /* Straight? */
                        cur_x--;
                    }
                    else {  /* Left turn */
                        linelist.add(new int[] { cur_x, cur_z+1 }); /* Finish line */
                        dir = direction.ZPLUS;
                        cur_x--; cur_z++;
                    }
                    break;
                case ZMINUS: /* Segment in Z- direction */
                    if(!ourShape.getFlag(cur_x, cur_z-1)) { /* Right turn? */
                        linelist.add(new int[] { cur_x, cur_z }); /* Finish line */
                        dir = direction.XPLUS;  /* Change direction */
                    }
                    else if(!ourShape.getFlag(cur_x-1, cur_z-1)) {  /* Straight? */
                        cur_z--;
                    }
                    else {  /* Left turn */
                        linelist.add(new int[] { cur_x, cur_z }); /* Finish line */
                        dir = direction.XMINUS;
                        cur_x--; cur_z--;
                    }
                    break;
            }
        }
        /* Build information for specific area */
        String polyid = town.getName() + "__" + poly_index;
        int sz = linelist.size();
        x = new double[sz];
        z = new double[sz];
        for(int i = 0; i < sz; i++) {
            int[] line = linelist.get(i);
            x[i] = (double)line[0] * (double)16;
            z[i] = (double)line[1] * (double)16;
        }
        /* Find existing one */
        AreaMarker areaMarker = existingAreaMarkers.remove(polyid); /* Existing area? */
        if(areaMarker == null) {
            areaMarker = set.createAreaMarker(polyid, town.getName(), false, worldName, x, z, false);
            if(areaMarker == null) {
                areaMarker = set.findAreaMarker(polyid);
                if (areaMarker == null) {
                    throw new Exception("Error adding area marker " + polyid);
                }
            }
        }
        else {
            areaMarker.setCornerLocations(x, z); /* Replace corner locations */
            areaMarker.setLabel(town.getName());   /* Update label */
        }
        /* Set popup */
        areaMarker.setDescription(infoWindowPopup);
        /* Set line and fill properties */
        addStyle(town, areaMarker);

        /* Fire an event allowing other plugins to alter the AreaMarker */
        TownRenderEvent renderEvent = new TownRenderEvent(town, areaMarker);
        Bukkit.getPluginManager().callEvent(renderEvent);
        areaMarker = renderEvent.getAreaMarker();

        /* Add to map */
        newWorldNameMarkerMap.put(polyid, areaMarker);
        poly_index++;
        return poly_index;
    }

    private void addStyle(TownData town, AreaMarker m) {
        AreaStyle as = townAreaStyle;	/* Look up custom style for town, if any */
        AreaStyle ns = regionAreaStyle;	/* Look up nation style, if any */

        m.setLineStyle(townAreaStyle.getBaseStrokeWeight(), townAreaStyle.getStrokeOpacity(), town.getChunkColor());
        m.setFillStyle(townAreaStyle.getFillOpacity(), town.getChunkColor());

        m.setRangeY(16, 16);
        //m.setBoostFlag(defstyle.getBoost(as, ns));

    }
    private void addStyle(RegionData region, AreaMarker m) {
        AreaStyle as = townAreaStyle;	/* Look up custom style for region, if any */
        AreaStyle ns = regionAreaStyle;	/* Look up nation style, if any */

        m.setLineStyle(townAreaStyle.getBaseStrokeWeight(), townAreaStyle.getStrokeOpacity(), region.getChunkColor());
        m.setFillStyle(townAreaStyle.getFillOpacity(), region.getChunkColor());

        m.setRangeY(16, 16);
        //m.setBoostFlag(defstyle.getBoost(as, ns));

    }
}
