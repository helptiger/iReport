package iReport;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class iReport extends JavaPlugin {

    public static final List<String> REPORTLIST = new ArrayList<>();
    MYSQL sql;

    public iReport() {
    }

    @Override
    @SuppressWarnings("unused")
    public boolean onCommand(CommandSender sender, Command cmd, String label, String args[]) {
        String player = sender.getName();
        List<Player> l = new ArrayList<>();
        l.addAll(Arrays.asList(sender.getServer().getOnlinePlayers()));
        if ((cmd.getName().equalsIgnoreCase("greport")) && (args.length == 1)) {
            String target = args[0];
            if (!sender.hasPermission("ireport.greport") && !sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "You don't have permission");
                return true;
            }
            String already = (String) getConfig().get("reports.griefing." + player);
            sender.sendMessage(ChatColor.BLUE + "You successfully reported " + ChatColor.RED + target);
            getConfig().set("reports.griefing." + player, Rlocation.getxyz(this, args[0]) + "; " + target);

            saveConfig();
            l.parallelStream().forEach(p ->{
                if (p.isOp() || p.hasPermission("iReport.seereport")) {
                    p.sendMessage(ChatColor.RED + player + " has reported " + target + " for griefing");
                }
            });

            return true;
        }
        if ((cmd.getName().equalsIgnoreCase("hreport")) && (args.length == 2)) {
            String target = args[0];
            if (!sender.hasPermission("ireport.hreport")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to perform this command");
                return true;
            }
            String already = (String) getConfig().get("reports.hacking." + player);
            getConfig().set("reports.hacking." + player, new StringBuilder("type: ").append(args[1]).toString() + "; " + target);
            sender.sendMessage(ChatColor.BLUE + "You successfully reported " + ChatColor.RED + target);
            saveConfig();

            l.parallelStream().forEach(p ->{
                if (p.isOp() || p.hasPermission("iReport.seereport")) {
                    p.sendMessage(ChatColor.RED + player + " has reported " + target + " for hacking " + args[1]);
                }
            });
            return true;
        }
        if ((cmd.getName().equalsIgnoreCase("sreport")) && (args.length == 1)) {
            String target = args[0];
            if (!sender.hasPermission("ireport.sreport")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to perform this command"s);
                return true;
            }
            String already = (String) getConfig().get("reports.swearing." + player);
            getConfig().set("reports.swearing." + player, "; " + target);
            sender.sendMessage(ChatColor.BLUE + "You successfully reported" + ChatColor.RED + target);
            saveConfig();

            l.parallelStream().forEach(p ->{
                if (p.isOp() || p.hasPermission("iReport.seereport")) {
                    p.sendMessage(ChatColor.RED + player + " has reported " + target + " for swearing");
                }
            });
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("ireport")) {
            sender.sendMessage(ChatColor.YELLOW + "==============================");
            sender.sendMessage(ChatColor.GREEN + "/greport - Report a griefer");
            sender.sendMessage(ChatColor.GREEN + "/hreport - Report a hacker");
            sender.sendMessage(ChatColor.GREEN + "/sreport - Report a swearer");
            sender.sendMessage(ChatColor.GREEN + "/ireport - Show this help menu");
            sender.sendMessage(ChatColor.YELLOW + "==============================");
            sender.sendMessage(ChatColor.MAGIC + "Created by tudse145 & heni123321");

            return true;
        }
        if (cmd.getName().equalsIgnoreCase("reports")) {
            try {
                Scanner sc = new Scanner(new File("plugins/iReport/", "config.yml"));
                while (sc.hasNext()) {
                    sender.sendMessage(sc.nextLine());
                }
                sc.close();
            } catch (FileNotFoundException e) {
                
            }

           
            return true;
        }
        return false;
    }

    public MYSQL getMYSQL() {
//      PluginManager pm = this.getServer().getPluginManager();
        if (sql == null) {
            try {
                sql = new MYSQL();
/*              Reports Reports = new Reports(this);
                pm.registerEvents(Reports, this);
                this.getCommand("greport").setExecutor(Reports);
   */         } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.sql;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> l = new ArrayList<>();
        if (sender.isOp()) {
            l.add("hreport");
            l.add("greport");
            l.add("sreport");
            l.add("ireport");
            return l;
        }
        if (sender.hasPermission("ireport.hreport")) {
            l.add("hreport");
        }
        if (sender.hasPermission("ireport.greport")) {
            l.add("greport");
        }
        if (sender.hasPermission("ireport.sreport")) {
            l.add("sreport");
        }
        l.add("ireport");
        return l;
    }

    @Override
    public void onEnable() {
        saveConfig();
        getConfig().options().copyDefaults(true);
        // getMYSQL();
    }
}
