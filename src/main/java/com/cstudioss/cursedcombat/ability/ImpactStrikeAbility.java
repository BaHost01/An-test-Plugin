package com.cstudioss.cursedcombat.ability;

import com.cstudioss.cursedcombat.combat.StatusEffectService;
import com.cstudioss.cursedcombat.combat.StatusEffectType;
import com.cstudioss.cursedcombat.util.VfxService;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class ImpactStrikeAbility implements Ability {
    private final JavaPlugin plugin;
    private final StatusEffectService statusEffectService;
    private final VfxService vfxService;

    public ImpactStrikeAbility(JavaPlugin plugin, StatusEffectService statusEffectService, VfxService vfxService) {
        this.plugin = plugin;
        this.statusEffectService = statusEffectService;
        this.vfxService = vfxService;
    }

    @Override
    public String getId() {
        return "impact_strike";
    }

    @Override
    public String getName() {
        return plugin.getConfig().getString("abilities.impact_strike.name", "Impact Strike");
    }

    @Override
    public double getCostCursedEnergy() {
        return plugin.getConfig().getDouble("abilities.impact_strike.cost", 20.0);
    }

    @Override
    public long getCooldownMs() {
        return plugin.getConfig().getLong("abilities.impact_strike.cooldown-ms", 5000L);
    }

    @Override
    public CastResult cast(AbilityContext context) {
        Player caster = context.getCaster();
        Entity target = context.getTargetEntity();
        if (!(target instanceof LivingEntity living)) {
            return CastResult.INVALID_TARGET;
        }
        double maxDistance = plugin.getConfig().getDouble("abilities.impact_strike.range", 3.5);
        if (caster.getLocation().distanceSquared(living.getLocation()) > maxDistance * maxDistance) {
            return CastResult.INVALID_TARGET;
        }
        double bonusDamage = plugin.getConfig().getDouble("abilities.impact_strike.bonus-damage", 4.0);
        double knockback = plugin.getConfig().getDouble("abilities.impact_strike.knockback", 0.8);
        living.damage(bonusDamage, caster);
        Vector push = living.getLocation().toVector().subtract(caster.getLocation().toVector()).normalize().multiply(knockback);
        living.setVelocity(push);
        vfxService.play("vfx.abilities.impact_strike.hit", living.getLocation(), caster.getUniqueId());

        int debuffSeconds = plugin.getConfig().getInt("abilities.impact_strike.debuff-duration-seconds", 3);
        statusEffectService.applyEffect(living, StatusEffectType.WEAKENED, debuffSeconds * 1000L);
        return CastResult.SUCCESS;
    }
}
