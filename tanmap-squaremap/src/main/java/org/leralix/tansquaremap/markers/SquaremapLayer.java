package org.leralix.tansquaremap.markers;

import org.leralix.tan.dataclass.Landmark;
import org.leralix.tancommon.markers.CommonAreaMarker;
import org.leralix.tancommon.markers.CommonMarker;
import org.leralix.tancommon.markers.CommonMarkerSet;
import org.leralix.tancommon.markers.IconType;
import xyz.jpenilla.squaremap.api.Key;
import xyz.jpenilla.squaremap.api.Point;
import xyz.jpenilla.squaremap.api.SimpleLayerProvider;
import xyz.jpenilla.squaremap.api.SquaremapProvider;
import xyz.jpenilla.squaremap.api.marker.Marker;
import xyz.jpenilla.squaremap.api.marker.MarkerOptions;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SquaremapLayer extends CommonMarkerSet {

    Map<Key, SimpleLayerProvider> layerMap;

    public SquaremapLayer(Map<Key, SimpleLayerProvider> markerSet) {
        this.layerMap = markerSet;
    }


    @Override
    public void deleteAllMarkers() {
        for(SimpleLayerProvider marker : layerMap.values()){
            marker.clearMarkers();
        }
    }

    @Override
    public CommonMarker createLandmark(Landmark landmark, String name, String worldName, int x, int y, int z, boolean b) {
        Point point = Point.of(x, z);

        MarkerOptions markerOptions = MarkerOptions.builder().
                hoverTooltip(generateDescription(landmark)).
                build();

        String imageKey = landmark.getOwnerID() != null ?
                IconType.LANDMARK_CLAIMED.getFileName():
                IconType.LANDMARK_UNCLAIMED.getFileName();

        Marker marker = Marker.icon(point,Key.of(imageKey),16).markerOptions(markerOptions);

        layerMap.get(Key.of(worldName)).addMarker(Key.of(landmark.getID()), marker);

        return new SquaremapMarker(marker);
    }

    @Override
    public CommonAreaMarker findAreaMarker(String polyID) {
        SimpleLayerProvider layer = layerMap.get(Key.of(polyID));
        if(layer == null){
            return null;
        }
        Marker areaMarker = layerMap.get(Key.of(polyID)).registeredMarkers().get(Key.of(polyID));
        return new SquaremapAreaMarker(areaMarker);
    }

    @Override
    public CommonAreaMarker createAreaMarker(String polyID, String name, boolean b, String worldName, double[] x, double[] z, Color color, String description) {

        List<Point> pointList = new ArrayList<>();

        for(int i = 0; i < x.length; i++) {
            pointList.add(Point.of(x[i], z[i]));
        }

        MarkerOptions options = MarkerOptions.builder().
                fillColor(color).
                fillOpacity(0.5).
                strokeColor(color).
                strokeOpacity(0.8).
                strokeWeight(2).
                hoverTooltip(description).
                build();

        Marker marker = Marker.polygon(pointList).markerOptions(options);

        layerMap.get(Key.of(worldName)).addMarker(Key.of(polyID), marker);
        return new SquaremapAreaMarker(marker);
    }
}
