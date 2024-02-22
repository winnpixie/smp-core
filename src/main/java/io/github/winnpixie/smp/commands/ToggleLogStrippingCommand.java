package io.github.winnpixie.smp.commands;

import io.github.winnpixie.hukkit.PDCWrapper;
import io.github.winnpixie.hukkit.commands.impl.PlayerCommand;
import io.github.winnpixie.smp.SMPCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ToggleLogStrippingCommand extends PlayerCommand<SMPCore> {
    private final BaseComponent[] permitMessage = new ComponentBuilder("You may now strip logs again!")
            .color(ChatColor.GREEN).create();
    private final BaseComponent[] denyMessage = new ComponentBuilder("You will no longer strip logs with an axe.")
            .color(ChatColor.RED).create();

    public ToggleLogStrippingCommand(SMPCore plugin) {
        super(plugin, "toggle-log-stripping");
    }

    @Override
    public boolean execute(@NotNull Player sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        PDCWrapper<SMPCore> pdc = new PDCWrapper<>(sender.getPersistentDataContainer(), getPlugin());
        if (pdc.getBoolean("can_strip_logs", true)) {
            pdc.setBoolean("can_strip_logs", false);

            sender.spigot().sendMessage(denyMessage);
        } else {
            pdc.setBoolean("can_strip_logs", true);

            sender.spigot().sendMessage(permitMessage);
        }

        return true;
    }
}
