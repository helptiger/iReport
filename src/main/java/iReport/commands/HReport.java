package iReport.commands;

import iReport.IReport;
import iReport.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HReport implements CommandExecutor {

    private IReport plugin;

    public HReport(IReport plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg1, String[] args) {
        if (args.length > 1) {
            String player = sender.getName();
            String target = args[0];
            if (!sender.hasPermission("ireport.hreport")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to perform this command");
                return true;
            }
            plugin.getReports().set("reports.hacking." + player, new StringBuilder("type: ").append(args[1]).toString() + "; " + target);
            sender.sendMessage(ChatColor.BLUE + "You successfully reported " + ChatColor.RED + target);
            plugin.saveReports();
            for (Player p : sender.getServer().getOnlinePlayers()) {
                if ((p.isOp()) || (p.hasPermission("iReport.seereport"))) {
                    p.sendMessage(ChatColor.RED + player + " has reported " + target + " for hacking " + args[1]);
                }
            }
            Utils.reportplayer(target, "hReport: " + args[1] + " ", sender, args.length > 2 ? Boolean.valueOf(args[1]) : false);
            return true;
        }
        return false;
    }

}
