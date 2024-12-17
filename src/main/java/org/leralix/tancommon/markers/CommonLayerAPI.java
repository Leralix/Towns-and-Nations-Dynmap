package org.leralix.tancommon.markers;



import java.util.HashMap;
import java.util.Map;

public abstract class CommonLayerAPI {


    public abstract boolean isWorking();

    public abstract CommonMarkerSet createMarkerSet(String id, String layerName, int minZoom, int chunkLayerPriority, boolean hideByDefault);

}
