package org.leralix.tandynmap;

import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapAPI;
import org.dynmap.markers.MarkerAPI;
import org.leralix.tandynmap.markers.CommonMarkerAPI;
import org.leralix.tandynmap.markers.DynmapMarkerAPI;

public class TownsAndNationsDynmap extends TownsAndNationsMapCommon {


    @Override
    protected String getSubMapName() {
        return "dynmap";
    }

    @Override
    protected int getBStatID() {
        return 20740;
    }

    @Override
    protected CommonMarkerAPI createMarkerAPI(Plugin markerAPI) {
        DynmapAPI dynmapAPI = (DynmapAPI) markerAPI;
        return new DynmapMarkerAPI(dynmapAPI.getMarkerAPI());
    }
}