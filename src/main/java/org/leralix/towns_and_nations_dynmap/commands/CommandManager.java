package org.leralix.towns_and_nations_dynmap.commands;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.leralix.towns_and_nations_dynmap.commands.subcommands.UpdateChunks;
import org.tan.TownsAndNations.commands.SubCommand;
import org.tan.TownsAndNations.storage.DataStorage.PlayerDataStorage;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor, TabExecutor, TabCompleter {

    private final ArrayList<org.tan.TownsAndNations.commands.SubCommand> subCommands = new ArrayList<>();

    public CommandManager(){
        subCommands.add(new UpdateChunks());

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player p){

            if (args.length > 0){
                for (int i = 0; i < getSubCommands().size(); i++){
                    if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName())){
                        getSubCommands().get(i).perform(p, args);

                        PlayerDataStorage.saveStats();
                        return true;
                    }
                }
                p.sendMessage("--------------------------------");
                for (int i = 0; i < getSubCommands().size(); i++){
                    p.sendMessage(getSubCommands().get(i).getSyntax() + " - " + getSubCommands().get(i).getDescription());
                }
                p.sendMessage("--------------------------------");
            }

        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if(args.length == 1) {
            for(org.tan.TownsAndNations.commands.SubCommand subCmd : subCommands) {
                if(subCmd.getName().startsWith(args[0].toLowerCase())) {
                    suggestions.add(subCmd.getName());
                }
            }
        }else {
            org.tan.TownsAndNations.commands.SubCommand subCmd = subCommands.stream().filter(cmd -> cmd.getName().equalsIgnoreCase(args[0])).findFirst().orElse(null);
            if(subCmd != null && sender instanceof Player) {
                suggestions = subCmd.getTabCompleteSuggestions((Player) sender, args);
            }
        }

        return suggestions;
    }

    public List<SubCommand> getSubCommands(){
        return subCommands;
    }


}
