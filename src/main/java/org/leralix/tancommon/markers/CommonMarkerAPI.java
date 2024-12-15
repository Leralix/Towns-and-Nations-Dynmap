package org.leralix.tancommon.markers;



import java.util.HashMap;
import java.util.Map;

public abstract class CommonMarkerAPI {

    protected final Map<String, CommonMarkerSet> commonMarkerSetList;

    protected CommonMarkerAPI(){
        commonMarkerSetList = new HashMap<>();
    }
    public abstract boolean isWorking();

    public abstract CommonMarkerSet createMarkerSet(String id, String layerName, int minZoom, int chunkLayerPriority, boolean hideByDefault);

}
