package org.leralix.towns_and_nations_dynmap.Storage;

import org.leralix.tan.dataclass.territory.RegionData;
import org.leralix.tan.dataclass.territory.TerritoryData;
import org.leralix.towns_and_nations_dynmap.TownsAndNations_Dynmap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegionDescription {

    private String ID;
    private String name;
    private final int daysSinceCreation;
    private String description;
    private final int numberOfClaims;
    private final int numberOfTowns;
    private String capitalName;
    private String nationName;
    private List<String> townListName;


    public RegionDescription(RegionData regionData){

        String ID = regionData.getID();
        String name = regionData.getName();


        Date today = new Date();
        Date creationDate = new Date(regionData.getDateTimeCreated());

        long diffInDays = today.getTime() - creationDate.getTime();
        int nbDays = (int) (diffInDays / (1000 * 60 * 60 * 24));

        int numberOfChunks = regionData.getNumberOfClaimedChunk();
        int nbTowns = regionData.getNumberOfTownsIn();
        String description = regionData.getDescription();
        String townCaptialName = regionData.getCapital().getName();
        List<String> townNames = new ArrayList<>();
        for(TerritoryData townData : regionData.getSubjects()){
            townNames.add(townData.getName());
        }


        this.ID = ID;
        this.name = name;
        this.daysSinceCreation = nbDays;
        this.description = description;
        this.numberOfClaims = numberOfChunks;
        this.numberOfTowns = nbTowns;
        this.capitalName = townCaptialName;
        this.nationName = name;
        this.townListName = townNames;
    }

    public String getID() {
        return ID;
    }



    public String getChunkDescription(){

        String description = TownsAndNations_Dynmap.getPlugin().getConfig().getString("region_infowindow", "Config not found - region");

        description = description.replace("%REGION_NAME%", this.name);
        description =  description.replace("%DAYS_SINCE_CREATION%", String.valueOf(this.daysSinceCreation));
        description  = description.replace("%DESCRIPTION%", this.description);
        description  = description.replace("%NUMBER_CLAIMS%", String.valueOf(this.numberOfClaims));
        description  = description.replace("%NUMBER_OF_TOWNS%", String.valueOf(this.numberOfTowns));

        description  = description.replace("%REGION_CAPITAL%", capitalName);

        StringBuilder memberList = new StringBuilder();
        for(String member : townListName){
            memberList.append(member).append(", ");
        }
        description  = description.replace("%TOWN_LIST%", memberList);

        return description;
    }



}
