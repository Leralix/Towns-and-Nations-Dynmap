package org.leralix.tancommon.storage;

import java.util.HashMap;
import java.util.Map;

public class RegionDescriptionStorage {

    private static Map<String, RegionDescription> townDescriptionData = new HashMap<>();


    public static void add(RegionDescription data){
        townDescriptionData.put(data.getID(), data);
    }
    public static RegionDescription get(String name){
        return townDescriptionData.get(name);
    }

    public static void remove(String name){
        townDescriptionData.remove(name);
    }

}
