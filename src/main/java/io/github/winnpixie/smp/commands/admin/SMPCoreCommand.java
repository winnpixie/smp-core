package io.github.winnpixie.smp.commands.admin;

import io.github.winnpixie.hukkit.commands.BaseCommand;
import io.github.winnpixie.hukkit.commands.CommandErrors;
import io.github.winnpixie.hukkit.configs.adapters.BukkitAdapter;
import io.github.winnpixie.smp.SMPCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class SMPCoreCommand extends BaseCommand<SMPCore> {
    private final BaseComponent[] reloadedMessage = new ComponentBuilder("The configuration has been reloaded.")
            .color(ChatColor.GREEN).create();
    private final BaseComponent[] usageMessage = new ComponentBuilder("=== SMP Core ===")
            .color(ChatColor.GOLD)
            .append("\n/smp-core reload|rl - Reloads the configuration file", ComponentBuilder.FormatRetention.NONE)
            .create();

    public SMPCoreCommand(SMPCore plugin) {
        super(plugin, "smp-core");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("smpcore.command") && !sender.isOp()) {
            sender.spigot().sendMessage(CommandErrors.LACKS_PERMISSIONS);
            return true;
        }

        if (args.length < 1) {
            sender.spigot().sendMessage(usageMessage);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload", "rl" -> reloadConfiguration(sender);
            default -> sender.spigot().sendMessage(CommandErrors.INVALID_ARGUMENTS);
        }

        return true;
    }

    private void reloadConfiguration(CommandSender sender) {
        this.getPlugin().reloadConfig();

        BukkitAdapter adapter = (BukkitAdapter) getPlugin().configManager.getAdapter();
        adapter.setConfig(getPlugin().getConfig());
        getPlugin().configManager.load();

        sender.spigot().sendMessage(reloadedMessage);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return Collections.singletonList("reload");
    }
}
