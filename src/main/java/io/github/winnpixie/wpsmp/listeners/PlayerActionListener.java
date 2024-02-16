package io.github.winnpixie.wpsmp.listeners;

import io.github.winnpixie.hukkit.ItemHelper;
import io.github.winnpixie.hukkit.PDCWrapper;
import io.github.winnpixie.hukkit.TextHelper;
import io.github.winnpixie.hukkit.listeners.EventListener;
import io.github.winnpixie.wpsmp.Config;
import io.github.winnpixie.wpsmp.WPSMP;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionType;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.Set;

public class PlayerActionListener extends EventListener<WPSMP> {
    private final Set<Material> axes = Set.of(
            Material.WOODEN_AXE,
            Material.STONE_AXE,
            Material.IRON_AXE,
            Material.GOLDEN_AXE,
            Material.DIAMOND_AXE,
            Material.NETHERITE_AXE
    );
    private final BaseComponent[] stripReminder = new ComponentBuilder("REMINDER: ")
            .color(ChatColor.RED)
            .append("You have log stripping disabled!")
            .color(ChatColor.YELLOW)
            .create();
    private final BaseComponent[] autoRespawnReminder = new ComponentBuilder("REMINDER: ")
            .color(ChatColor.RED)
            .append("Auto-Respawn enabled, you are being respawned.")
            .color(ChatColor.YELLOW)
            .create();

    public PlayerActionListener(WPSMP plugin) {
        super(plugin);
    }

    @EventHandler
    private void onChat(AsyncPlayerChatEvent event) {
        event.setFormat(TextHelper.formatText(Config.CHAT_FORMAT));

        if (Config.CHAT_GREEN_TEXT && event.getMessage().startsWith(">")) {
            event.setMessage("\u00A7a" + event.getMessage());
        } else if (Config.FORMAT_CHAT_COLORS) {
            event.setMessage(TextHelper.formatText(event.getMessage()));
        }
    }

    @EventHandler
    private void onRightClick(PlayerInteractEvent event) {
        if (doBoneMeal(event)) return;

        if (doLogStrip(event)) return;

        if (showHeadInfo(event)) return;
    }

    private boolean doBoneMeal(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return false;
        if (event.getHand() == null) return false;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return false;
        if (!isGrowable(clickedBlock)) return false;

        ItemStack item = event.getPlayer().getInventory().getItem(event.getHand());
        if (item == null) return false;
        if (item.getType() != Material.BONE_MEAL) return false;

        Block nextBlock = clickedBlock.getRelative(BlockFace.UP, 1);
        while (nextBlock.getType() == clickedBlock.getType()) {
            nextBlock = nextBlock.getRelative(BlockFace.UP, 1);
        }

        int height = 0;
        while (nextBlock.getRelative(BlockFace.DOWN, height + 1).getType() == clickedBlock.getType()) {
            height++;
        }

        if (height > 2) return false;
        if (nextBlock.getY() >= clickedBlock.getWorld().getMaxHeight()) return false;
        if (!nextBlock.getType().isAir()) return false;

        nextBlock.setType(clickedBlock.getType());
        nextBlock.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, nextBlock.getLocation().add(0.5, 0.5, 0.5),
                20, 0.5, 0.5, 0.5);

        switch (event.getHand()) {
            case HAND -> event.getPlayer().swingMainHand();
            case OFF_HAND -> event.getPlayer().swingOffHand();
        }

        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return true;
        item.setAmount(item.getAmount() - 1);
        event.getPlayer().getInventory().setItem(event.getHand(), item);

