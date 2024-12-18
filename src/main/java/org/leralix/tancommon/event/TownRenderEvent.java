package org.leralix.tancommon.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.leralix.tancommon.markers.CommonAreaMarker;

public class TownRenderEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final CommonAreaMarker areaMarker;

    public TownRenderEvent(CommonAreaMarker areaMarker) {
        super(!Bukkit.getServer().isPrimaryThread());
        this.areaMarker = areaMarker;
    }

    public CommonAreaMarker getAreaMarker() {
        return areaMarker;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

}