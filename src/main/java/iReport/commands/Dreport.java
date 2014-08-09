package iReport.commands;

import java.util.UUID;

import iReport.IReport;
import iReport.util.Data;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Dreport implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Data data = Data.init();
        if (args[0].equals("*")) {
            if (sender.hasPermission("ireport.dreport.all")) {
                data.playermapo.keySet().stream().forEach(uuid -> {
                    IReport.getMYSQL().queryUpdate("DELETE FROM reports WHERE uuid = '" + uuid.toString() + "'");
                });
                data.playermapo.clear();
                data.playermapor.clear();
                data.playermapr.clear();
                sender.sendMessage(ChatColor.GREEN + "Successfully cleared reports");
            } else {
                sender.sendMessage(ChatColor.RED + "You don't have permission");
            }
            return true;
        }
        try {
            String s = data.playermapo.get(UUID.fromString(args[0]));
            data.playermapo.remove(UUID.fromString(args[0]));
            data.playermapr.remove(UUID.fromString(args[0]));
            data.playermapor.remove(s);
            IReport.getMYSQL().queryUpdate("DELETE FROM reports WHERE uuid = '" + UUID.fromString(args[0]) + "'");
            sender.sendMessage(ChatColor.GREEN + "Successfully deleted " + s);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "invalid UUID");
        }
        return true;
    }

}
