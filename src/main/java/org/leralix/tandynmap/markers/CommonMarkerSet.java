package org.leralix.tandynmap.markers;



public interface CommonMarkerSet {

    void setMarkerSetLabel(String landmarks);
    void setMinZoom(int minZoom);
    void setLayerPriority(int priority);
    void setHideByDefault(boolean isHidden);
    void deleteAllMarkers();

    CommonMarker findMarker(String id);
    CommonMarker createMarker(String id, String landmark, String worldName, int x, int y, int z, boolean b);

    CommonAreaMarker findAreaMarker(String polyID);
    CommonAreaMarker createAreaMarker(String polyID, String name, boolean b, String worldName, double[] x, double[] z, boolean b1);

}
