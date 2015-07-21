package iReport.commands;

import iReport.util.Constance;
import iReport.util.Utils;

import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

import com.google.common.base.Optional;

public final class greport implements CommandCallable {

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        return Utils.getPlayerNames();
    }

    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        if (!testPermission(source)) {
            throw new CommandException(Texts.of(TextColors.RED, "You don't have permission to use this command"));
        }
        String[] args = arguments.split(" ");
        if (args.length > 0 && !args[0].isEmpty()) {
            String player = source.getName();
            String target = args[0];
            Utils.reportplayer(target, "gReport: " + Utils.getxyz(args[0], source) + " ", source, args.length > 1 ? Boolean.valueOf(args[1]) : false);
            source.sendMessage(Texts.builder("You successfully reported ").color(TextColors.BLUE).append(Texts.builder(target).color(TextColors.RED).build()).build());
            source.sendMessage(Utils.get(Constance.GREPORT_SUCESS, target));
            for (Player p : Constance.server.getOnlinePlayers()) {
                if (p.hasPermission("iReport.seereport") && p != source) {
                    p.sendMessage(Texts.builder(player + " has reported " + target + " for griefing").color(TextColors.RED).build());
                }
            }
            return CommandResult.success();
        }
        throw new CommandException(Texts.builder("Not enough arguments").color(TextColors.RED).build());
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return source.hasPermission("ireport.greport");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.of((Text) Texts.of("Reports a player for grief"));
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return Optional.of((Text) Texts.of("Reports a player for grief"));
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Texts.of("<name>");
    }

}
