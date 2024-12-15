package org.leralix.tansquaremap;

import org.bukkit.plugin.Plugin;
import org.leralix.tancommon.TownsAndNationsMapCommon;
import org.leralix.tancommon.markers.CommonMarkerAPI;
import org.leralix.tansquaremap.markers.SquaremapMarkerAPI;

public class TownsAndNationsSquaremap extends TownsAndNationsMapCommon {


    @Override
    protected String getSubMapName() {
        return "squaremap";
    }

    @Override
    protected int getBStatID() {
        return 24150;
    }

    @Override
    protected CommonMarkerAPI createMarkerAPI(Plugin markerAPI) {
        return new SquaremapMarkerAPI();
    }
}