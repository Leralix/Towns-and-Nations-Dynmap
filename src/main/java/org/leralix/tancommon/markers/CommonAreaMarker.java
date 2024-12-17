package org.leralix.tancommon.markers;

public interface CommonAreaMarker {

    void setCornerLocations(double[] x, double[] z);

    void setLabel(String name);

    void setLineStyle(int baseStrokeWeight, double strokeOpacity, int chunkColorCode);

    void setFillStyle(double fillOpacity, int chunkColorCode);

}
