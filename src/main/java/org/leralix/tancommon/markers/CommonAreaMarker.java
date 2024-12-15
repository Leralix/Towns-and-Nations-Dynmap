package org.leralix.tancommon.markers;

public interface CommonAreaMarker {

    void deleteMarker();

    void setCornerLocations(double[] x, double[] z);

    void setLabel(String name);

    void setDescription(String infoWindowPopup);

    void setLineStyle(int baseStrokeWeight, double strokeOpacity, int chunkColorCode);

    void setFillStyle(double fillOpacity, int chunkColorCode);

    void setRangeY(int i, int i1);
}
