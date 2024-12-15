package org.leralix.tandynmap.markers;

import org.dynmap.markers.AreaMarker;

public class DynmapAreaMarker implements CommonAreaMarker {

    AreaMarker areaMarker;
    public DynmapAreaMarker(AreaMarker areaMarker){
        this.areaMarker = areaMarker;
    }

    @Override
    public void deleteMarker() {
        areaMarker.deleteMarker();
    }

    @Override
    public void setCornerLocations(double[] x, double[] z) {
        areaMarker.setCornerLocations(x, z);
    }

    @Override
    public void setLabel(String name) {
        areaMarker.setLabel(name);
    }

    @Override
    public void setDescription(String infoWindowPopup) {
        areaMarker.setDescription(infoWindowPopup);
    }

    @Override
    public void setLineStyle(int baseStrokeWeight, double strokeOpacity, int chunkColorCode) {
        areaMarker.setLineStyle(baseStrokeWeight, strokeOpacity, chunkColorCode);
    }

    @Override
    public void setFillStyle(double fillOpacity, int chunkColorCode) {
        areaMarker.setFillStyle(fillOpacity, chunkColorCode);
    }

    @Override
    public void setRangeY(int i, int i1) {
        areaMarker.setRangeY(i, i1);
    }
}
