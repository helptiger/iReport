package iReport.util;

import iReport.IReport;
import static iReport.util.Data.init;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerLoginEvent;

@SuppressWarnings(value = { "deprecation" })
public class Utils implements Listener {

    @EventHandler
    public void login(final PlayerLoginEvent event) {
        Player p = event.getPlayer();
        if (!Data.init().playermap.containsKey(p.getUniqueId())) {
            Data.init().playermap.put(p.getUniqueId(), p.getName());
        } else if (Data.init().playermap.get(p.getUniqueId()) != p.getName()) {
            Data.init().playermap.put(p.getUniqueId(), p.getName());
            if (isReported(p.getUniqueId())) {
                updateusernameMYSQL(p.getUniqueId(), p.getName());
            }
        }
    }

    @EventHandler
    public void invcliock(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals("reports")) {
            event.setCancelled(true);
        }
    }

    public static boolean isReported(UUID uniqueId) {
        return Data.init().playermapr.get(uniqueId) != null;
    }

    public static String getxyz(String p, CommandSender sender) {
        try {
            Location loc = Bukkit.getPlayer(p).getLocation();
            return String.valueOf("x " + loc.getBlockX() + " y " + loc.getBlockY() + " z " + loc.getBlockZ());
        } catch (Exception e) {
            if (sender != null) {
                IReport.logger.log(Level.WARNING, p + " is not online");
                sender.sendMessage(ChatColor.RED + p + " is not online");
            }
        }

        return null;

    }

    public static void reportplayer(String target, String reporttype, CommandSender sender, boolean b) {
        boolean isreported = false;
        UUID p = null;
        try {
            p = Bukkit.getPlayer(target).getUniqueId();
        } catch (NullPointerException e) {
            return;
        }
        Data data = Data.init();
        data.playermapo.put(p, target);
        Object o = data.playermapor.get(target);
        if (!data.playermapor.containsKey(target) && o == null ? true : o.equals(p) || b)
            data.playermapor.put(target, p);
        else
            sender.sendMessage("player " + target + " is alredy reported with another UUID please look at the reports or add true");
        synchronized (data.playermap.get(p)) {
            if (data.playermapr.containsKey(p)) {
                isreported = true;
                String s = data.playermapr.get(p);
                data.playermapr.put(p, s + reporttype + "reporter: " + sender.getName() + " ");
            } else {
                data.playermapr.put(p, reporttype + "reporter: " + sender.getName() + " ");
            }
        }
        updateMYSQL(Bukkit.getPlayer(target), isreported);
    }

    public static void updateMYSQL(Player player, boolean isReported) {
        UUID uuid = player.getUniqueId();
        Map<UUID, String> map1 = init().playermap;
        Map<UUID, String> map2 = init().playermapo;
        Map<UUID, String> map3 = init().playermapr;
        if (isReported) {
            IReport.getMYSQL().queryUpdate("INSERT INTO reports (`uuid`, `currentname`, `Report`, `username`) values ('" + uuid + "','" + map1.get(uuid) + "','" + map3.get(uuid) + "','" + map2.get(uuid) + "')");
        } else {
            IReport.getMYSQL().queryUpdate("UPDATE Reports SET Report = '" + map3.get(uuid) + "' WHERE uuid = '" + uuid + "'");

        }

    }

    public static void updateusernameMYSQL(UUID uniqueId, String name) {
        IReport.getMYSQL().queryUpdate("UPDATE Reports SET currentname = '" + name + "' WHERE uuid = '" + uniqueId + "'");
    }
}
