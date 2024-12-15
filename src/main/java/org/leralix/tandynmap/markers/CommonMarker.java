package org.leralix.tandynmap.markers;

public interface CommonMarker {


    void deleteMarker();

    void setDescription(String newDescription);

    void setLocation(String worldName, int x, int y, int z);
}
