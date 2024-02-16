package io.github.winnpixie.wpsmp.commands;

import io.github.winnpixie.hukkit.commands.impl.PlayerCommand;
import io.github.winnpixie.wpsmp.Config;
import io.github.winnpixie.wpsmp.WPSMP;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.command.Command;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SitCommand extends PlayerCommand<WPSMP> {
    private final BaseComponent[] disabledMessage = new ComponentBuilder("Sitting is currently disabled.")
            .color(ChatColor.RED).create();
    private final BaseComponent[] invalidBlockMessage = new ComponentBuilder("Target block is not a valid stair or slab.")
            .color(ChatColor.RED).create();
    private final BaseComponent[] sitMessage = new ComponentBuilder("You are now sitting.")
            .color(ChatColor.GREEN).create();

    public SitCommand(WPSMP plugin) {
        super(plugin, "sit");
    }

    @Override
    public boolean execute(@NotNull Player sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!Config.ALLOW_SITTING) {
            sender.spigot().sendMessage(disabledMessage);
            return true;
        }

        Block block = sender.getTargetBlockExact(6, FluidCollisionMode.NEVER);
        if (!isValidSeat(block)) {
            sender.spigot().sendMessage(invalidBlockMessage);
            return true;
        }

        getPlugin().playerManager.use(sender, data -> {
            ArmorStand seat = (ArmorStand) sender.getWorld().spawnEntity(block.getLocation().add(0.5, -1, 0.5), EntityType.ARMOR_STAND);

            if (Tag.STAIRS.isTagged(block.getType())) {
                seat.setRotation(switch (((Stairs) block.getBlockData()).getFacing()) {
                    case EAST -> 90;
                    case SOUTH -> 180;
                    case WEST -> 270;
                    default -> 0;
                }, 0F);
            }

            seat.setInvulnerable(true);
            seat.setGravity(false);
            seat.setInvisible(true);
            seat.addPassenger(sender);

            data.setSeat(seat);
            sender.spigot().sendMessage(sitMessage);
        });

        return true;
    }

    private boolean isValidSeat(Block block) {
        if (block == null) return false;

        Material blockType = block.getType();
        Block blockAbove = block.getRelative(BlockFace.UP);
        if (!blockAbove.isEmpty() && !blockAbove.isLiquid()) return false;

        if (Tag.STAIRS.isTagged(blockType)) {
            Stairs stairData = (Stairs) block.getBlockData();
            return stairData.getHalf() == Bisected.Half.BOTTOM;
        }

        return Tag.SLABS.isTagged(blockType);
    }
}
