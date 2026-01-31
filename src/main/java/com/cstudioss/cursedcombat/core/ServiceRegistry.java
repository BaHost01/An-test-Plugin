package com.cstudioss.cursedcombat.core;

import com.cstudioss.cursedcombat.ability.AbilityCaster;
import com.cstudioss.cursedcombat.ability.AbilityRegistry;
import com.cstudioss.cursedcombat.ability.CooldownManager;
import com.cstudioss.cursedcombat.combat.BarrierZoneService;
import com.cstudioss.cursedcombat.combat.StatusEffectService;
import com.cstudioss.cursedcombat.data.PlayerDataService;
import com.cstudioss.cursedcombat.util.ParticleThrottler;
import com.cstudioss.cursedcombat.util.VfxService;

public class ServiceRegistry {
    private final PlayerDataService playerDataService;
    private final CooldownManager cooldownManager;
    private final AbilityRegistry abilityRegistry;
    private final AbilityCaster abilityCaster;
    private final StatusEffectService statusEffectService;
    private final BarrierZoneService barrierZoneService;
    private final ParticleThrottler particleThrottler;
    private final VfxService vfxService;

    public ServiceRegistry(PlayerDataService playerDataService,
                           CooldownManager cooldownManager,
                           AbilityRegistry abilityRegistry,
                           AbilityCaster abilityCaster,
                           StatusEffectService statusEffectService,
                           BarrierZoneService barrierZoneService,
                           ParticleThrottler particleThrottler,
                           VfxService vfxService) {
        this.playerDataService = playerDataService;
        this.cooldownManager = cooldownManager;
        this.abilityRegistry = abilityRegistry;
        this.abilityCaster = abilityCaster;
        this.statusEffectService = statusEffectService;
        this.barrierZoneService = barrierZoneService;
        this.particleThrottler = particleThrottler;
        this.vfxService = vfxService;
    }

    public PlayerDataService getPlayerDataService() {
        return playerDataService;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public AbilityRegistry getAbilityRegistry() {
        return abilityRegistry;
    }

    public AbilityCaster getAbilityCaster() {
        return abilityCaster;
    }

    public StatusEffectService getStatusEffectService() {
        return statusEffectService;
    }

    public BarrierZoneService getBarrierZoneService() {
        return barrierZoneService;
    }

    public ParticleThrottler getParticleThrottler() {
        return particleThrottler;
    }

    public VfxService getVfxService() {
        return vfxService;
    }
}
