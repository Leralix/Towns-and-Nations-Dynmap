package org.leralix.towns_and_nations_dynmap.Storage;

import org.leralix.towns_and_nations_dynmap.TownsAndNations_Dynmap;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.RegionDataStorage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TownDescription {

    private String ID;
    private String name;
    private final int daysSinceCreation;
    private String description;
    private final int numberOfClaims;
    private final int townLevel;
    private final int numberOfMembers;
    private String ownerName;
    private String regionName;
    private String nationName;
    private List<String> membersName;


    public TownDescription(TownData townData){

        String ID = townData.getID();
        String name = townData.getName();


        int nbDays = 0;
        try {
            // Définissez le format de date
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            // Désactivez le strict pour accepter les formats de date moins stricts
            sdf.setLenient(true);
            // Parsez la chaîne en objet Date
            Date date = sdf.parse(townData.getDateCreated());
            // Obtenez la date d'aujourd'hui
            Date today = new Date();

            // Calculez la différence en millisecondes entre les deux dates
            long difference = today.getTime() - date.getTime();
            // Convertissez en jours
            nbDays = (int) difference / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }





        int numberOfChunks = townData.getNumberOfClaimedChunk();
        int townLevel = townData.getTownLevel().getTownLevel();
        int nbPlayer = townData.getPlayerList().size();
        String description = townData.getDescription();
        String ownerName = townData.getLeaderData().getName();
        String regionName = null;
        if(townData.haveRegion())
            regionName = RegionDataStorage.get(townData.getRegionID()).getName();

        List<String> playersName = new ArrayList<>();

        for(String playerID : townData.getPlayerList()){
            playersName.add(PlayerDataStorage.get(playerID).getName());
        }


        this.ID = ID;
        this.name = name;
        this.daysSinceCreation = nbDays;
        this.description = description;
        this.numberOfClaims = numberOfChunks;
        this.townLevel = townLevel;
        this.numberOfMembers = nbPlayer;
        this.ownerName = ownerName;
        this.regionName = regionName;
        this.nationName = name;
        this.membersName = playersName;
    }

    public String getID(){
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getRegionName() {
        return regionName;
    }

    public String getNationName() {
        return nationName;
    }

    public List<String> getMembersName() {
        return membersName;
    }

    public String getChunkDescription(){
        StringBuilder description = new StringBuilder();

        String start = TownsAndNations_Dynmap.getPlugin().getConfig().getString("town_infowindow.start", "Config not found");
        description.append(start);

        //Add the town name
        String townName = TownsAndNations_Dynmap.getPlugin().getConfig().getString("town_infowindow.town_name", "Config not found");
        townName  = townName.replace("%TOWN_NAME%", this.name);
        townName  = townName.replace("%DAYS_SINCE_CREATION%", String.valueOf(this.daysSinceCreation));
        description.append(townName);

        //Add the town description
        String townDescription = TownsAndNations_Dynmap.getPlugin().getConfig().getString("town_infowindow.town_description", "Config not found");
        townDescription  = townDescription.replace("%TOWN_DESCRIPTION%", this.description);
        description.append(townDescription);

        //Add the datas
        String townData = TownsAndNations_Dynmap.getPlugin().getConfig().getString("town_infowindow.town_data", "Config not found");
        townData  = townData.replace("%NUMBER_CLAIMS%", String.valueOf(this.numberOfClaims));
        townData  = townData.replace("%TOWN_LEVEL%", String.valueOf(this.townLevel));
        description.append(townData);

        //Add region name if town have one
        if(regionName != null){
            String region = TownsAndNations_Dynmap.getPlugin().getConfig().getString("town_infowindow.town_region", "Config not found");
            region  = region.replace("%REGION_NAME%", regionName);
            description.append(region);
        }

        //Town leader
        String townLeader = TownsAndNations_Dynmap.getPlugin().getConfig().getString("town_infowindow.town_leader", "Config not found");
        townLeader  = townLeader.replace("%TOWN_LEADER%", ownerName);
        description.append(townLeader);

        //Member list
        String membersList = TownsAndNations_Dynmap.getPlugin().getConfig().getString("town_infowindow.town_members", "Config not found");
        StringBuilder memberList = new StringBuilder();
        for(String member : getMembersName()){
            memberList.append(member).append(", ");
        }
        membersList  = membersList.replace("%MEMBERS_LIST%", memberList);
        description.append(membersList);

        return description.toString();
    }



}
