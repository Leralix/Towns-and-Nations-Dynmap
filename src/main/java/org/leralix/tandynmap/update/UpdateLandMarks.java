package org.leralix.tandynmap.update;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.Vector3D;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tandynmap.TownsAndNationsDynmap;

import java.util.HashMap;
import java.util.Map;

public class UpdateLandMarks implements Runnable {

    private final Map<String, Marker> landmarks;
    private final MarkerSet set;
    private final long updatePeriod;


    public UpdateLandMarks(MarkerSet set, long updatePeriod){
        this.landmarks = new HashMap<>();
        this.set = set;
        this.updatePeriod = updatePeriod;
    }

    public UpdateLandMarks(UpdateLandMarks copy) {
        this.landmarks = new HashMap<>();
        this.set = copy.set;
        this.updatePeriod = copy.updatePeriod;
    }

    @Override
    public void run() {
        update();
    }

    public void update(){

        //Reset old markers
        for (Marker landmarkMarker : landmarks.values()){
            landmarkMarker.deleteMarker();
        }

        for(Landmark landmark : LandmarkStorage.getList()) {
            Vector3D vector3D = landmark.getPosition();
            Marker existingMarker = set.findMarker(landmark.getID());
            String worldName = vector3D.getWorld().getName();

            if (existingMarker != null) {
                existingMarker.setLocation(worldName, vector3D.getX(), vector3D.getY(), vector3D.getZ());
            } else {
                MarkerIcon landmarkIcon = TownsAndNationsDynmap.getPlugin().getMarkerAPI().getMarkerIcon("diamond");
                Marker newLandmark = set.createMarker(landmark.getID(), "landmark", worldName, vector3D.getX(), vector3D.getY(), vector3D.getZ(), landmarkIcon, false);
                newLandmark.setDescription(generateDescription(landmark));
                landmarks.put(landmark.getID(), newLandmark);
            }
        }

        Plugin plugin = TownsAndNationsDynmap.getPlugin();
        if(updatePeriod > 0)
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new UpdateLandMarks(this), updatePeriod);

    }

    private String generateDescription(Landmark landmark) {

        String res = TownsAndNationsDynmap.getPlugin().getConfig().getString("landmark_infowindow");
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
