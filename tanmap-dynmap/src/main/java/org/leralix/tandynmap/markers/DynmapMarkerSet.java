package org.leralix.tandynmap.markers;

import org.dynmap.markers.*;

public class DynmapMarkerSet implements CommonMarkerSet {

    MarkerAPI markerAPI;
    MarkerSet markerSet;

    public DynmapMarkerSet(MarkerAPI markerAPI, String id, String layerName) {
        this.markerAPI = markerAPI;
        this.markerSet = markerAPI.createMarkerSet(id, layerName, null, false);
    }

    @Override
    public void setMarkerSetLabel(String landmarks) {
        markerSet.setMarkerSetLabel(landmarks);
    }

    @Override
    public void setMinZoom(int minZoom) {
        markerSet.setMinZoom(minZoom);
    }

    @Override
    public void setLayerPriority(int priority) {
        markerSet.setLayerPriority(priority);
    }

    @Override
    public void setHideByDefault(boolean isHidden) {
        markerSet.setHideByDefault(isHidden);
    }

    @Override
    public void deleteAllMarkers() {
        for (AreaMarker areaMarker : markerSet.getAreaMarkers()){
            areaMarker.deleteMarker();
        }
    }

    @Override
    public CommonMarker findMarker(String id) {
        Marker marker = markerSet.findMarker(id);
        return new DynmapMarker(marker);
    }

    @Override
    public CommonMarker createMarker(String id, String name, String worldName, int x, int y, int z, boolean b) {
        Marker marker = markerSet.createMarker(id, name, worldName, x, y, z, markerAPI.getMarkerIcon("diamond"), b);
        return new DynmapMarker(marker);
    }

    @Override
    public CommonAreaMarker findAreaMarker(String polyID) {
        AreaMarker areaMarker = markerSet.findAreaMarker(polyID);
        return new DynmapAreaMarker(areaMarker);
    }

    @Override
    public CommonAreaMarker createAreaMarker(String polyID, String name, boolean b, String worldName, double[] x, double[] z, boolean b1) {
        AreaMarker marker = markerSet.createAreaMarker(polyID, name, b, worldName, x, z, b1);
        return new DynmapAreaMarker(marker);
    }
}
