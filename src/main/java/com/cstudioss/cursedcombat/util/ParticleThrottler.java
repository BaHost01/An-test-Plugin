package com.cstudioss.cursedcombat.util;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ParticleThrottler {
    private final Map<UUID, Long> lastParticleTime = new ConcurrentHashMap<>();
    private final long throttleMs;

    public ParticleThrottler(long throttleMs) {
        this.throttleMs = throttleMs;
    }

    public boolean canSpawn(UUID sourceId) {
        long now = System.currentTimeMillis();
        Long last = lastParticleTime.get(sourceId);
        if (last == null || now - last >= throttleMs) {
            lastParticleTime.put(sourceId, now);
            return true;
        }
        return false;
    }
}
