package org.leralix.tandynmap.markers;

import org.dynmap.markers.*;
import org.leralix.tancommon.markers.CommonAreaMarker;
import org.leralix.tancommon.markers.CommonMarker;
import org.leralix.tancommon.markers.CommonMarkerSet;

import java.awt.*;

public class DynmapMarkerSet implements CommonMarkerSet {

    MarkerAPI markerAPI;
    MarkerSet markerSet;

    public DynmapMarkerSet(MarkerAPI markerAPI, String id, String layerName, int minZoom, int chunkLayerPriority, boolean hideByDefault) {
        this.markerAPI = markerAPI;
        this.markerSet = markerAPI.createMarkerSet(id, layerName, null, false);

        this.markerSet.setMinZoom(minZoom);
        this.markerSet.setLayerPriority(chunkLayerPriority);
        this.markerSet.setHideByDefault(hideByDefault);
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
    public CommonAreaMarker createAreaMarker(String polyID, String name, boolean b, String worldName, double[] x, double[] z, Color color, String description) {
        AreaMarker marker = markerSet.createAreaMarker(polyID, name, b, worldName, x, z, false);
        return new DynmapAreaMarker(marker);
    }
}
