package io.github.winnpixie.wpsmp.commands;

import io.github.winnpixie.hukkit.Hukkit;
import io.github.winnpixie.hukkit.commands.BaseCommand;
import io.github.winnpixie.hukkit.commands.CommandErrors;
import io.github.winnpixie.wpsmp.WPSMP;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PingCommand extends BaseCommand<WPSMP> {
    public PingCommand(WPSMP plugin) {
        super(plugin, "ping");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            if (!(sender instanceof Player)) {
                sender.spigot().sendMessage(CommandErrors.PLAYERS_ONLY);
                return true;
            }

            sender.spigot().sendMessage(new ComponentBuilder("Your ping is ").color(ChatColor.YELLOW)
                    .append("%dms".formatted(((Player) sender).getPing())).color(ChatColor.RED).italic(true)
                    .create());
            return true;
        }

        Hukkit.findPlayerExact(args[0]).ifPresentOrElse(player ->
                        sender.spigot().sendMessage(new ComponentBuilder("%s's ".formatted(player.getName()))
                                .color(ChatColor.RED)
                                .append("ping is ").color(ChatColor.YELLOW)
                                .append("%dms".formatted(player.getPing())).color(ChatColor.RED).italic(true)
                                .create()),
                () -> sender.spigot().sendMessage(CommandErrors.INVALID_TARGET));

        return true;
    }
}
