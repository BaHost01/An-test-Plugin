package com.cstudioss.cursedcombat.ability;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {
    private final Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

    public boolean isOnCooldown(UUID playerId, String abilityId, long now) {
        Long expiry = cooldowns
                .getOrDefault(playerId, Map.of())
                .get(abilityId);
        return expiry != null && expiry > now;
    }

    public long getRemaining(UUID playerId, String abilityId, long now) {
        Long expiry = cooldowns
                .getOrDefault(playerId, Map.of())
                .get(abilityId);
        if (expiry == null) {
            return 0;
        }
        return Math.max(0, expiry - now);
    }

    public void applyCooldown(UUID playerId, String abilityId, long now, long durationMs) {
        cooldowns.computeIfAbsent(playerId, ignored -> new ConcurrentHashMap<>())
                .put(abilityId, now + durationMs);
    }

    public Map<String, Long> getCooldowns(UUID playerId) {
        return cooldowns.getOrDefault(playerId, Map.of());
    }
}
