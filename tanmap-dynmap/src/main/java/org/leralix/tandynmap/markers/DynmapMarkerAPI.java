package org.leralix.tandynmap.markers;


import org.dynmap.markers.MarkerAPI;
import org.leralix.tancommon.markers.CommonMarkerAPI;
import org.leralix.tancommon.markers.CommonMarkerSet;

public class DynmapMarkerAPI extends CommonMarkerAPI {

    private final MarkerAPI dynmapAPI;

    public DynmapMarkerAPI(MarkerAPI api){
        this.dynmapAPI = api;
    }


    @Override
    public boolean isWorking() {
        return dynmapAPI != null;
    }

    @Override
    public CommonMarkerSet createMarkerSet(String id, String layerName, int minZoom, int chunkLayerPriority, boolean hideByDefault) {
        CommonMarkerSet markerSet = new DynmapMarkerSet(dynmapAPI, id, layerName, minZoom, chunkLayerPriority, hideByDefault);
        commonMarkerSetList.put(id, markerSet);
        return markerSet;
    }

}
