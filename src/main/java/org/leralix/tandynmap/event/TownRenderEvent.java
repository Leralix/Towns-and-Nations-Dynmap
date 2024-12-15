package org.leralix.tandynmap.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.leralix.tan.dataclass.territory.TownData;
import org.leralix.tandynmap.markers.CommonAreaMarker;

public class TownRenderEvent extends Event {
    private static HandlerList handlers = new HandlerList();
    private final TownData town;
    private final CommonAreaMarker areaMarker;

    public TownRenderEvent(TownData town, CommonAreaMarker areaMarker) {
        super(!Bukkit.getServer().isPrimaryThread());
        this.town = town;
        this.areaMarker = areaMarker;
    }

    public TownData getTown() {
        return town;
    }

    public CommonAreaMarker getAreaMarker() {
        return areaMarker;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}