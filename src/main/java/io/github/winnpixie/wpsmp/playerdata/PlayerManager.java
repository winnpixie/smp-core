package io.github.winnpixie.wpsmp.playerdata;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class PlayerManager {
    private final Map<UUID, PlayerData> dataMap = new HashMap<>();

    public PlayerData retrieve(UUID uuid) {
        return dataMap.computeIfAbsent(uuid, v -> new PlayerData());
    }

    public PlayerData retrieve(Player player) {
        return retrieve(player.getUniqueId());
    }

    public PlayerData use(UUID id, Consumer<PlayerData> consumer) {
        PlayerData data = retrieve(id);
        consumer.accept(data);

        return data;
    }

    public PlayerData use(Player player, Consumer<PlayerData> consumer) {
        PlayerData data = retrieve(player);
        consumer.accept(data);

        return data;
    }

    public PlayerData delete(UUID uuid) {
        return dataMap.remove(uuid);
    }

    public PlayerData delete(Player player) {
        return delete(player.getUniqueId());
    }
}
