package org.leralix.tancommon.update;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.leralix.tan.dataclass.Landmark;
import org.leralix.tan.dataclass.Vector3D;
import org.leralix.tan.storage.stored.LandmarkStorage;
import org.leralix.tan.storage.stored.TownDataStorage;
import org.leralix.tancommon.markers.CommonMarker;
import org.leralix.tancommon.markers.CommonMarkerSet;
import org.leralix.tancommon.TownsAndNationsMapCommon;

import java.util.HashMap;
import java.util.Map;

public class UpdateLandMarks implements Runnable {

    private final Map<String, CommonMarker> landmarks;
    private final CommonMarkerSet set;
    private final long updatePeriod;


    public UpdateLandMarks(CommonMarkerSet set, long updatePeriod){
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
        for (CommonMarker landmarkMarker : landmarks.values()){
            landmarkMarker.deleteMarker();
        }

        for(Landmark landmark : LandmarkStorage.getList()) {
            Vector3D vector3D = landmark.getPosition();
            CommonMarker existingMarker = set.findMarker(landmark.getID());
            String worldName = vector3D.getWorld().getName();

            if (existingMarker != null) {
                existingMarker.setLocation(worldName, vector3D.getX(), vector3D.getY(), vector3D.getZ());
            } else {
                CommonMarker newLandmark = set.createMarker(landmark.getID(), "landmark", worldName, vector3D.getX(), vector3D.getY(), vector3D.getZ(), false);
                newLandmark.setDescription(generateDescription(landmark));
                landmarks.put(landmark.getID(), newLandmark);
            }
        }

        Plugin plugin = TownsAndNationsMapCommon.getPlugin();
        if(updatePeriod > 0)
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new UpdateLandMarks(this), updatePeriod);

    }

    private String generateDescription(Landmark landmark) {

        String res = TownsAndNationsMapCommon.getPlugin().getConfig().getString("landmark_infowindow");
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
