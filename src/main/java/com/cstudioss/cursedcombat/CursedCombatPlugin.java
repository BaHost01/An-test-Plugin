package com.cstudioss.cursedcombat;

import com.cstudioss.cursedcombat.ability.AbilityCaster;
import com.cstudioss.cursedcombat.ability.AbilityRegistry;
import com.cstudioss.cursedcombat.ability.BarrierZoneAbility;
import com.cstudioss.cursedcombat.ability.BindingSnapAbility;
import com.cstudioss.cursedcombat.ability.CooldownManager;
import com.cstudioss.cursedcombat.ability.CursedDashAbility;
import com.cstudioss.cursedcombat.ability.EnergyBurstAbility;
import com.cstudioss.cursedcombat.ability.ImpactStrikeAbility;
import com.cstudioss.cursedcombat.combat.BarrierZoneService;
import com.cstudioss.cursedcombat.combat.DamageListener;
import com.cstudioss.cursedcombat.combat.StatusEffectService;
import com.cstudioss.cursedcombat.command.CursedCommand;
import com.cstudioss.cursedcombat.core.ServiceRegistry;
import com.cstudioss.cursedcombat.data.PlayerDataListener;
import com.cstudioss.cursedcombat.data.PlayerDataService;
import com.cstudioss.cursedcombat.data.TechniqueService;
import com.cstudioss.cursedcombat.util.ParticleThrottler;
import com.cstudioss.cursedcombat.util.VfxService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CursedCombatPlugin extends JavaPlugin {
    private ServiceRegistry services;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        PlayerDataService playerDataService = new PlayerDataService(this);
        playerDataService.reloadDefaults();

        TechniqueService techniqueService = new TechniqueService(this);
        techniqueService.reload();

        long throttleMs = getConfig().getLong("cursed-energy.particle-throttle-ms", 150L);
        ParticleThrottler particleThrottler = new ParticleThrottler(throttleMs);
        VfxService vfxService = new VfxService(this, particleThrottler);

        StatusEffectService statusEffectService = new StatusEffectService(this);
        BarrierZoneService barrierZoneService = new BarrierZoneService(this, statusEffectService, vfxService);

        CooldownManager cooldownManager = new CooldownManager();
        AbilityRegistry abilityRegistry = new AbilityRegistry();
        abilityRegistry.register(new CursedDashAbility(this, vfxService));
        abilityRegistry.register(new ImpactStrikeAbility(this, statusEffectService, vfxService));
        abilityRegistry.register(new BarrierZoneAbility(this, barrierZoneService));
        abilityRegistry.register(new EnergyBurstAbility(this, statusEffectService, vfxService));
        abilityRegistry.register(new BindingSnapAbility(this, statusEffectService, vfxService));

        AbilityCaster abilityCaster = new AbilityCaster(this, playerDataService, techniqueService, cooldownManager, abilityRegistry, statusEffectService);

        services = new ServiceRegistry(
                playerDataService,
                cooldownManager,
                abilityRegistry,
                abilityCaster,
                statusEffectService,
                barrierZoneService,
                particleThrottler,
                vfxService
        );

        getServer().getPluginManager().registerEvents(new PlayerDataListener(playerDataService), this);
        getServer().getPluginManager().registerEvents(new DamageListener(statusEffectService), this);

        CursedCommand cursedCommand = new CursedCommand(this, playerDataService, techniqueService, cooldownManager, abilityRegistry, abilityCaster, barrierZoneService);
        if (getCommand("curse") != null) {
            getCommand("curse").setExecutor(cursedCommand);
            getCommand("curse").setTabCompleter(cursedCommand);
        }

        barrierZoneService.start();

        getServer().getScheduler().runTaskTimer(this, playerDataService::tickRegen, 20L, 20L);
        getServer().getScheduler().runTaskTimer(this, () -> {
            for (Player player : getServer().getOnlinePlayers()) {
                services.getStatusEffectService().cleanupExpired(player);
            }
        }, 40L, 40L);

        getLogger().info("CursedCombat enabled.");
    }

    @Override
    public void onDisable() {
        if (services != null) {
            for (Player player : getServer().getOnlinePlayers()) {
                services.getPlayerDataService().saveProfile(player);
            }
            services.getBarrierZoneService().stop();
        }
        getLogger().info("CursedCombat disabled.");
    }
}
