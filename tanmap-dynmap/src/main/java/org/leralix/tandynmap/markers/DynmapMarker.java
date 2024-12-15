package org.leralix.tandynmap.markers;

import org.dynmap.markers.Marker;

public class DynmapMarker implements CommonMarker {

    Marker marker;

    public DynmapMarker(Marker marker){
        this.marker = marker;
    }

    @Override
    public void deleteMarker() {
        marker.deleteMarker();
    }

    @Override
    public void setDescription(String newDescription) {
        marker.setDescription(newDescription);
    }

    @Override
    public void setLocation(String worldName, int x, int y, int z) {
        marker.setLocation(worldName, x, y, z);
    }
}
