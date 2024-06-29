package org.leralix.towns_and_nations_dynmap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import org.leralix.towns_and_nations_dynmap.Bstat.Metrics;
import org.leralix.towns_and_nations_dynmap.Storage.ChunkManager;
import org.leralix.towns_and_nations_dynmap.Update.UpdateLandMarks;
import org.leralix.towns_and_nations_dynmap.Update.UpdatePositions;
import org.leralix.towns_and_nations_dynmap.commands.CommandManager;
import org.tan.TownsAndNations.TownsAndNations;

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
    long update_period;
    ChunkManager chunkManager;
    private final Map<String, AreaMarker> areaMarkers = new HashMap<>();


    UpdateLandMarks updateLandMarks;
    UpdatePositions updatePositions;
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


        Objects.requireNonNull(getCommand("tanmap")).setExecutor(new CommandManager());

        initialise(dynmapAPI);
        logger.info("[TaN - Dynmap] -Towns and Nations - Dynmap is running");

    }

    private void initialise(DynmapAPI dynmapAPI) {
        markerAPI = dynmapAPI.getMarkerAPI();

        if (markerAPI == null) {
            getLogger().severe("Error loading dynmap marker API!");
            return;
        }

        initialiseClaimedChunks(markerAPI);
        initialiseLandmarks(markerAPI);
    }

    private void initialiseClaimedChunks(MarkerAPI markerAPI) {


        FileConfiguration cfg = getConfig();
        cfg.options().copyDefaults(true);
        this.saveConfig();

        MarkerSet set = markerAPI.getMarkerSet("townsandnations.markerset");
        if(set == null)
            set = markerAPI.createMarkerSet("townsandnations.markerset", cfg.getString("layer.name", "Towns and Nations"), null, false);
        else
            set.setMarkerSetLabel(cfg.getString("layer.name", "Towns and Nations"));


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
        updatePositions = new UpdatePositions(chunkManager, update_period);
        getServer().getScheduler().scheduleSyncDelayedTask(this, updatePositions, 40);
    }

    private void initialiseLandmarks(MarkerAPI markerAPI) {
        FileConfiguration cfg = getConfig();
        cfg.options().copyDefaults(true);
        this.saveConfig();

        MarkerSet set2 = markerAPI.getMarkerSet("townsandnations.landmarks");
        if(set2 == null)
            set2 = markerAPI.createMarkerSet("townsandnations.landmarks", cfg.getString("layer.name", "Landmarks"), null, false);
        else
            set2.setMarkerSetLabel(cfg.getString("layer2.name", "Landmarks"));

        int minZoom = cfg.getInt("layer.minimum_zoom", 0);
        if(minZoom > 0)
            set2.setMinZoom(minZoom);

        set2.setLayerPriority(cfg.getInt("layer2.layer_priority", 10));
        set2.setHideByDefault(cfg.getBoolean("layer2.hide_by_default", false));

        for (AreaMarker areaMarker : set2.getAreaMarkers()){
            areaMarker.deleteMarker();
        }
        int per = cfg.getInt("update.period", 300);
        if(per < 15) per = 15;
        update_period = per* 20L;

        updateLandMarks = new UpdateLandMarks(set2, update_period);
        getServer().getScheduler().scheduleSyncDelayedTask(this, updateLandMarks, 40);
    }


    @Override
    public void onDisable() {
        // lol
    }

    public static TownsAndNations_Dynmap getPlugin(){
        return plugin;
    }

        public Map<String, AreaMarker> getAreaMarkers() {
        return areaMarkers;
    }

    public void updateDynmap() {
        updatePositions.update();
        updateLandMarks.update();
    }

    public static MarkerAPI getMarkerAPI() {
        return markerAPI;
    }
}


