package org.leralix.tansquaremap;

import org.bukkit.plugin.Plugin;
import org.leralix.tancommon.TownsAndNationsMapCommon;
import org.leralix.tancommon.markers.CommonLayerAPI;
import org.leralix.tansquaremap.markers.SquaremapLayerAPI;

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
    protected CommonLayerAPI createMarkerAPI(Plugin markerAPI) {
        return new SquaremapLayerAPI();
    }
}