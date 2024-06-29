package org.leralix.towns_and_nations_dynmap.Update;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;
import org.leralix.towns_and_nations_dynmap.TownsAndNations_Dynmap;
import org.tan.TownsAndNations.DataClass.Landmark;
import org.tan.TownsAndNations.DataClass.Vector3D;
import org.tan.TownsAndNations.storage.DataStorage.LandmarkStorage;
import org.tan.TownsAndNations.storage.DataStorage.TownDataStorage;

import java.util.HashMap;
import java.util.Map;

public class UpdateLandMarks implements Runnable {

    Map<String, Marker> landmarks;
    MarkerSet set;
    long update_period;


    public UpdateLandMarks(MarkerSet set, long update_period){
        this.landmarks = new HashMap<>();
        this.set = set;
        this.update_period = update_period;
    }

    public UpdateLandMarks(UpdateLandMarks copy) {
        this.landmarks = new HashMap<>();
        this.set = copy.set;
        this.update_period = copy.update_period;
    }

    @Override
    public void run() {
        update();
    }

    public void update(){

        for(Landmark landmark : LandmarkStorage.getList()) {
            Vector3D vector3D = landmark.getPosition();
            Marker existingMarker = set.findMarker(landmark.getID());
            String worldName = vector3D.getWorld().getName();

            if (existingMarker != null) {
                existingMarker.setLocation(worldName, vector3D.getX(), vector3D.getY(), vector3D.getZ());
            } else {
                MarkerIcon landmarkIcon = TownsAndNations_Dynmap.getMarkerAPI().getMarkerIcon("diamond");
                Marker newLandmark = set.createMarker(landmark.getID(), "landmark", worldName, vector3D.getX(), vector3D.getY(), vector3D.getZ(), landmarkIcon, false);
                newLandmark.setDescription(generateDescription(landmark));
                landmarks.put(landmark.getID(), newLandmark);
            }
        }

        Plugin plugin = TownsAndNations_Dynmap.getPlugin();
        if(update_period > 0)
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new UpdateLandMarks(this), update_period);

    }

    private String generateDescription(Landmark landmark) {

        String res = TownsAndNations_Dynmap.getPlugin().getConfig().getString("landmark_infowindow");
        if(res == null)
            return "No description";

        ItemStack reward = landmark.getRessources();
        String ownerName;
        if(landmark.hasOwner())
            ownerName = TownDataStorage.get(landmark.getOwnerID()).getName();
        else
            ownerName = "No owner";

        res = res.replace("%QUANTITY%", String.valueOf(reward.getAmount()) );
        res = res.replace("%ITEM%", reward.getType().name());
        res = res.replace("%OWNER%", ownerName);

        return res;
    }
}
