package org.leralix.tancommon.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tancommon.markers.CommonAreaMarker;

public class RegionRenderEvent extends Event {
    private static HandlerList handlers = new HandlerList();
    private final RegionData regionData;
    private final CommonAreaMarker areaMarker;

    public RegionRenderEvent(RegionData town, CommonAreaMarker areaMarker) {
        super(!Bukkit.getServer().isPrimaryThread());
        this.regionData = town;
        this.areaMarker = areaMarker;
    }

    public RegionData getRegion() {
        return regionData;
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