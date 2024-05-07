package org.leralix.towns_and_nations_dynmap.Storage;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerSet;
import org.leralix.towns_and_nations_dynmap.Style.ChunkStyle;
import org.tan.TownsAndNations.DataClass.newChunkData.ClaimedChunk2;
import org.tan.TownsAndNations.DataClass.newChunkData.RegionClaimedChunk;
import org.tan.TownsAndNations.DataClass.newChunkData.TownClaimedChunk;
import org.tan.TownsAndNations.TownsAndNations;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChunkManager {

    private final Map<String, AreaMarker> AreaMap = new HashMap<>();
    private final MarkerSet set;
    private final ChunkStyle townChunkStyle;
    private final ChunkStyle regionChunkStyle;

    public ChunkManager(MarkerSet set) {
        this.set = set;

        this.townChunkStyle = new ChunkStyle(TownsAndNations.getPlugin().getConfig(), "town_fieldStyle");
        this.regionChunkStyle = new ChunkStyle(TownsAndNations.getPlugin().getConfig(), "region_fieldStyle");
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
            areamarker.setDescription(description);

            int strokeWeight = townChunkStyle.getBaseStrokeWeight();
            double strokeOpacity = townChunkStyle.getStrokeOpacity();
            double fillOpacity = townChunkStyle.getFillOpacity();

            areamarker.setLineStyle(strokeWeight, strokeOpacity, color);
            areamarker.setFillStyle(fillOpacity, color);
        }
        else if (claimedChunk instanceof RegionClaimedChunk regionClaimedChunk) {
            String description = RegionDescriptionStorage.get(regionClaimedChunk.getOwnerID()).getChunkDescription();

            int strokeWeight = townChunkStyle.getBaseStrokeWeight();
            double strokeOpacity = townChunkStyle.getStrokeOpacity();
            double fillOpacity = townChunkStyle.getFillOpacity();

            areamarker.setDescription(description);
            areamarker.setLineStyle(strokeWeight, strokeOpacity, color);
            areamarker.setFillStyle(fillOpacity, color);
        }



        AreaMap.put(markerID, areamarker);

    }


    public void clear() {
        /* Remove all chunks colored */
        for(AreaMarker oldArea : AreaMap.values()) {
            oldArea.deleteMarker();
        }
        AreaMap.clear();
    }
}
