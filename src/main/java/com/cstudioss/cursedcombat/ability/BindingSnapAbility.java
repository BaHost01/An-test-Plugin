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

public class BindingSnapAbility implements Ability {
    private final JavaPlugin plugin;
    private final StatusEffectService statusEffectService;
    private final VfxService vfxService;

    public BindingSnapAbility(JavaPlugin plugin, StatusEffectService statusEffectService, VfxService vfxService) {
        this.plugin = plugin;
        this.statusEffectService = statusEffectService;
        this.vfxService = vfxService;
    }

    @Override
    public String getId() {
        return "binding_snap";
    }

    @Override
    public String getName() {
        return plugin.getConfig().getString("abilities.binding_snap.name", "Binding Snap");
    }

    @Override
    public double getCostCursedEnergy() {
        return plugin.getConfig().getDouble("abilities.binding_snap.cost", 25.0);
    }

    @Override
    public long getCooldownMs() {
        return plugin.getConfig().getLong("abilities.binding_snap.cooldown-ms", 9000L);
    }

    @Override
    public CastResult cast(AbilityContext context) {
        Player caster = context.getCaster();
        Entity target = context.getTargetEntity();
        if (!(target instanceof LivingEntity living)) {
            return CastResult.INVALID_TARGET;
        }
        double maxDistance = plugin.getConfig().getDouble("abilities.binding_snap.range", 5.0);
        if (caster.getLocation().distanceSquared(living.getLocation()) > maxDistance * maxDistance) {
            return CastResult.INVALID_TARGET;
        }
        int stunSeconds = plugin.getConfig().getInt("abilities.binding_snap.stun-duration-seconds", 2);
        double pullStrength = plugin.getConfig().getDouble("abilities.binding_snap.pull-strength", 0.3);
        Location origin = caster.getLocation();
        Vector pull = origin.toVector().subtract(living.getLocation().toVector()).normalize().multiply(pullStrength);
        living.setVelocity(pull);
        statusEffectService.applyEffect(living, StatusEffectType.STUNNED, stunSeconds * 1000L);
        vfxService.play("vfx.abilities.binding_snap.hit", living.getLocation(), caster.getUniqueId());
        return CastResult.SUCCESS;
    }
}
