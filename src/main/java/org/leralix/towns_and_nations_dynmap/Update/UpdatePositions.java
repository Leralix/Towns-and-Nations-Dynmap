package org.leralix.towns_and_nations_dynmap.Update;

import org.bukkit.plugin.Plugin;
import org.dynmap.markers.AreaMarker;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tan.storage.stored.RegionDataStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.towns_and_nations_dynmap.Storage.*;
import org.leralix.towns_and_nations_dynmap.TownsAndNations_Dynmap;

import java.util.HashMap;
import java.util.Map;

public class UpdatePositions implements Runnable {

    Map<String, AreaMarker> newmap;
    ChunkManager chunkManager;
    Long update_period;

    public UpdatePositions(ChunkManager chunkManager, long update_period) {
        this.chunkManager = chunkManager;
        this.update_period = update_period;
        this.newmap = new HashMap<>();
    }

    public UpdatePositions(UpdatePositions copy) {
        this.chunkManager = copy.chunkManager;
        this.update_period = copy.update_period;
        this.newmap = new HashMap<>();
    }

    @Override
    public void run() {
        update();
    }


    public void update() {

        //Update town and regions descriptions
        for(TownData townData : TownDataStorage.getTownMap().values()){
            TownDescription townDescription = new TownDescription(townData);
            TownDescriptionStorage.add(townDescription);
        }

        for(RegionData regionData : RegionDataStorage.getAllRegions()){
            RegionDescription regionDescription = new RegionDescription(regionData);
            RegionDescriptionStorage.add(regionDescription);
        }


        //Reset old markers
        for (AreaMarker areaMarker : newmap.values()){
            areaMarker.deleteMarker();
        }

        for(TownData townData : TownDataStorage.getTownMap().values()){
            chunkManager.updateTown(townData, newmap);
        }

        for(RegionData regionData : RegionDataStorage.getAllRegions()){
            chunkManager.updateRegion(regionData, newmap);
        }

        Plugin plugin = TownsAndNations_Dynmap.getPlugin();
        if(update_period > 0)
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new UpdatePositions(this), update_period);

    }
}
