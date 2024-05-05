package org.leralix.towns_and_nations_dynmap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import org.leralix.towns_and_nations_dynmap.Storage.*;
import org.leralix.towns_and_nations_dynmap.commands.CommandManager;
import org.tan.TownsAndNations.Bstats.Metrics;
import org.tan.TownsAndNations.DataClass.RegionData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.DataClass.newChunkData.ClaimedChunk2;
import org.tan.TownsAndNations.TownsAndNations;
import org.tan.TownsAndNations.storage.DataStorage.RegionDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;


public final class TownsAndNations_Dynmap extends JavaPlugin {

    private final int BSTAT_ID = 20740;
    private static TownsAndNations_Dynmap plugin;
    Logger logger = this.getLogger();
    PluginManager pm = getServer().getPluginManager();
    private static MarkerAPI markerAPI;
    private MarkerSet set;
    private boolean reload = false;
    private static Map<String, AreaMarker> resareas = new HashMap<>();
    long update_period;
    ChunkManager chunkManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        //reloadConfig();
        //saveConfig();
        // Plugin startup logic
        plugin = this;

        logger.info("[TaN - Dynmap] -Loading Plugin");
        new Metrics(this, BSTAT_ID);

        //get Dynmap
        Plugin dynmap = pm.getPlugin("dynmap");
        if (dynmap == null || !dynmap.isEnabled()) {
            logger.severe("Cannot find dynmap, check your logs to see if it enabled properly?!");
            return;
        }

        //Get T&N
        Plugin TaN = pm.getPlugin("TownsAndNations");
        if (TaN == null || !TaN.isEnabled()) {
            logger.severe("Cannot find Towns and Nations, check your logs to see if it enabled properly?!");
            return;
        }
        TownsAndNations.setDynmapAddonLoaded(true);

        DynmapAPI dynmapAPI = (DynmapAPI) dynmap;
        TownsAndNations TanApi = (TownsAndNations) TaN;



        Objects.requireNonNull(getCommand("tanmap")).setExecutor(new CommandManager());

        initialise(dynmapAPI);
    }

    private void initialise(DynmapAPI dynmapAPI) {
        markerAPI = dynmapAPI.getMarkerAPI();

        if(markerAPI == null) {
            getLogger().severe("Error loading dynmap marker API!");
            return;
        }


        FileConfiguration cfg = getConfig();
        cfg.options().copyDefaults(true);
        this.saveConfig();

        set = markerAPI.getMarkerSet("townsandnations.markerset");
        if(set == null)
            set = markerAPI.createMarkerSet("townsandnations.markerset", cfg.getString("layer.name", "Towns and Nations"), null, false);
        else
            set.setMarkerSetLabel(cfg.getString("layer.name", "Town and Nations"));


        int minZoom = cfg.getInt("layer.minimum_zoom", 0);
        if(minZoom > 0)
            set.setMinZoom(minZoom);

        set.setLayerPriority(cfg.getInt("layer.layer_priority", 10));
        set.setHideByDefault(cfg.getBoolean("layer.hide_by_default", false));

        for (AreaMarker areaMarker : set.getAreaMarkers()){
            areaMarker.deleteMarker();
        }


        /* Set up update job - based on periond */
        int per = cfg.getInt("update.period", 300);
        if(per < 15) per = 15;
        update_period = per* 20L;

        chunkManager = new ChunkManager(set);

        for(TownData townData : TownDataStorage.getTownMap().values()){
            TownDescription townDescription = new TownDescription(townData);
            TownDescriptionStorage.add(townDescription);
        }

        for(RegionData regionData : RegionDataStorage.getAllRegions()){
            RegionDescription regionDescription = new RegionDescription(regionData);
            RegionDescriptionStorage.add(regionDescription);
        }

        getServer().getScheduler().scheduleSyncDelayedTask(this, new Update(), 40);   /* First time is 2 seconds */
    }

    private class Update implements Runnable {
        public void run() {
            Update();
        }
    }

    public void Update() {
        chunkManager.clear();

        for(ClaimedChunk2 chunk : TownsAndNations.getAPI().getChunkList()) {
            chunkManager.add(chunk);
        }

        getServer().getScheduler().scheduleSyncDelayedTask(this, new Update(), update_period);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static TownsAndNations_Dynmap getPlugin(){
        return plugin;
    }

    public static Logger getPluginLogger() {
        return plugin.getLogger();
    }

    public MarkerAPI getMarkerAPI(){
        return markerAPI;
    }

}


