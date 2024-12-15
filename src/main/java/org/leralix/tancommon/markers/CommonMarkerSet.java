package org.leralix.tancommon.markers;


import java.awt.*;

public interface CommonMarkerSet {

    void deleteAllMarkers();

    CommonMarker findMarker(String id);
    CommonMarker createMarker(String id, String landmark, String worldName, int x, int y, int z, boolean b);

    CommonAreaMarker findAreaMarker(String polyID);
    CommonAreaMarker createAreaMarker(String polyID, String name, boolean b, String worldName, double[] x, double[] z, Color color, String description);

}
