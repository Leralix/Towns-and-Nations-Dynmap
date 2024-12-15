package org.leralix.tansquaremap.markers;


import org.bukkit.Bukkit;
import org.bukkit.World;
import org.leralix.tancommon.markers.CommonMarkerAPI;
import org.leralix.tancommon.markers.CommonMarkerSet;
import xyz.jpenilla.squaremap.api.*;

import java.util.ArrayList;
import java.util.List;

public class SquaremapMarkerAPI extends CommonMarkerAPI {

    private final Squaremap api;
    private final List<World> mapWorlds;

    public SquaremapMarkerAPI(){
        super();
        api = SquaremapProvider.get();
        this.mapWorlds = new ArrayList<>();

        mapWorlds.addAll(Bukkit.getWorlds());
    }


    @Override
    public boolean isWorking() {
        return api != null;
    }

    @Override
    public CommonMarkerSet createMarkerSet(String id, String layerName, int minZoom, int chunkLayerPriority, boolean hideByDefault) {
        SimpleLayerProvider provider = SimpleLayerProvider.builder(layerName)
                .showControls(true)
                .layerPriority(chunkLayerPriority)
                .defaultHidden(hideByDefault)
                .build();
        CommonMarkerSet markerSet = new SquaremapMarkerSet(provider);


        for(World world : mapWorlds){
            api.getWorldIfEnabled(BukkitAdapter.worldIdentifier(world)).ifPresent(mapWorld -> {
                Key key = Key.of(id);
                mapWorld.layerRegistry().register(key, provider);
            });
        }

        commonMarkerSetList.put(id, markerSet);
        return markerSet;
    }


}
