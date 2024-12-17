package org.leralix.tansquaremap;

import org.bukkit.plugin.Plugin;
import org.leralix.tancommon.TownsAndNationsMapCommon;
import org.leralix.tancommon.markers.CommonLayerAPI;
import org.leralix.tancommon.markers.IconType;
import org.leralix.tansquaremap.markers.SquaremapLayerAPI;
import org.leralix.tansquaremap.markers.SquaremapMarker;
import xyz.jpenilla.squaremap.api.Key;
import xyz.jpenilla.squaremap.api.SquaremapProvider;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TownsAndNationsSquaremap extends TownsAndNationsMapCommon {


    @Override
    protected void registerIcons() {
        File iconsDir = new File(getDataFolder(), "icons");
        if (!iconsDir.exists()) {
            iconsDir.mkdirs();
        }

        TownsAndNationsSquaremap.getPlugin().saveResource("icons/LandmarkClaimed.png", true);
        TownsAndNationsSquaremap.getPlugin().saveResource("icons/LandmarkFree.png", true);


        registerIcon(IconType.LANDMARK_CLAIMED);
        registerIcon(IconType.LANDMARK_UNCLAIMED);
    }

    private void registerIcon(IconType iconType) {
        try {
            File file = new File(TownsAndNationsMapCommon.getPlugin().getDataFolder(), "icons/" + iconType.getFileName());
            BufferedImage image = ImageIO.read(file);
            SquaremapProvider.get().iconRegistry().register(Key.of(iconType.getFileName()),image);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du chargement de landmark.png", e);
        }
    }

    @Override
    protected String getSubMapName() {
        return "squaremap";
    }

    @Override
    protected int getBStatID() {
        return 24150;
    }

    @Override
    protected CommonLayerAPI createMarkerAPI(Plugin markerAPI) {
        return new SquaremapLayerAPI();
    }
}