package iReport.commands;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static iReport.util.Data.init;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class Reports implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
        Map<UUID, String> map1 = init().playermap;
        Map<UUID, String> map2 = init().playermapo;
        Map<UUID, String> map3 = init().playermapr;
        if (sender instanceof HumanEntity && args.length == 1 && args[0].equalsIgnoreCase("gui")) {
            Inventory inv = calculate(init().playermapo.size());
            for (UUID uuid : map2.keySet()) {
                ItemStack i = new ItemStack(Material.SKULL_ITEM, 1);
                i.setDurability((short) 3);
                SkullMeta meta = (SkullMeta) i.getItemMeta();
                meta.setOwner(map1.get(uuid));
                meta.setDisplayName(map1.get(uuid));
                meta.setLore(Arrays.asList("UUID: " + uuid, "currentname: " + map1.get(uuid), map3.get(uuid), "username: " + map2.get(uuid)));
                i.setItemMeta(meta);
                inv.addItem(i);
            }
            ((HumanEntity) sender).openInventory(inv);
            return true;
        }
        if (args.length == 2) {
            try {
                if (args[0].equalsIgnoreCase("uuid")) {
                    UUID u = UUID.fromString(args[1]);
                    sender.sendMessage("UUID: " + u + " currentname: " + map1.get(u) + " " + map3.get(u) + "username: " + map2.get(u));
                }
                if (args[0].equalsIgnoreCase("usernameo")) {
                    UUID u = init().playermapor.get(args[1]);
                    sender.sendMessage("UUID: " + u + " currentname: " + map1.get(u) + " " + map3.get(u) + "username: " + map2.get(u));
                }
                return true;
            } catch (Exception e) {
                sender.sendMessage(ChatColor.RED + "invalid UUID");
            }
        } else if (args.length == 0) {
            map3.entrySet().stream().forEach(e -> {
                UUID u = e.getKey();
                sender.sendMessage("UUID: " + u + " currentname: " + map1.get(u) + " " + e.getValue() + "username: " + map2.get(u));
            });
            return true;
        }

        return false;
    }

    private Inventory calculate(int size) {
        double f = size;
        f = f / 9;
        if (f == size / 9) {
            return Bukkit.createInventory(null, (int) (f * 9), "reports");
        }
        size = size / 9;
        size++;
        size = size * 9;
        return Bukkit.createInventory(null, size, "reports");
    }
}
