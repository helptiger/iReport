package iReport.commands;

import iReport.IReport;
import iReport.util.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class sreport implements CommandExecutor {

    private IReport plugin;

    public sreport(IReport plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String arg1, String[] args) {
        if (args.length > 0) {
            String player = sender.getName();
            String target = args[0];
            if (!sender.hasPermission("ireport.sreport") && !sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "You don't have permission");
                return true;
            }
            plugin.getReports().set("reports.swearing." + player, "; " + target);
            Utils.reportplayer(target, "sReport ", sender, args.length > 1 ? Boolean.valueOf(args[1]) : false);
            sender.sendMessage(ChatColor.BLUE + "You successfully reported " + ChatColor.RED + target);
            plugin.saveReports();
            sender.getServer().getOnlinePlayers().parallelStream().filter(p -> (p.isOp() || p.hasPermission("iReport.seereport")) && p != sender).forEach(p -> p.sendMessage(ChatColor.RED + player + " has reported " + target + " for swearing"));
            return true;
        }
        return false;
    }
}
