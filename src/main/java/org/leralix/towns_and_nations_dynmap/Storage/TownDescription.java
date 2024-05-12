package org.leralix.towns_and_nations_dynmap.Storage;

import org.leralix.towns_and_nations_dynmap.TownsAndNations_Dynmap;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.DataStorage.RegionDataStorage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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


        Date today = new Date();
        Date creationDate = new Date(townData.getDateTimeCreated());

        long diffInDays = today.getTime() - creationDate.getTime();
        int nbDays = (int) (diffInDays / (1000 * 60 * 60 * 24));


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
        String description = TownsAndNations_Dynmap.getPlugin().getConfig().getString("town_infowindow", "Config not found - town");

        description = description.replace("%TOWN_NAME%", this.name);
        description = description.replace("%DAYS_SINCE_CREATION%", String.valueOf(this.daysSinceCreation));
        description = description.replace("%DESCRIPTION%", this.description);
        description = description.replace("%NUMBER_CLAIMS%", String.valueOf(this.numberOfClaims));
        description = description.replace("%TOWN_LEVEL%", String.valueOf(this.townLevel));
        description = description.replace("%REGION_NAME%", Objects.requireNonNullElse(regionName, "No region"));
        description = description.replace("%TOWN_LEADER%", ownerName);

        //Member list
        StringBuilder memberList = new StringBuilder();
        for(String member : getMembersName()){
            memberList.append(member).append(", ");
        }
        description  = description.replace("%MEMBERS_LIST%", memberList);

        return description;
    }



}
