package org.leralix.towns_and_nations_dynmap.Storage;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerSet;
import org.tan.TownsAndNations.DataClass.newChunkData.ClaimedChunk2;
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

    public void add(ClaimedChunk2 chunk) {

        String markerID = chunk.getX() + "_" + chunk.getZ() + "_" + chunk.getWorldUUID();
        String worldName = Bukkit.getWorld(UUID.fromString(chunk.getWorldUUID())).getName();
        double[] x = new double[] { chunk.getX()*16, chunk.getX()*16 + 16 };
        double[] z = new double[] { chunk.getZ()*16, chunk.getZ()*16 + 16 };
        int color = TownsAndNations.getAPI().getChunkColor(chunk);

        AreaMarker areamarker = set.createAreaMarker(markerID, chunk.getName() + "TETSTTTs", false, worldName, x, z, false);
        areamarker.setLineStyle(1, 1, color);
        areamarker.setFillStyle(0.4, color);

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
