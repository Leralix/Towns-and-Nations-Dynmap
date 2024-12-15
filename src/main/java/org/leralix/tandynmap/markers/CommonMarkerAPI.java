package org.leralix.tandynmap.markers;



import java.util.HashMap;
import java.util.Map;

public abstract class CommonMarkerAPI {

    Map<String, CommonMarkerSet> commonMarkerSetList;

    protected CommonMarkerAPI(){
        commonMarkerSetList = new HashMap<>();
    }
    public abstract boolean isWorking();

    public CommonMarkerSet getMarkerSet(String markerSetID){
        return commonMarkerSetList.get(markerSetID);
    }

    public abstract CommonMarkerSet createMarkerSet(String id, String layerName);

}
