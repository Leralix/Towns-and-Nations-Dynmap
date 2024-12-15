package org.leralix.tandynmap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.leralix.tan.TownsAndNations;
import org.leralix.tan.dataclass.PluginVersion;
import org.leralix.tandynmap.bstat.Metrics;
import org.leralix.tandynmap.commands.CommandManager;
import org.leralix.tandynmap.markers.CommonAreaMarker;
import org.leralix.tandynmap.markers.CommonMarkerAPI;
import org.leralix.tandynmap.markers.CommonMarkerSet;
import org.leralix.tandynmap.storage.ChunkManager;
import org.leralix.tandynmap.update.UpdateChunks;
import org.leralix.tandynmap.update.UpdateLandMarks;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class TownsAndNationsMapCommon extends JavaPlugin {

    private static TownsAndNationsMapCommon plugin;
    Logger logger = this.getLogger();
    private CommonMarkerAPI markerAPI;
    private long updatePeriod;
    private final PluginVersion pluginVersion = new PluginVersion(0,10 ,0);
    private final Map<String, CommonAreaMarker> areaMarkers = new HashMap<>();

    UpdateLandMarks updateLandMarks;
    UpdateChunks updateChunks;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        // Plugin startup logic
        plugin = this;

        logger.info("[Tanmap : " + getSubMapName() + "] -Loading Plugin");
        new Metrics(this, getBStatID());


        PluginManager pm = getServer().getPluginManager();
        //Get T&N
        Plugin tanPlugin = pm.getPlugin("TownsAndNations");
        if (tanPlugin == null || !tanPlugin.isEnabled()) {
            logger.severe("Cannot find Towns and Nations, check your logs to see if it enabled properly?!");
            setEnabled(false);
            return;
        }

        //get specific plugin
        Plugin specificMapPlugin = pm.getPlugin(getSubMapName());
        if (specificMapPlugin == null || !specificMapPlugin.isEnabled()) {
            logger.severe("Cannot find " + getSubMapName() + ", check your logs to see if it enabled properly?!");
            setEnabled(false);
            return;
        }
        PluginVersion minTanVersion = TownsAndNations.getPlugin().getMinimumSupportingDynmap();
        if(pluginVersion.isOlderThan(minTanVersion)){
            logger.log(Level.SEVERE,"Towns and Nations is not compatible with this version of tanmap (minimum version: {0})", minTanVersion);
            setEnabled(false);
            return;
        }

        Objects.requireNonNull(getCommand("tanmap")).setExecutor(new CommandManager());


        initialise(specificMapPlugin);

        logger.info("[TaN - Dynmap] -Towns and Nations - Dynmap is running");

    }

    private void initialise(Plugin mapPlugin) {
        markerAPI = createMarkerAPI(mapPlugin);

        if (!markerAPI.isWorking()) {
            getLogger().severe("Error loading dynmap marker API!");
            return;
        }

        int per = getConfig().getInt("update.period", 300);
        if(per < 15) per = 15;
        updatePeriod = per * 20L;

        initialiseClaimedChunks(markerAPI);
        initialiseLandmarks(markerAPI);
    }

    private void initialiseClaimedChunks(CommonMarkerAPI markerAPI) {


        FileConfiguration cfg = getConfig();
        cfg.options().copyDefaults(true);
        this.saveConfig();

        CommonMarkerSet set = markerAPI.getMarkerSet("townsandnations.chunks");
        if(set == null)
            set = markerAPI.createMarkerSet("townsandnations.chunks", cfg.getString("chunk_layer.name", "Towns and Nations"));
        else
            set.setMarkerSetLabel(cfg.getString("chunk_layer.name", "Towns and Nations"));


        int minZoom = cfg.getInt("chunk_layer.minimum_zoom", 0);
        if(minZoom > 0)
            set.setMinZoom(minZoom);

        set.setLayerPriority(cfg.getInt("chunk_layer.priority", 10));
        set.setHideByDefault(cfg.getBoolean("chunk_layer.hide_by_default", false));

        set.deleteAllMarkers();

        updateChunks = new UpdateChunks(new ChunkManager(set), updatePeriod);
        getServer().getScheduler().scheduleSyncDelayedTask(this, updateChunks, 40);
    }

    private void initialiseLandmarks(CommonMarkerAPI markerAPI) {
        FileConfiguration cfg = getConfig();
        cfg.options().copyDefaults(true);
        this.saveConfig();

        CommonMarkerSet set = markerAPI.getMarkerSet("townsandnations.landmarks");
        if(set == null)
            set = markerAPI.createMarkerSet("townsandnations.landmarks", cfg.getString("landmark_layer.name", "Landmarks"));
        else
            set.setMarkerSetLabel(cfg.getString("landmark_layer.name", "Landmarks"));

        int minZoom = cfg.getInt("landmark_layer.minimum_zoom", 0);
        if(minZoom > 0)
            set.setMinZoom(minZoom);

        set.setLayerPriority(cfg.getInt("landmark_layer.priority", 10));
        set.setHideByDefault(cfg.getBoolean("landmark_layer.hide_by_default", false));

        set.deleteAllMarkers();



        updateLandMarks = new UpdateLandMarks(set, updatePeriod);
        getServer().getScheduler().scheduleSyncDelayedTask(this, updateLandMarks, 40);
    }


    @Override
    public void onDisable() {
        // lol
    }

    public static TownsAndNationsMapCommon getPlugin(){
        return plugin;
    }

    public Map<String, CommonAreaMarker> getAreaMarkers() {
        return areaMarkers;
    }

    public void updateDynmap() {
        updateChunks.update();
        updateLandMarks.update();
    }

    public CommonMarkerAPI getMarkerAPI() {
        return this.markerAPI;
    }

    protected abstract String getSubMapName();

    protected abstract int getBStatID();

    protected abstract CommonMarkerAPI createMarkerAPI(Plugin markerAPI);

}


