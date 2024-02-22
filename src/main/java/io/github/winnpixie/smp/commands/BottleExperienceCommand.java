package io.github.winnpixie.smp.commands;

import io.github.winnpixie.hukkit.ItemHelper;
import io.github.winnpixie.hukkit.MathHelper;
import io.github.winnpixie.hukkit.PDCWrapper;
import io.github.winnpixie.hukkit.commands.CommandErrors;
import io.github.winnpixie.hukkit.commands.impl.PlayerCommand;
import io.github.winnpixie.smp.SMPCore;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class BottleExperienceCommand extends PlayerCommand<SMPCore> {
    private final BaseComponent[] notEnoughExp = new ComponentBuilder("Insufficient experience.")
            .color(ChatColor.RED)
            .create();
    private final BaseComponent[] noBottle = new ComponentBuilder("You must be holding an empty bottle.")
            .color(ChatColor.RED)
            .create();

    public BottleExperienceCommand(SMPCore plugin) {
        super(plugin, "bottle-experience");
    }

    @Override
    public boolean execute(@NotNull Player sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.spigot().sendMessage(CommandErrors.MISSING_ARGUMENTS);
            return true;
        }

        int levels;
        if (args[0].equalsIgnoreCase("all")) {
            levels = sender.getLevel();
        } else if (MathHelper.isInt(args[0])) {
            levels = Integer.parseInt(args[0]);

            if (sender.getLevel() < levels) {
                sender.spigot().sendMessage(notEnoughExp);
                return true;
            }
        } else {
            sender.spigot().sendMessage(CommandErrors.INVALID_ARGUMENTS);
            return true;
        }

        if (levels < 1) {
            sender.spigot().sendMessage(CommandErrors.INVALID_ARGUMENTS);
            return true;
        }

        ItemStack currentItem = sender.getInventory().getItemInMainHand();
        if (currentItem.getType() != Material.GLASS_BOTTLE) {
            sender.spigot().sendMessage(noBottle);
            return true;
        }

        ItemStack expPotion = new ItemStack(Material.POTION);
        ItemHelper.editMetaData(expPotion, PotionMeta.class, meta -> {
            meta.setBasePotionData(new PotionData(PotionType.UNCRAFTABLE));

            meta.setDisplayName("\u00A7a\u00A7oPotion of Experience");
            meta.setLore(Arrays.asList("Upon consumption, this potion",
                    "will grant the drinker",
                    "%d level(s) of experience.".formatted(levels)));

            PDCWrapper<SMPCore> pdc = new PDCWrapper<>(meta.getPersistentDataContainer(), getPlugin());
            pdc.setInt("poe_levels", levels);
        });

        currentItem.setAmount(currentItem.getAmount() - 1);
        sender.getInventory().addItem(expPotion)
                .forEach((idx, stack) -> sender.getWorld().dropItem(sender.getLocation(), stack));

        sender.setLevel(sender.getLevel() - levels);
        sender.spigot().sendMessage(new ComponentBuilder("You bottled up ").color(ChatColor.LIGHT_PURPLE)
                .append("%d level(s) ".formatted(levels)).color(ChatColor.DARK_PURPLE)
                .append("of experience").color(ChatColor.LIGHT_PURPLE).create());
        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull Player sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return Arrays.asList("all", Integer.toString(sender.getLevel()));
    }
}