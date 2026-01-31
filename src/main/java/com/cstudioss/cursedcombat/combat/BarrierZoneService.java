package com.cstudioss.cursedcombat.combat;

import com.cstudioss.cursedcombat.util.VfxService;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class BarrierZoneService {
    private final JavaPlugin plugin;
    private final StatusEffectService statusEffectService;
    private final VfxService vfxService;
    private final List<BarrierZone> zones = new ArrayList<>();
    private BukkitTask task;

    public BarrierZoneService(JavaPlugin plugin, StatusEffectService statusEffectService, VfxService vfxService) {
        this.plugin = plugin;
        this.statusEffectService = statusEffectService;
        this.vfxService = vfxService;
    }

    public void start() {
        stop();
        int tickInterval = plugin.getConfig().getInt("abilities.barrier_zone.tick-interval", 10);
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, tickInterval, tickInterval);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void addZone(Player caster, Location center, double radius, long durationMs) {
        long expiresAt = System.currentTimeMillis() + durationMs;
        zones.add(new BarrierZone(caster.getUniqueId(), center.clone(), radius, expiresAt, center.getWorld()));
        vfxService.play("vfx.abilities.barrier_zone.cast", center, caster.getUniqueId());
    }

    private void tick() {
        long now = System.currentTimeMillis();
        int debuffSeconds = plugin.getConfig().getInt("abilities.barrier_zone.debuff-duration-seconds", 3);
        long debuffMs = debuffSeconds * 1000L;
        for (Iterator<BarrierZone> iterator = zones.iterator(); iterator.hasNext(); ) {
            BarrierZone zone = iterator.next();
            if (zone.getExpiresAt() <= now || zone.getWorld() == null) {
                iterator.remove();
                continue;
            }
            double radius = zone.getRadius();
            for (Entity entity : zone.getWorld().getNearbyEntities(zone.getCenter(), radius, radius, radius)) {
                if (!(entity instanceof LivingEntity living)) {
                    continue;
                }
                if (entity.getUniqueId().equals(zone.getCasterId())) {
                    continue;
                }
                statusEffectService.applyEffect(living, StatusEffectType.WEAKENED, debuffMs);
            }
            UUID casterId = zone.getCasterId();
            if (casterId != null) {
                vfxService.play("vfx.abilities.barrier_zone.tick", zone.getCenter(), casterId);
            }
        }
    }
}
