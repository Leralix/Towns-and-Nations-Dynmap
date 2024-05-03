package org.leralix.towns_and_nations_dynmap.Style;

import org.bukkit.configuration.file.FileConfiguration;

public class TownChunkStyle {
    String strokecolor;
    double strokeopacity;
    int strokeweight;
    String fillcolor;
    double fillopacity;
    String label;

    public TownChunkStyle(FileConfiguration cfg, String path) {
        strokecolor = cfg.getString(path+".strokeColor", "#FF0000");
        strokeopacity = cfg.getDouble(path+".strokeOpacity", 0.8);
        strokeweight = cfg.getInt(path+".strokeWeight", 3);
        fillcolor = cfg.getString(path+".fillColor", "#FF0000");
        fillopacity = cfg.getDouble(path+".fillOpacity", 0.35);
    }
}