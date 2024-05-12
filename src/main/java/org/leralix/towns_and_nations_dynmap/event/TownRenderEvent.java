package org.leralix.towns_and_nations_dynmap.event;

import com.avaje.ebean.validation.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.dynmap.markers.AreaMarker;
import org.tan.TownsAndNations.DataClass.TownData;

public class TownRenderEvent extends Event {
    private static HandlerList handlers = new HandlerList();
    private final TownData town;
    private final AreaMarker areaMarker;

    public TownRenderEvent(TownData town, AreaMarker areaMarker) {
        super(!Bukkit.getServer().isPrimaryThread());
        this.town = town;
        this.areaMarker = areaMarker;
    }

    public TownData getTown() {
        return town;
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