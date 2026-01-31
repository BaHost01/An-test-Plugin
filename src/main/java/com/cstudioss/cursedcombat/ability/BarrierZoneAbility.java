package com.cstudioss.cursedcombat.ability;

import com.cstudioss.cursedcombat.combat.BarrierZoneService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class BarrierZoneAbility implements Ability {
    private final JavaPlugin plugin;
    private final BarrierZoneService barrierZoneService;

    public BarrierZoneAbility(JavaPlugin plugin, BarrierZoneService barrierZoneService) {
        this.plugin = plugin;
        this.barrierZoneService = barrierZoneService;
    }

    @Override
    public String getId() {
        return "barrier_zone";
    }

    @Override
    public String getName() {
        return plugin.getConfig().getString("abilities.barrier_zone.name", "Barrier Zone");
    }

    @Override
    public double getCostCursedEnergy() {
        return plugin.getConfig().getDouble("abilities.barrier_zone.cost", 35.0);
    }

    @Override
    public long getCooldownMs() {
        return plugin.getConfig().getLong("abilities.barrier_zone.cooldown-ms", 12000L);
    }

    @Override
    public CastResult cast(AbilityContext context) {
        Player caster = context.getCaster();
        Location target = context.getTargetLocation();
        if (target == null || target.getWorld() == null) {
            return CastResult.INVALID_TARGET;
        }
        if (!target.getWorld().equals(caster.getWorld())) {
            target = caster.getLocation();
        }
        double radius = plugin.getConfig().getDouble("abilities.barrier_zone.radius", 5.0);
        int durationSeconds = plugin.getConfig().getInt("abilities.barrier_zone.duration-seconds", 6);
        barrierZoneService.addZone(caster, target, radius, durationSeconds * 1000L);
        return CastResult.SUCCESS;
    }
}
