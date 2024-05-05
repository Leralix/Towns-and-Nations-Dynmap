package org.leralix.towns_and_nations_dynmap.Storage;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerSet;
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

    public ChunkManager(MarkerSet set) {
        this.set = set;
    }

    public void add(ClaimedChunk2 claimedChunk) {

        String markerID = claimedChunk.getX() + "_" + claimedChunk.getZ() + "_" + claimedChunk.getWorldUUID();
        String worldName = Bukkit.getWorld(UUID.fromString(claimedChunk.getWorldUUID())).getName();
        double[] x = new double[] { claimedChunk.getX()*16, claimedChunk.getX()*16 + 16 };
        double[] z = new double[] { claimedChunk.getZ()*16, claimedChunk.getZ()*16 + 16 };
        int color = TownsAndNations.getAPI().getChunkColor(claimedChunk);

        String description = "Name not found";
        if(claimedChunk instanceof TownClaimedChunk townChunk) {
            description = TownDescriptionStorage.get(townChunk.getOwnerID()).getChunkDescription();
        }
        else if (claimedChunk instanceof RegionClaimedChunk regionClaimedChunk) {
            description = regionClaimedChunk.getName();
        }


        AreaMarker areamarker = set.createAreaMarker(markerID, "Description not found", false, worldName, x, z, false);
        areamarker.setLineStyle(1, 1, color);
        areamarker.setFillStyle(0.4, color);
        areamarker.setDescription(description);


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
