package io.github.winnpixie.wpsmp.commands;

import io.github.winnpixie.hukkit.Hukkit;
import io.github.winnpixie.hukkit.commands.BaseCommand;
import io.github.winnpixie.hukkit.commands.CommandErrors;
import io.github.winnpixie.wpsmp.WPSMP;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SeenCommand extends BaseCommand<WPSMP> {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss a");

    public SeenCommand(WPSMP plugin) {
        super(plugin, "seen");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.spigot().sendMessage(CommandErrors.MISSING_ARGUMENTS);
            return true;
        }

        Hukkit.getOfflinePlayer(args[0]).ifPresent(player -> {
            if (!player.hasPlayedBefore()) {
                sender.spigot().sendMessage(CommandErrors.INVALID_TARGET);
                return;
            }

            sender.spigot().sendMessage(new ComponentBuilder(player.getName()).color(ChatColor.RED).append("'s ")
                    .append("last log-on was ").color(ChatColor.YELLOW)
                    .append(dateFormat.format(new Date(player.getLastPlayed()))).color(ChatColor.RED).create());
        });

        return true;
    }
}
