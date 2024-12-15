package org.leralix.tandynmap.markers;


import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;

public class DynmapMarkerAPI extends CommonMarkerAPI{

    private final MarkerAPI dynmapAPI;

    public DynmapMarkerAPI(MarkerAPI api){
        this.dynmapAPI = api;
    }


    @Override
    public boolean isWorking() {
        return dynmapAPI != null;
    }

    @Override
    public CommonMarkerSet createMarkerSet(String id, String layerName) {
        CommonMarkerSet markerSet = new DynmapMarkerSet(dynmapAPI, id, layerName);
        commonMarkerSetList.put(id, markerSet);
        return markerSet;
    }

    public MarkerIcon getLandmarkIcon() {
        return dynmapAPI.getMarkerIcon("diamond");
    }
}
