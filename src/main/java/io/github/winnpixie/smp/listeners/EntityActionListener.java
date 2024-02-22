package io.github.winnpixie.smp.listeners;

import io.github.winnpixie.hukkit.listeners.EventListener;
import io.github.winnpixie.smp.Config;
import io.github.winnpixie.smp.SMPCore;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class EntityActionListener extends EventListener<SMPCore> {
    public EntityActionListener(SMPCore plugin) {
        super(plugin);
    }

    @EventHandler
    private void onEntityDamaged(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent eveEvent) {
            if (Config.HEAD_PATS) {
                if (doHeadPat(eveEvent)) return; // Don't execute anything else, this was a friendly gesture.
            }

            if (Config.HOT_HANDS) {
                doHotHandsAttack(eveEvent);
            }

            if (Config.PARRY_SOUNDS) {
                doParrySound(eveEvent);
            }
        }
    }

    private boolean doHeadPat(EntityDamageByEntityEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return false;
        if (!(event.getDamager() instanceof Player player)) return false;
        if (!player.isSneaking()) return false;
        if (!(event.getEntity() instanceof Tameable receiver)) return false;
        if (!receiver.isTamed()) return false;

        receiver.getWorld().spawnParticle(Particle.HEART, receiver.getEyeLocation(), 5,
                0.5, 0.5, 0.5);
        event.setCancelled(true);

        return true;
    }

    private void doHotHandsAttack(EntityDamageByEntityEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
        if (!(event.getDamager() instanceof Player player)) return;
        if (player.getFireTicks() < 1) return;

        Entity victim = event.getEntity();
        if (victim.getFireTicks() < 100) { // 5 seconds
            victim.setFireTicks(victim.getFireTicks() + 200); // Add 10 seconds
        } else {
            victim.setFireTicks(victim.getFireTicks() + 100); // Add 5 seconds
        }
    }

    private void doParrySound(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Projectile)) return;

        Entity attacker = event.getDamager();
        if (attacker instanceof Projectile projectile) {
            if (!(projectile.getShooter() instanceof LivingEntity)) return;

            attacker = (LivingEntity) projectile.getShooter();
        }

        if (!(attacker instanceof Player player)) return;

        player.getWorld().playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.1f);
    }

    @EventHandler
    private void onProjectileCollide(ProjectileHitEvent event) {
        if (!isThrowable(event.getEntity())) return;
        if (!(event.getHitEntity() instanceof Player player)) return;

        player.damage(0.0, event.getEntity());
    }

    private boolean isThrowable(Entity entity) {
        return entity instanceof Egg || entity instanceof Snowball;
    }
}
