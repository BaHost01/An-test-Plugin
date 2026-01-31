package com.cstudioss.cursedcombat.combat;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageListener implements Listener {
    private final StatusEffectService statusEffectService;

    public DamageListener(StatusEffectService statusEffectService) {
        this.statusEffectService = statusEffectService;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (damager instanceof LivingEntity livingDamager) {
            if (statusEffectService.isActive(livingDamager, StatusEffectType.STUNNED)) {
                event.setCancelled(true);
                return;
            }
            if (statusEffectService.isActive(livingDamager, StatusEffectType.WEAKENED)) {
                event.setDamage(event.getDamage() * 0.8);
            }
        }
        if (event.getEntity() instanceof LivingEntity victim) {
            statusEffectService.cleanupExpired(victim);
        }
    }
}
