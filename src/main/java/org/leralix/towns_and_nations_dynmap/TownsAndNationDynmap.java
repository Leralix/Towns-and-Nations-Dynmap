package org.leralix.towns_and_nations_dynmap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.*;
import org.tan.TownsAndNations.TownsAndNations;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;


public final class TownsAndNationDynmap extends JavaPlugin {

    private static TownsAndNationDynmap plugin;

    private static final String DEF_INFOWINDOW = "<div class=\"infowindow\"><span style=\"font-size:120%;\">%regionname%</span><br /> Owners <span style=\"font-weight:bold;\">%playerowners%</span><br/>Members <span style=\"font-weight:bold;\">%playermembers%</span><br/>Flags<br /><span style=\"font-weight:bold;\">%flags%</span></div>";

    Logger logger = this.getLogger();
    PluginManager pm = getServer().getPluginManager();

    private static MarkerAPI markerAPI;
    private MarkerSet set;
    private boolean reload = false;
    private Map<String, AreaMarker> resareas = new HashMap<>();
    boolean use3d;
    String infowindow;
    int maxdepth;
    long updperiod;
    AreaStyle defstyle;
    Map<String, AreaStyle> cusstyle;
    Map<String, AreaStyle> cuswildstyle;
    Map<String, AreaStyle> ownerstyle;
    Set<String> visible;
    Set<String> hidden;
    boolean stop;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        Plugin dynmap;
        Plugin TaN;

        logger.info("\u001B[33m--------------Towns & Nations Dynmap----------------\u001B[0m");


        logger.info("[TaN] -Loading Plugin");

        //get Dynmap
        dynmap = pm.getPlugin("dynmap");
        if (dynmap == null || !dynmap.isEnabled()) {
            logger.severe("Cannot find dynmap, check your logs to see if it enabled properly?!");
            return;
        }

        //Get T&N
        TaN = pm.getPlugin("TownsAndNations");
        if (TaN == null || !TaN.isEnabled()) {
            logger.severe("Cannot find Towny, check your logs to see if it enabled properly?!");
            return;
        }

        DynmapAPI dynmapAPI = (DynmapAPI) dynmap;
        TownsAndNations TanApi = (TownsAndNations) TaN;

        logger.info("Nom du plugin tan: " + TanApi.getName());
        logger.info("Nom du plugin dynmap: " + dynmapAPI.getDynmapVersion());


        initialise(dynmapAPI, TanApi);












        getLogger().info("\u001B[33m--------------Towns & Nations Dynmap----------------\u001B[0m");

    }

    private void initialise(DynmapAPI dynmapAPI, TownsAndNations TanApi) {
        markerAPI = dynmapAPI.getMarkerAPI();

        if(markerAPI == null) {
            getLogger().severe("Error loading dynmap marker API!");
            return;
        }
        /* Load configuration */
        if(reload) {
            reloadConfig();
            if(set != null) {
                set.deleteMarkerSet();
                set = null;
            }
            resareas.clear();
        }
        else {
            reload = true;
        }

        FileConfiguration cfg = getConfig();
        cfg.options().copyDefaults(true);   /* Load defaults, if needed */
        this.saveConfig();  /* Save updates, if needed */

        set = markerAPI.getMarkerSet("townsandnations.markerset");
        if(set == null)
            set = markerAPI.createMarkerSet("preciousstones.markerset", cfg.getString("layer.name", "townsandnations"), null, false);
        else
            set.setMarkerSetLabel(cfg.getString("layer.name", "PreciousStones"));

        if(set == null) {
            getLogger().severe("Error creating marker set");
            return;
        }
        int minzoom = cfg.getInt("layer.minzoom", 0);
        if(minzoom > 0)
            set.setMinZoom(minzoom);

        set.setLayerPriority(cfg.getInt("layer.layerprio", 10));
        set.setHideByDefault(cfg.getBoolean("layer.hidebydefault", false));
        use3d = cfg.getBoolean("use3dfields", false);
        infowindow = cfg.getString("infowindow", DEF_INFOWINDOW);
        maxdepth = cfg.getInt("maxdepth", 16);

        /* Get style information */
        defstyle = new AreaStyle(cfg, "fieldstyle");
        cusstyle = new HashMap<>();
        ownerstyle = new HashMap<>();
        cuswildstyle = new HashMap<>();
        ConfigurationSection sect = cfg.getConfigurationSection("custstyle");
        if(sect != null) {
            Set<String> ids = sect.getKeys(false);

            for(String id : ids) {
                if(id.indexOf('|') >= 0)
                    cuswildstyle.put(id, new AreaStyle(cfg, "custstyle." + id, defstyle));
                else
                    cusstyle.put(id, new AreaStyle(cfg, "custstyle." + id, defstyle));
            }
        }
        sect = cfg.getConfigurationSection("ownerstyle");
        if(sect != null) {
            Set<String> ids = sect.getKeys(false);

            for(String id : ids) {
                ownerstyle.put(id.toLowerCase(), new AreaStyle(cfg, "ownerstyle." + id, defstyle));
            }
        }
        List<String> vis = cfg.getStringList("visiblefields");
        if(vis != null) {
            visible = new HashSet<>(vis);
        }
        List<String> hid = cfg.getStringList("hiddenfields");
        if(hid != null) {
            hidden = new HashSet<>(hid);
        }

        /* Set up update job - based on periond */
        int per = cfg.getInt("update.period", 300);
        if(per < 15) per = 15;
        updperiod = per* 20L;
        stop = false;

        getServer().getScheduler().scheduleSyncDelayedTask(this, new PreciousStonesUpdate(), 40);   /* First time is 2 seconds */

        getLogger().info("version " + this.getDescription().getVersion() + " is activated");

        MarkerIcon icon = markerAPI.getMarkerIcon("building");
        String htmlLabel = "<div>Hello World</div>";
        set.createMarker("uniqueMarkerId", htmlLabel, true,
                "world", 10, 20, 30, icon, false);

    }

    private class PreciousStonesUpdate implements Runnable {
        public void run() {
            if(!stop)
                updateForceFields();
        }
    }

    private void updateForceFields() {
        Map<String,AreaMarker> newmap = new HashMap<>(); /* Build new map */

        double[] x = new double[] { 1, 10 };
        double[] z = new double[] { 1, 10 };


        String markerid = "test";
        AreaMarker m = resareas.remove(markerid); /* Existing area? */
        if(m == null) {
            m = set.createAreaMarker(markerid, "name", false, Bukkit.getWorlds().get(0).getName(), x, z, false);
            if(m == null)
                return;
        }
        else {
            m.setCornerLocations(x, z); /* Replace corner locations */
            m.setLabel("newname");   /* Update label */
        }

        newmap.put("test", null);


        /* Now, review old map - anything left is gone */
        for(AreaMarker oldm : resareas.values()) {
            oldm.deleteMarker();
        }
        /* Replace with new map */
        resareas = newmap;

        /* And schedule next update */
        getServer().getScheduler().scheduleSyncDelayedTask(this, new PreciousStonesUpdate(), updperiod);

    }

    private void handleField(World world, Field field, Map<String, AreaMarker> newmap) {

    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static TownsAndNationDynmap getPlugin(){
        return plugin;
    }

    public static Logger getPluginLogger() {
        return plugin.getLogger();
    }


}


