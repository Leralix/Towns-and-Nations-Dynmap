package org.leralix.tansquaremap.markers;

import org.leralix.tancommon.markers.CommonMarker;
import xyz.jpenilla.squaremap.api.marker.Marker;

public class SquaremapMarker implements CommonMarker {

    Marker marker;

    public SquaremapMarker(Marker marker){
        this.marker = marker;
    }

    @Override
    public void deleteMarker() {

    }

    @Override
    public void setDescription(String newDescription) {

    }

    @Override
    public void setLocation(String worldName, int x, int y, int z) {

    }
}
