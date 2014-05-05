package iReport.commands;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import iReport.iReport;
import static iReport.util.Data.init;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Reports implements CommandExecutor {

    @SuppressWarnings("unused")
    private iReport plugin;

    public Reports(iReport iReport) {
        this.plugin = iReport;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
        Map<UUID, String> map1 = init().playermap;
        Map<UUID, String> map2 = init().playermapo;
        Map<UUID, String> map3 = init().playermapr;
        if (args.length == 1) {
            String[] s = args[0].split(":");
            if (s[0].equalsIgnoreCase("uuid")) {
                UUID u = UUID.fromString(s[1]);
                sender.sendMessage("UUID: " + u + " currentname: " + map1.get(u) + " " +map3.get(u) + " username: " + map2.get(u));
            }
        }
        
        Iterator<Entry<UUID, String>> iterator3 = map3.entrySet().iterator();
        while (iterator3.hasNext()) {
            Entry<UUID, String> e = iterator3.next();
            UUID u = e.getKey();
            sender.sendMessage("UUID: " + u + " currentname: " + map1.get(u) + " " + e.getValue() + " username: " + map2.get(u));
        }
        return true;
    }
}
