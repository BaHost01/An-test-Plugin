package com.cstudioss.cursedcombat.ability;

import com.cstudioss.cursedcombat.util.MathUtil;
import com.cstudioss.cursedcombat.util.VfxService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class CursedDashAbility implements Ability {
    private final JavaPlugin plugin;
    private final VfxService vfxService;

    public CursedDashAbility(JavaPlugin plugin, VfxService vfxService) {
        this.plugin = plugin;
        this.vfxService = vfxService;
    }

    @Override
    public String getId() {
        return "cursed_dash";
    }

    @Override
    public String getName() {
        return plugin.getConfig().getString("abilities.cursed_dash.name", "Cursed Dash");
    }

    @Override
    public double getCostCursedEnergy() {
        return plugin.getConfig().getDouble("abilities.cursed_dash.cost", 15.0);
    }

    @Override
    public long getCooldownMs() {
        return plugin.getConfig().getLong("abilities.cursed_dash.cooldown-ms", 3500L);
    }

    @Override
    public CastResult cast(AbilityContext context) {
        Player caster = context.getCaster();
        if (!caster.isValid() || caster.isDead()) {
            return CastResult.FAILED;
        }
        double distance = plugin.getConfig().getDouble("abilities.cursed_dash.distance", 6.0);
        double velocityCap = plugin.getConfig().getDouble("abilities.cursed_dash.velocity-cap", 1.5);
        Vector direction = caster.getLocation().getDirection().normalize();
        RayTraceResult hit = caster.getWorld().rayTraceBlocks(
                caster.getEyeLocation(),
                direction,
                distance
        );
        if (hit != null && hit.getHitPosition() != null) {
            distance = Math.min(distance, hit.getHitPosition().distance(caster.getEyeLocation().toVector()) - 0.5);
        }
        double strength = MathUtil.clamp(distance / 4.0, 0.2, velocityCap);
        Vector velocity = direction.multiply(strength);
        caster.setVelocity(velocity);
        vfxService.play("vfx.abilities.cursed_dash.cast", caster.getLocation(), caster.getUniqueId());
        return CastResult.SUCCESS;
    }
}