        return true;
    }

    private boolean isGrowable(Block block) {
        return block.getType() == Material.CACTUS || block.getType() == Material.SUGAR_CANE;
    }

    private boolean doLogStrip(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return false;
        if (event.getHand() == null) return false;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return false;
        if (!Tag.LOGS.isTagged(clickedBlock.getType())) return false;
        if (clickedBlock.getType().name().startsWith("STRIPPED_")) return false;

        Player player = event.getPlayer();

        ItemStack item = player.getInventory().getItem(event.getHand());
        if (item == null) return false;
        if (!axes.contains(item.getType())) return false;

        PDCWrapper<WPSMP> pdc = new PDCWrapper<>(player.getPersistentDataContainer(), getPlugin());
        if (pdc.getBoolean("can_strip_logs", true)) return false;

        player.spigot().sendMessage(stripReminder);
        event.setCancelled(true);

        return true;
    }

    private boolean showHeadInfo(PlayerInteractEvent event) {
        if (event.getPlayer().isSneaking()) return false;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return false;
        if (event.getHand() == null || !event.getHand().equals(EquipmentSlot.HAND)) return false;

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return false;
        if (!(clickedBlock.getState() instanceof Skull skullState)) return false;
        if (skullState.getOwningPlayer() == null) return false;

        String ownerName = skullState.getOwningPlayer().getName();
        if (ownerName == null) return false;

        event.getPlayer().spigot().sendMessage(new ComponentBuilder("This head belongs to ")
                .color(ChatColor.YELLOW)
                .append(ownerName).color(ChatColor.RED).create());
        event.setCancelled(true);

        return true;
    }

    @EventHandler
    private void onDeath(PlayerDeathEvent event) {
        doPhilzaDeath(event);

        doBeheading(event);

        sayDeathLocation(event);

        respawnPlayer(event);
    }

    // RIP Philza hardcore world
    private boolean doPhilzaDeath(PlayerDeathEvent event) {
        if (!Config.PHILZA_DEATHS) return false;
        if (!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageEvent)) return false;

        Entity killer = damageEvent.getDamager();
        if (killer.equals(event.getEntity())) return false;
        if (!(killer instanceof Zombie zombie)) return false;
        if (zombie.isAdult()) return false;

        event.setDeathMessage(String.format("%s went out like Philza!", event.getEntity().getDisplayName()));
        return true;
    }

    // PvP player heads
    private boolean doBeheading(PlayerDeathEvent event) {
        if (!Config.DROP_HEAD_ON_DEATH) return false;
        if (!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent damageEvent)) return false;

        Entity killer = damageEvent.getDamager();
        if (killer instanceof Projectile projectile) {
            if (!(projectile.getShooter() instanceof LivingEntity)) return false;

            killer = (LivingEntity) projectile.getShooter();
        }

        if (!(killer instanceof Player)) return false;
        if (killer.equals(event.getEntity())) return false;

        ItemStack headStack = new ItemStack(Material.PLAYER_HEAD, 1);
        if (headStack.getItemMeta() == null) return false;

        ItemHelper.editMetaData(headStack, SkullMeta.class, meta -> meta.setOwningPlayer(event.getEntity()));
        event.getEntity().getWorld().dropItemNaturally(event.getEntity().getLocation(), headStack);
        return true;
    }

    private void sayDeathLocation(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location deathLoc = player.getLocation();

        World world = deathLoc.getWorld();
        if (world == null) return;

        player.spigot().sendMessage(new ComponentBuilder("You died at ").color(ChatColor.RED)
                .append("X ")
                .append(String.format("%.1f ", deathLoc.getX())).color(ChatColor.YELLOW)
                .append("Y ").color(ChatColor.RED)
                .append(String.format("%.1f ", deathLoc.getY())).color(ChatColor.YELLOW)
                .append("Z ").color(ChatColor.RED)
                .append(String.format("%.1f ", deathLoc.getZ())).color(ChatColor.YELLOW)
                .append("in the ").color(ChatColor.RED)
                .append(world.getEnvironment().name()).color(ChatColor.YELLOW).create());
    }

    private void respawnPlayer(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PDCWrapper<WPSMP> pdc = new PDCWrapper<>(player.getPersistentDataContainer(), getPlugin());
        if (!pdc.getBoolean("auto_respawn", false)) return;

        player.spigot().sendMessage(autoRespawnReminder);
        getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), player.spigot()::respawn, 1L);
    }

    @EventHandler
    private void onConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.POTION) return;

        ItemHelper.editMetaData(event.getItem(), PotionMeta.class, meta -> {
            if (meta.getBasePotionData().getType() != PotionType.UNCRAFTABLE) return;

            PDCWrapper<WPSMP> pdc = new PDCWrapper<>(meta.getPersistentDataContainer(), getPlugin());
            if (!pdc.has("poe_levels")) return;

            Player player = event.getPlayer();
            player.setLevel(player.getLevel() + pdc.getInt("poe_levels"));

            player.getWorld().playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
            player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation(), 100,
                    0.75, 1.3, 0.75);
        });
    }

    @EventHandler
    private void onPrepareAnvil(PrepareAnvilEvent event) {
        if (!Config.REMOVE_REPAIR_LIMIT) return;

        AnvilInventory anvilInv = event.getInventory();
        if (anvilInv.getRepairCost() < anvilInv.getMaximumRepairCost()) return;

        Player player = (Player) event.getView().getPlayer();
        player.spigot().sendMessage(new ComponentBuilder("DEBUG: ").color(ChatColor.YELLOW)
                .append("%d level(s) ".formatted(anvilInv.getRepairCost())).color(ChatColor.RED)
                .append("for non-vanilla Anvil usage.").color(ChatColor.YELLOW).create());

        getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
            anvilInv.setMaximumRepairCost(Short.MAX_VALUE);
            player.updateInventory();
        });
    }   

    @EventHandler
    private void onSneak(EntityDismountEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getDismounted() instanceof ArmorStand)) return;

        getPlugin().playerManager.use(player, data -> data.setSeat(null));
    }
}