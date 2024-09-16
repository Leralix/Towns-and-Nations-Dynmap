package org.leralix.towns_and_nations_dynmap.event;

import com.avaje.ebean.validation.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.dynmap.markers.AreaMarker;
import org.tan.TownsAndNations.DataClass.territoryData.RegionData;

public class RegionRenderEvent extends Event {
    private static HandlerList handlers = new HandlerList();
    private final RegionData regionData;
    private final AreaMarker areaMarker;

    public RegionRenderEvent(RegionData town, AreaMarker areaMarker) {
        super(!Bukkit.getServer().isPrimaryThread());
        this.regionData = town;
        this.areaMarker = areaMarker;
    }

    public RegionData getRegion() {
        return regionData;
    }

    public AreaMarker getAreaMarker() {
        return areaMarker;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}