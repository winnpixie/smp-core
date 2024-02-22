package io.github.winnpixie.smp.commands;

import io.github.winnpixie.hukkit.commands.impl.PlayerCommand;
import io.github.winnpixie.smp.SMPCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class HatCommand extends PlayerCommand<SMPCore> {
    private final BaseComponent[] noItemMessage = new ComponentBuilder("No item in your main hand.")
            .color(ChatColor.RED)
            .create();

    public HatCommand(SMPCore plugin) {
        super(plugin, "hat");
    }

    @Override
    public boolean execute(@NotNull Player sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ItemStack currentItem = sender.getInventory().getItemInMainHand();
        if (currentItem.getType() == Material.AIR) {
            sender.spigot().sendMessage(noItemMessage);
            return true;
        }

        sender.getInventory().setItemInMainHand(null);

        ItemStack helmetItem = sender.getInventory().getHelmet();
        if (helmetItem != null && helmetItem.getType() != Material.AIR) {
            sender.getInventory().setItemInMainHand(helmetItem);
        }

        sender.getInventory().setHelmet(currentItem);

        sender.spigot().sendMessage(new ComponentBuilder("You are now wearing ").color(ChatColor.YELLOW)
                .append(new TranslatableComponent(currentItem.getTranslationKey())).color(ChatColor.RED).italic(true)
                .create());
        return true;
    }
}
