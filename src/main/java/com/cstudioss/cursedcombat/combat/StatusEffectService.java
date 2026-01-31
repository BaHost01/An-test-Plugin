package com.cstudioss.cursedcombat.combat;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.EnumMap;
import java.util.Map;

public class StatusEffectService {
    private final JavaPlugin plugin;
    private final Map<StatusEffectType, NamespacedKey> keys = new EnumMap<>(StatusEffectType.class);

    public StatusEffectService(JavaPlugin plugin) {
        this.plugin = plugin;
        for (StatusEffectType type : StatusEffectType.values()) {
            keys.put(type, new NamespacedKey(plugin, "status_" + type.getId()));
        }
    }

    public void applyEffect(LivingEntity entity, StatusEffectType type, long durationMs) {
        long expiry = System.currentTimeMillis() + durationMs;
        PersistentDataContainer container = entity.getPersistentDataContainer();
        container.set(keys.get(type), PersistentDataType.LONG, expiry);

        int ticks = (int) Math.max(1, durationMs / 50L);
        if (type == StatusEffectType.STUNNED) {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, ticks, 10, true, false, false));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, ticks, 1, true, false, false));
        } else if (type == StatusEffectType.WEAKENED) {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, ticks, 0, true, false, false));
        }
    }

    public boolean isActive(LivingEntity entity, StatusEffectType type) {
        PersistentDataContainer container = entity.getPersistentDataContainer();
        Long expiry = container.get(keys.get(type), PersistentDataType.LONG);
        return expiry != null && expiry > System.currentTimeMillis();
    }

    public void cleanupExpired(LivingEntity entity) {
        PersistentDataContainer container = entity.getPersistentDataContainer();
        long now = System.currentTimeMillis();
        for (StatusEffectType type : StatusEffectType.values()) {
            Long expiry = container.get(keys.get(type), PersistentDataType.LONG);
            if (expiry != null && expiry <= now) {
                container.remove(keys.get(type));
            }
        }
    }
}
