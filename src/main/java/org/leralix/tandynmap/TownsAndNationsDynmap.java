package org.leralix.tandynmap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.PluginVersion;
import org.leralix.tandynmap.bstat.Metrics;
import org.leralix.tandynmap.storage.ChunkManager;
import org.leralix.tandynmap.update.UpdateLandMarks;
import org.leralix.tandynmap.update.UpdateChunks;
import org.leralix.tandynmap.commands.CommandManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;


public class TownsAndNationsDynmap extends JavaPlugin {

    private final int BSTAT_ID = 20740;
    private static TownsAndNationsDynmap plugin;
    Logger logger = this.getLogger();
    private MarkerAPI markerAPI;
    private long updatePeriod;
    private final PluginVersion pluginVersion = new PluginVersion(0,10 ,0);
    private final Map<String, AreaMarker> areaMarkers = new HashMap<>();


    UpdateLandMarks updateLandMarks;
    UpdateChunks updateChunks;
    @Override
    public void onEnable() {
        saveDefaultConfig();
        // Plugin startup logic
        plugin = this;

        logger.info("[TaN - Dynmap] -Loading Plugin");
        new Metrics(this, BSTAT_ID);

        //get Dynmap
        PluginManager pm = getServer().getPluginManager();
        Plugin dynmap = pm.getPlugin("dynmap");
        if (dynmap == null || !dynmap.isEnabled()) {
            logger.severe("Cannot find dynmap, check your logs to see if it enabled properly?!");
            setEnabled(false);
            return;
        }

        //Get T&N
        Plugin tanPlugin = pm.getPlugin("TownsAndNations");
        if (tanPlugin == null || !tanPlugin.isEnabled()) {
            logger.severe("Cannot find Towns and Nations, check your logs to see if it enabled properly?!");
            setEnabled(false);
            return;
        }
        PluginVersion minTanVersion = TownsAndNations.getPlugin().getMinimumSupportingDynmap();
        if(pluginVersion.isOlderThan(minTanVersion)){
            logger.severe("Towns and Nations is not compatible with this version of Dynmap (minimum version: " + minTanVersion + ")");
            setEnabled(false);
            return;
        }

        TownsAndNations.getPlugin().setDynmapAddonLoaded(true);

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

        /* Set up update job - based on periond */
        int per = getConfig().getInt("update.period", 300);
        if(per < 15) per = 15;
        updatePeriod = per* 20L;

        initialiseClaimedChunks(markerAPI);
        initialiseLandmarks(markerAPI);
    }

    private void initialiseClaimedChunks(MarkerAPI markerAPI) {


        FileConfiguration cfg = getConfig();
        cfg.options().copyDefaults(true);
        this.saveConfig();

        MarkerSet set = markerAPI.getMarkerSet("townsandnations.chunks");
        if(set == null)
            set = markerAPI.createMarkerSet("townsandnations.chunks", cfg.getString("chunk_layer.name", "Towns and Nations"), null, false);
        else
            set.setMarkerSetLabel(cfg.getString("chunk_layer.name", "Towns and Nations"));


        int minZoom = cfg.getInt("chunk_layer.minimum_zoom", 0);
        if(minZoom > 0)
            set.setMinZoom(minZoom);

        set.setLayerPriority(cfg.getInt("chunk_layer.priority", 10));
        set.setHideByDefault(cfg.getBoolean("chunk_layer.hide_by_default", false));

        for (AreaMarker areaMarker : set.getAreaMarkers()){
            areaMarker.deleteMarker();
        }


        updateChunks = new UpdateChunks(new ChunkManager(set), updatePeriod);
        getServer().getScheduler().scheduleSyncDelayedTask(this, updateChunks, 40);
    }

    private void initialiseLandmarks(MarkerAPI markerAPI) {
        FileConfiguration cfg = getConfig();
        cfg.options().copyDefaults(true);
        this.saveConfig();

        MarkerSet set2 = markerAPI.getMarkerSet("townsandnations.landmarks");
        if(set2 == null)
            set2 = markerAPI.createMarkerSet("townsandnations.landmarks", cfg.getString("landmark_layer.name", "Landmarks"), null, false);
        else
            set2.setMarkerSetLabel(cfg.getString("landmark_layer.name", "Landmarks"));

        int minZoom = cfg.getInt("landmark_layer.minimum_zoom", 0);
        if(minZoom > 0)
            set2.setMinZoom(minZoom);

        set2.setLayerPriority(cfg.getInt("landmark_layer.priority", 10));
        set2.setHideByDefault(cfg.getBoolean("landmark_layer.hide_by_default", false));

        for (AreaMarker areaMarker : set2.getAreaMarkers()){
            areaMarker.deleteMarker();
        }

        updateLandMarks = new UpdateLandMarks(set2, updatePeriod);
        getServer().getScheduler().scheduleSyncDelayedTask(this, updateLandMarks, 40);
    }


    @Override
    public void onDisable() {
        // lol
    }

    public static TownsAndNationsDynmap getPlugin(){
        return plugin;
    }

        public Map<String, AreaMarker> getAreaMarkers() {
        return areaMarkers;
    }

    public void updateDynmap() {
        updateChunks.update();
        updateLandMarks.update();
    }

    public MarkerAPI getMarkerAPI() {
        return this.markerAPI;
    }
}


