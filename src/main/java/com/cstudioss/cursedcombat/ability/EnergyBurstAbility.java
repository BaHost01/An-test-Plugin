package com.cstudioss.cursedcombat.ability;

import com.cstudioss.cursedcombat.combat.StatusEffectService;
import com.cstudioss.cursedcombat.combat.StatusEffectType;
import com.cstudioss.cursedcombat.util.VfxService;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class EnergyBurstAbility implements Ability {
    private final JavaPlugin plugin;
    private final StatusEffectService statusEffectService;
    private final VfxService vfxService;

    public EnergyBurstAbility(JavaPlugin plugin, StatusEffectService statusEffectService, VfxService vfxService) {
        this.plugin = plugin;
        this.statusEffectService = statusEffectService;
        this.vfxService = vfxService;
    }

    @Override
    public String getId() {
        return "energy_burst";
    }

    @Override
    public String getName() {
        return plugin.getConfig().getString("abilities.energy_burst.name", "Energy Burst");
    }

    @Override
    public double getCostCursedEnergy() {
        return plugin.getConfig().getDouble("abilities.energy_burst.cost", 30.0);
    }

    @Override
    public long getCooldownMs() {
        return plugin.getConfig().getLong("abilities.energy_burst.cooldown-ms", 8000L);
    }

    @Override
    public CastResult cast(AbilityContext context) {
        Player caster = context.getCaster();
        Location origin = caster.getLocation();
        double radius = plugin.getConfig().getDouble("abilities.energy_burst.radius", 4.5);
        double damage = plugin.getConfig().getDouble("abilities.energy_burst.damage", 3.0);
        double knockback = plugin.getConfig().getDouble("abilities.energy_burst.knockback", 0.6);
        int debuffSeconds = plugin.getConfig().getInt("abilities.energy_burst.debuff-duration-seconds", 2);
        vfxService.play("vfx.abilities.energy_burst.cast", origin, caster.getUniqueId());
        for (Entity entity : caster.getWorld().getNearbyEntities(origin, radius, radius, radius)) {
            if (!(entity instanceof LivingEntity living)) {
                continue;
            }
            if (entity.getUniqueId().equals(caster.getUniqueId())) {
                continue;
            }
            living.damage(damage, caster);
            Vector push = living.getLocation().toVector().subtract(origin.toVector()).normalize().multiply(knockback);
            living.setVelocity(push);
            statusEffectService.applyEffect(living, StatusEffectType.WEAKENED, debuffSeconds * 1000L);
        }
        return CastResult.SUCCESS;
    }
}
