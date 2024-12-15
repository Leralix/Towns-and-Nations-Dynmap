package org.leralix.tancommon.update;

import org.bukkit.plugin.Plugin;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tancommon.markers.CommonAreaMarker;
import org.leralix.tancommon.storage.*;
import org.leralix.tancommon.TownsAndNationsMapCommon;

import java.util.HashMap;
import java.util.Map;

public class UpdateChunks implements Runnable {

    final Map<String, CommonAreaMarker> chunkMap;
    final ChunkManager chunkManager;
    final Long updatePeriod;

    public UpdateChunks(ChunkManager chunkManager, long updatePeriod) {
        this.chunkManager = chunkManager;
        this.updatePeriod = updatePeriod;
        this.chunkMap = new HashMap<>();
    }

    public UpdateChunks(UpdateChunks copy) {
        this.chunkManager = copy.chunkManager;
        this.updatePeriod = copy.updatePeriod;
        this.chunkMap = new HashMap<>();
    }

    @Override
    public void run() {
        update();
    }


    public void update() {

        //Reset old markers
        for (CommonAreaMarker areaMarker : chunkMap.values()){
            areaMarker.deleteMarker();
        }

        //Update town and regions descriptions
        for(TownData townData : TownDataStorage.getTownMap().values()){
            TownDescription townDescription = new TownDescription(townData);
            TownDescriptionStorage.add(townDescription);
        }

        for(RegionData regionData : RegionDataStorage.getAllRegions()){
            RegionDescription regionDescription = new RegionDescription(regionData);
            RegionDescriptionStorage.add(regionDescription);
        }


        for(TownData townData : TownDataStorage.getTownMap().values()){
            chunkManager.updateTown(townData, chunkMap);
        }

        for(RegionData regionData : RegionDataStorage.getAllRegions()){
            chunkManager.updateRegion(regionData, chunkMap);
        }

        Plugin plugin = TownsAndNationsMapCommon.getPlugin();
        if(updatePeriod > 0)
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new UpdateChunks(this), updatePeriod);

    }
}
