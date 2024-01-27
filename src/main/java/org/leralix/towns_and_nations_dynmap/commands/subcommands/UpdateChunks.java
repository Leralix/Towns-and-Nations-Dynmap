package org.leralix.towns_and_nations_dynmap.commands.subcommands;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.leralix.towns_and_nations_dynmap.TownsAndNations_Dynmap;
import org.tan.TownsAndNations.DataClass.ClaimedChunkSettings;
import org.tan.TownsAndNations.DataClass.PlayerData;
import org.tan.TownsAndNations.DataClass.TownData;
import org.tan.TownsAndNations.Lang.Lang;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.enums.TownRolePermission;
import org.tan.TownsAndNations.storage.ClaimedChunkStorage;
import org.tan.TownsAndNations.storage.PlayerDataStorage;
import org.tan.TownsAndNations.storage.TownDataStorage;

import java.util.List;

import static org.tan.TownsAndNations.utils.ChatUtils.getTANString;

public class UpdateChunks extends SubCommand {
    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String getDescription() {
        return "Update the Dynmap";
    }
    public int getArguments(){ return 1;}

    @Override
    public String getSyntax() {
        return "/tanmap update";
    }
    @Override
    public List<String> getTabCompleteSuggestions(Player player, String[] args){
        return null;
    }
    @Override
    public void perform(Player player, String[] args){
        TownsAndNations_Dynmap.getPlugin().Update();
        player.sendMessage("Dynmap updated");

    }

}


