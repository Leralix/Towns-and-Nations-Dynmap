package org.leralix.towns_and_nations_dynmap.Storage;

import org.leralix.towns_and_nations_dynmap.TownsAndNations_Dynmap;
import org.tan.TownsAndNations.DataClass.RegionData;
import org.tan.TownsAndNations.DataClass.TownData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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


        int nbDays = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
            sdf.setLenient(true);
            Date date = sdf.parse(regionData.getDateCreated());
            Date today = new Date();
            long difference = today.getTime() - date.getTime();
            nbDays = (int) difference / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        int numberOfChunks = regionData.getNumberOfClaimedChunk();
        int nbTowns = regionData.getNumberOfTownsIn();
        String description = regionData.getDescription();
        String townCaptialName = regionData.getOwner().getTown().getName();
        List<String> townNames = new ArrayList<>();
        for(TownData townData : regionData.getTownsInRegion()){
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
