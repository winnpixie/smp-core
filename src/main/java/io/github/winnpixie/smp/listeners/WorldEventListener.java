package io.github.winnpixie.smp.listeners;

import io.github.winnpixie.hukkit.listeners.EventListener;
import io.github.winnpixie.smp.Config;
import io.github.winnpixie.smp.SMPCore;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.LightningStrikeEvent;

import java.util.Set;

public class WorldEventListener extends EventListener<SMPCore> {
    private final Set<Material> metallicSwords = Set.of(
            Material.GOLDEN_SWORD,
            Material.IRON_SWORD,
            Material.NETHERITE_SWORD // Netherite contains gold.
    );

    public WorldEventListener(SMPCore plugin) {
        super(plugin);

        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (Config.ALLOW_NETHER_ROOF) return;

            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (player.isDead()) continue;
                if (player.getWorld().getEnvironment() != World.Environment.NETHER) continue;
                if (player.getLocation().getY() < 128) continue;

                player.damage(4.0);
            }
        }, 0L, 10L);
    }

    @EventHandler
    private void onLightningStrike(LightningStrikeEvent event) {
        // TODO: Add back Sea of Thieves' cutlass attraction during storm mechanic?
    }
}