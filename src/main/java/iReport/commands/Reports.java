package iReport.commands;

import static iReport.util.Data.init;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import org.spongepowered.api.data.manipulator.OwnableData;
import org.spongepowered.api.data.manipulator.item.LoreData;
import org.spongepowered.api.entity.living.Human;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventories;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.custom.CustomInventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

import iReport.IReport;
import iReport.util.Data;
import iReport.util.TranslatableWrapper;

public final class Reports implements CommandCallable {

    private List<Text> setLore(UUID uuid) {
        List<Text> list = new ArrayList<Text>();
        Map<UUID, String> map1 = init().playermap;
        Map<UUID, String> map2 = init().playermapo;
        Map<UUID, String> map3 = init().playermapr;
        list.add(Texts.of("UUID: " + uuid));
        list.add(Texts.of("currentname: " + map1.get(uuid)));
        for (String string : map3.get(uuid).split(";")) {
            list.add(Texts.of(string));
        }
        list.add(Texts.of("username: " + map2.get(uuid)));
        return list;
    }

    private CustomInventory calculate(int size) {
        TranslatableWrapper t = new TranslatableWrapper("reports");
        float f = size;
        f = f / 9;
        if (f == size / 9) {
            return Inventories.customInventoryBuilder().name(t).size((int) (f * 9)).build();
        }
        size = size / 9;
        size++;
        size = size * 9;
        return Inventories.customInventoryBuilder().name(t).size(size).build();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        if (!testPermission(source)) {
            return Lists.newArrayList();
        }
        String[] args = arguments.split(" ");
        if (args.length < 2) {
            List<String> l = Lists.newArrayList();
            if ("uuid".startsWith(args[0].toLowerCase())) {
                l.add("uuid");
            }
            if ("usernameo".startsWith(args[0].toLowerCase())) {
                l.add("usernameo");
            }
            if ("gui".startsWith(args[0].toLowerCase())) {
                l.add("gui");
            }
            return l;
        }
        if (args[0].equalsIgnoreCase("uuid")) {
            return Data.init().playermapo.keySet().parallelStream().map(UUID::toString).filter(s -> s.startsWith(args.length > 1 ? args[1] : "")).collect(Collectors.toList());
        }
        if (args[0].equalsIgnoreCase("usernameo")) {
            return Data.init().playermapo.values().parallelStream().filter(s -> s.startsWith(args.length > 1 ? args[1] : "")).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    @Override
    public Optional<CommandResult> process(CommandSource source, String arguments) throws CommandException {
        if (!testPermission(source)) {
            source.sendMessage(Texts.of(TextColors.RED, "You don't have permission to use this command"));
            return Optional.<CommandResult>absent();
        }
        String[] args = arguments.split(" ");
        Map<UUID, String> map1 = init().playermap;
        Map<UUID, String> map2 = init().playermapo;
        Map<UUID, String> map3 = init().playermapr;
        if (source instanceof Human && args.length == 1 && args[0].equalsIgnoreCase("gui")) {
            CustomInventory inv = calculate(init().playermapo.size());
            map2.keySet().parallelStream().forEach(uuid -> {
                ItemStack i = IReport.game.getRegistry().getItemBuilder().itemType(ItemTypes.SKULL).quantity(1).build();
                OwnableData od = i.getOrCreate(OwnableData.class).get();
                od.setProfile(IReport.game.getRegistry().createGameProfile(uuid, map1.get(uuid)));
                i.offer(od);
                LoreData ld = i.getOrCreate(LoreData.class).get();
                ld.set(setLore(uuid));
                i.offer(ld);
                inv.offer(i);
            });
            ((Human) source).openInventory(inv);
            return Optional.of(CommandResult.success());
        }
        if (args.length == 2) {
            try {
                if (args[0].equalsIgnoreCase("uuid")) {
                    UUID u = UUID.fromString(args[1]);
                    source.sendMessage(setLore(u));
                }
                if (args[0].equalsIgnoreCase("usernameo")) {
                    UUID u = init().playermapor.get(args[1]);
                    source.sendMessage(setLore(u));
                }
                return Optional.of(CommandResult.success());
            } catch (Exception e) {
                throw new CommandException(Texts.builder("invalid UUID").color(TextColors.RED).build());
            }
        } else {
            if (map3.isEmpty()) {
                source.sendMessage(Texts.builder("There is no reports").color(TextColors.RED).build());
                return Optional.of(CommandResult.success());
            }
            for (Entry<UUID, String> entry : map3.entrySet()) {
                UUID u = entry.getKey();
                source.sendMessage(setLore(u));
                source.sendMessage(Texts.of(" "));
            }
            return Optional.of(CommandResult.success());
        }
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return source.hasPermission("ireport.reports");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.of((Text)Texts.of("Shows a list of reported players"));
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return Optional.of((Text)Texts.of("Shows a list of reported players"));
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Texts.of("[gui]");
    }

}
