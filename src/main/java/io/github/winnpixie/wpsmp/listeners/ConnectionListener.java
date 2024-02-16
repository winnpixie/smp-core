package io.github.winnpixie.wpsmp.listeners;

import io.github.winnpixie.hukkit.TextHelper;
import io.github.winnpixie.hukkit.listeners.EventListener;
import io.github.winnpixie.wpsmp.Config;
import io.github.winnpixie.wpsmp.WPSMP;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ConnectionListener extends EventListener<WPSMP> {
    public ConnectionListener(WPSMP plugin) {
        super(plugin);
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        unlockAllRecipes(event.getPlayer());

        setJoinMessage(event);

        sendMOTD(event.getPlayer());

        getPlugin().infoboard.register(event.getPlayer());
    }

    private void unlockAllRecipes(Player player) {
        getPlugin().getServer().recipeIterator().forEachRemaining(recipe -> {
            if (!(recipe instanceof Keyed keyed)) return;

            player.discoverRecipe(keyed.getKey());
        });
    }

    private void setJoinMessage(PlayerJoinEvent event) {
        if (Config.BROADCAST_JOINS) {
            if (!event.getPlayer().hasPlayedBefore() && !Config.FIRST_TIMER_MESSAGE.isEmpty()) {
                // Send first-timer's message
                event.setJoinMessage(TextHelper.formatText(Config.FIRST_TIMER_MESSAGE
                        .replace("{player_name}", event.getPlayer().getName())));
            } else if (!Config.JOIN_MESSAGE.isEmpty()) {
                // Send normal custom join message
                event.setJoinMessage(TextHelper.formatText(Config.JOIN_MESSAGE
                        .replace("{player_name}", event.getPlayer().getName())));
            }
        } else {
            event.setJoinMessage(null);
        }
    }

    private void sendMOTD(Player player) {
        for (String line : Config.MOTD_LINES) {
            player.spigot().sendMessage(TextComponent.fromLegacyText(TextHelper.formatText(line)
                    .replace("{player_name}", player.getDisplayName())));
        }
    }

    @EventHandler
    private void onLogin(PlayerLoginEvent event) {
        if (!Config.WHITELIST_IGNORE_FULL) return;
        if (event.getResult() != PlayerLoginEvent.Result.KICK_FULL) return;
        if (!event.getPlayer().isWhitelisted()) return;

        event.allow();
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        getPlugin().playerManager.use(event.getPlayer(), data -> {
            data.setSeat(null);

            getPlugin().playerManager.delete(event.getPlayer());
        });

        forgetAllRecipes(event.getPlayer());

        setQuitMessage(event);
    }

    private void forgetAllRecipes(Player player) {
        player.getDiscoveredRecipes().iterator().forEachRemaining(player::undiscoverRecipe);
    }

    private void setQuitMessage(PlayerQuitEvent event) {
        if (!Config.BROADCAST_QUITS) {
            event.setQuitMessage(null);
            return;
        }

        if (Config.QUIT_MESSAGE.isEmpty()) return;

        event.setQuitMessage(TextHelper.formatText(Config.QUIT_MESSAGE
                .replace("{player_name}", event.getPlayer().getDisplayName())));
    }
}
