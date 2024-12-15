package org.leralix.tancommon.commands.subcommands;

import org.bukkit.entity.Player;
import org.leralix.tancommon.TownsAndNationsMapCommon;
import org.leralix.tancommon.commands.SubCommand;

import java.util.List;
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
        try {
            TownsAndNationsMapCommon.getPlugin().updateDynmap();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        player.sendMessage("Dynmap updated");
    }

}


