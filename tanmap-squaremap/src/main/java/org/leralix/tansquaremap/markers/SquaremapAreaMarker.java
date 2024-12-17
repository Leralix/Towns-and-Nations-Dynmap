package org.leralix.tansquaremap.markers;


import org.leralix.tancommon.markers.CommonAreaMarker;
import xyz.jpenilla.squaremap.api.SimpleLayerProvider;
import xyz.jpenilla.squaremap.api.marker.Marker;

public class SquaremapAreaMarker implements CommonAreaMarker {

    Marker areaMarker;

    public SquaremapAreaMarker(Marker areaMarker){
        this.areaMarker = areaMarker;
    }

    @Override
    public void deleteMarker() {

    }

    @Override
    public void setCornerLocations(double[] x, double[] z) {

    }

    @Override
    public void setLabel(String name) {

    }

    @Override
    public void setDescription(String infoWindowPopup) {

    }

    @Override
    public void setLineStyle(int baseStrokeWeight, double strokeOpacity, int chunkColorCode) {

    }

    @Override
    public void setFillStyle(double fillOpacity, int chunkColorCode) {

    }

    @Override
    public void setRangeY(int i, int i1) {

    }
}
