package iReport.commands;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

import com.google.common.collect.Lists;

import iReport.util.Constance;
import iReport.util.Data;
import iReport.util.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public final class Dreport implements CommandCallable {

    private static final File FILE = new File(Constance.configfolder, "reports.cfg");

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        if (!testPermission(source)) {
            return Lists.newArrayList();
        }
        return Data.init().playermapo.keySet().parallelStream().map(UUID::toString).filter(s -> s.startsWith(arguments.split(" ")[0])).collect(Collectors.toList());
    }

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        if (!testPermission(source)) {
            throw new CommandException(Utils.get("permission.missing"));
        }
        String[] args = arguments.split(" ");
        Data data = Data.init();
        if (args[0].equals("*")) {
            if (source.hasPermission("ireport.dreport.all")) {
                data.playermapo.keySet().stream().map(UUID::toString).forEach(this::delete);
                Constance.getMYSQL().queryUpdate("DELETE FROM reports");
                data.playermapo.clear();
                data.playermapor.clear();
                data.playermapr.clear();
                source.sendMessage(Utils.get("dreport.sucess.all"));
                return CommandResult.success();
            } else {
                throw new CommandException(Utils.get("permission.missing"));
            }

        }
        try {
            UUID uuid = UUID.fromString(args[0]);
            String playername = data.playermapo.get(uuid);
            data.playermapo.remove(uuid);
            data.playermapr.remove(uuid);
            data.playermapor.remove(playername);
            delete(uuid.toString());
            source.sendMessage(Utils.get("dreport.sucess.all", playername));
            Constance.getMYSQL().queryUpdate("DELETE FROM reports WHERE uuid = '" + UUID.fromString(args[0]) + "'");
            return CommandResult.success();
        } catch (IllegalArgumentException e) {
            throw new CommandException(Utils.get("dreport.error"));
        }
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return source.hasPermission("ireport.dreport");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.of(Utils.get("dreport.description"));
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return Optional.of(Utils.get("dreport.description"));
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Texts.of("<UUID>");
    }

    public void delete(String uuid) {
        HoconConfigurationLoader cfgfile = HoconConfigurationLoader.builder().setFile(FILE).build();
        try {
            ConfigurationNode config = cfgfile.load();
            config.getNode("reports").removeChild(uuid);
            cfgfile.save(config);
        } catch (IOException e) {
        }
    }
}
