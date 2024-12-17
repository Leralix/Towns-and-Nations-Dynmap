package org.leralix.tansquaremap.markers;


import org.bukkit.Bukkit;
import org.bukkit.World;
import org.leralix.tancommon.TownsAndNationsMapCommon;
import org.leralix.tancommon.markers.CommonLayerAPI;
import org.leralix.tancommon.markers.CommonMarkerSet;
import xyz.jpenilla.squaremap.api.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class SquaremapLayerAPI extends CommonLayerAPI {

    private final Squaremap api;

    public SquaremapLayerAPI(){
        super();
        this.api = SquaremapProvider.get();

        BufferedImage image;

        try {
            File file = new File(TownsAndNationsMapCommon.getPlugin().getDataFolder(), "icons/landmark.png");
            image = ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du chargement de landmark.png", e);
        }

        api.iconRegistry().register(Key.of("landmark"),image);
    }


    @Override
    public boolean isWorking() {
        return api != null;
    }

    @Override
    public CommonMarkerSet createMarkerSet(String id, String layerName, int minZoom, int chunkLayerPriority, boolean hideByDefault) {


        Map<Key, SimpleLayerProvider> simpleLayerProviderMap = new HashMap<>();

        for(World world : Bukkit.getWorlds()){
            SimpleLayerProvider provider = SimpleLayerProvider.builder(layerName)
                    .showControls(true)
                    .layerPriority(chunkLayerPriority)
                    .defaultHidden(hideByDefault)
                    .build();

            Optional<MapWorld> optionalWorld = api.getWorldIfEnabled(BukkitAdapter.worldIdentifier(world));
            if(optionalWorld.isPresent()){
                MapWorld mapWorld = optionalWorld.get();

                mapWorld.layerRegistry().register(Key.of(id), provider);
                simpleLayerProviderMap.put(Key.of(world.getName()), provider);
            }
        }

        return new SquaremapLayer(simpleLayerProviderMap);
    }

}
