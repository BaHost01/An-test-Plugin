package com.cstudioss.cursedcombat.ability;

import com.cstudioss.cursedcombat.combat.StatusEffectService;
import com.cstudioss.cursedcombat.combat.StatusEffectType;
import com.cstudioss.cursedcombat.data.PlayerDataService;
import com.cstudioss.cursedcombat.data.PlayerProfile;
import com.cstudioss.cursedcombat.data.TechniqueDefinition;
import com.cstudioss.cursedcombat.data.TechniqueService;
import com.cstudioss.cursedcombat.util.MathUtil;
import com.cstudioss.cursedcombat.util.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class AbilityCaster {
    private final JavaPlugin plugin;
    private final PlayerDataService playerDataService;
    private final TechniqueService techniqueService;
    private final CooldownManager cooldownManager;
    private final AbilityRegistry abilityRegistry;
    private final StatusEffectService statusEffectService;

    public AbilityCaster(JavaPlugin plugin,
                         PlayerDataService playerDataService,
                         TechniqueService techniqueService,
                         CooldownManager cooldownManager,
                         AbilityRegistry abilityRegistry,
                         StatusEffectService statusEffectService) {
        this.plugin = plugin;
        this.playerDataService = playerDataService;
        this.techniqueService = techniqueService;
        this.cooldownManager = cooldownManager;
        this.abilityRegistry = abilityRegistry;
        this.statusEffectService = statusEffectService;
    }

    public CastResult castAbility(Player player, String abilityId) {
        PlayerProfile profile = playerDataService.getProfile(player.getUniqueId());
        if (profile == null) {
            return CastResult.FAILED;
        }
        if (statusEffectService.isActive(player, StatusEffectType.STUNNED)) {
            TextUtil.send(player, "&cYou are stunned and cannot cast abilities.");
            return CastResult.FAILED;
        }
        TechniqueDefinition technique = techniqueService.getTechnique(profile.getTechniqueId());
        if (technique == null) {
            TextUtil.send(player, "&cNo technique equipped.");
            return CastResult.FAILED;
        }
        List<String> allowed = technique.getAbilityIds();
        if (!allowed.contains(abilityId)) {
            TextUtil.send(player, "&cThat ability is not available for your technique.");
            return CastResult.INVALID_TARGET;
        }
        Ability ability = abilityRegistry.get(abilityId);
        if (ability == null) {
            TextUtil.send(player, "&cUnknown ability.");
            return CastResult.INVALID_TARGET;
        }
        long now = System.currentTimeMillis();
        if (cooldownManager.isOnCooldown(player.getUniqueId(), abilityId, now)) {
            long remainingMs = cooldownManager.getRemaining(player.getUniqueId(), abilityId, now);
            TextUtil.send(player, "&eAbility is on cooldown for " + (remainingMs / 1000.0) + "s.");
            return CastResult.COOLDOWN;
        }
        if (profile.getCurrentCursedEnergy() < ability.getCostCursedEnergy()) {
            TextUtil.send(player, "&cNot enough cursed energy.");
            return CastResult.NO_ENERGY;
        }

        double entityRange = plugin.getConfig().getDouble("targeting.entity-range", 5.0);
        double locationRange = plugin.getConfig().getDouble("targeting.location-range", 12.0);
        AbilityContext context = new AbilityContext(
                player,
                AbilityTargeting.getTargetEntity(player, entityRange),
                AbilityTargeting.getTargetLocation(player, locationRange),
                player.getWorld(),
                now
        );
        CastResult result = ability.cast(context);
        if (result == CastResult.SUCCESS) {
            double remaining = MathUtil.clamp(
                    profile.getCurrentCursedEnergy() - ability.getCostCursedEnergy(),
                    0,
                    profile.getMaxCursedEnergy()
            );
            profile.setCurrentCursedEnergy(remaining);
            cooldownManager.applyCooldown(player.getUniqueId(), abilityId, now, ability.getCooldownMs());
        }
        return result;
    }
}
