package iReport.commands;

import java.util.stream.Stream;

import iReport.iReport;
import iReport.mysql.MYSQL;
import iReport.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class sreport implements CommandExecutor {

    private iReport plugin;

    public sreport(iReport plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg1, String[] args) {
        if (args.length > 0) {
            String player = sender.getName();
            String target = args[0];
            if ((!sender.hasPermission("ireport.sreport")) && (!sender.isOp())) {
                sender.sendMessage(ChatColor.RED + "You don't have permission");
                return true;
            }
            plugin.getReports().set("reports.swearing." + player, "; " + target);
            sender.sendMessage(ChatColor.BLUE + "You successfully reported " + ChatColor.RED + target);
            if (MYSQL.isenable) {
                plugin.getMYSQL().queryUpdate("INSERT INTO reports (`name`,`Reason`) values ('" + target + "',' Swearing ')");
            }
            plugin.saveReports();
            Stream.of(sender.getServer().getOnlinePlayers()).parallel().filter(p -> p.isOp() || p.hasPermission("iReport.seereport")).forEach(p -> p.sendMessage(ChatColor.RED + player + " has reported " + target + " for swearing"));
            Utils.reportplayer(target, "sReport ", sender, args.length > 1 ? Boolean.valueOf(args[1]) : false);
            return true;
        }
        return false;
    }
}
