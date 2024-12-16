package org.leralix.tansquaremap.markers;

import org.leralix.tancommon.markers.CommonAreaMarker;
import org.leralix.tancommon.markers.CommonMarker;
import org.leralix.tancommon.markers.CommonMarkerSet;
import xyz.jpenilla.squaremap.api.Key;
import xyz.jpenilla.squaremap.api.Point;
import xyz.jpenilla.squaremap.api.SimpleLayerProvider;
import xyz.jpenilla.squaremap.api.marker.Marker;
import xyz.jpenilla.squaremap.api.marker.MarkerOptions;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SquaremapMarkerSet implements CommonMarkerSet {

    SimpleLayerProvider markerSet;

    public SquaremapMarkerSet(SimpleLayerProvider markerSet) {
        this.markerSet = markerSet;
    }


    @Override
    public void deleteAllMarkers() {
        markerSet.clearMarkers();
    }

    @Override
    public CommonMarker findMarker(String id) {
        Marker marker = markerSet.registeredMarkers().get(Key.of(id));
        return new SquaremapMarker(marker);
    }

    @Override
    public CommonMarker createMarker(String id, String name, String worldName, int x, int y, int z, boolean b) {

        Point point = Point.of(x, z);
        id = id.replace(" ", "_");
        Marker marker = Marker.icon(point,Key.of("diamond"),1);
        markerSet.addMarker(Key.of(id), marker);
        return new SquaremapMarker(marker);
    }

    @Override
    public CommonAreaMarker findAreaMarker(String polyID) {
        Marker areaMarker = markerSet.registeredMarkers().get(Key.of(polyID));
        return new SquaremapAreaMarker(areaMarker);
    }

    @Override
    public CommonAreaMarker createAreaMarker(String polyID, String name, boolean b, String worldName, double[] x, double[] z, Color color, String description) {

        List<Point> pointList = new ArrayList<>();

        for(int i = 0; i < x.length; i++) {
            pointList.add(Point.of(x[i], z[i]));
        }

        String regex = "[^a-zA-Z0-9._-]";
        polyID = polyID.replaceAll(regex, "_");

        MarkerOptions options = MarkerOptions.builder().
                fillColor(color).
                fillOpacity(0.5).
                strokeColor(color).
                strokeOpacity(0.8).
                strokeWeight(2).
                hoverTooltip(description).
                build();

        Marker marker = Marker.polygon(pointList).markerOptions(options);

        markerSet.addMarker(Key.of(polyID), marker);
        return new SquaremapAreaMarker(marker);
    }
}
