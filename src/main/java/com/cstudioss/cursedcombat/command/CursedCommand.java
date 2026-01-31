package com.cstudioss.cursedcombat.command;

import com.cstudioss.cursedcombat.ability.AbilityCaster;
import com.cstudioss.cursedcombat.ability.AbilityRegistry;
import com.cstudioss.cursedcombat.ability.CooldownManager;
import com.cstudioss.cursedcombat.combat.BarrierZoneService;
import com.cstudioss.cursedcombat.data.PlayerDataService;
import com.cstudioss.cursedcombat.data.PlayerProfile;
import com.cstudioss.cursedcombat.data.TechniqueDefinition;
import com.cstudioss.cursedcombat.data.TechniqueService;
import com.cstudioss.cursedcombat.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CursedCommand implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;
    private final PlayerDataService playerDataService;
    private final TechniqueService techniqueService;
    private final CooldownManager cooldownManager;
    private final AbilityRegistry abilityRegistry;
    private final AbilityCaster abilityCaster;
    private final BarrierZoneService barrierZoneService;

    public CursedCommand(JavaPlugin plugin,
                         PlayerDataService playerDataService,
                         TechniqueService techniqueService,
                         CooldownManager cooldownManager,
                         AbilityRegistry abilityRegistry,
                         AbilityCaster abilityCaster,
                         BarrierZoneService barrierZoneService) {
        this.plugin = plugin;
        this.playerDataService = playerDataService;
        this.techniqueService = techniqueService;
        this.cooldownManager = cooldownManager;
        this.abilityRegistry = abilityRegistry;
        this.abilityCaster = abilityCaster;
        this.barrierZoneService = barrierZoneService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            TextUtil.send(sender, "&eUsage: /curse <stats|cast|settechnique|reload>");
            return true;
        }
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "stats" -> handleStats(sender);
            case "cast" -> handleCast(sender, args);
            case "settechnique" -> handleSetTechnique(sender, args);
            case "reload" -> handleReload(sender);
            default -> TextUtil.send(sender, "&cUnknown subcommand.");
        }
        return true;
    }

    private void handleStats(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            TextUtil.send(sender, "&cOnly players can use this command.");
            return;
        }
        PlayerProfile profile = playerDataService.getProfile(player.getUniqueId());
        if (profile == null) {
            TextUtil.send(sender, "&cNo profile loaded.");
            return;
        }
        TechniqueDefinition technique = techniqueService.getTechnique(profile.getTechniqueId());
        String techniqueName = technique != null ? technique.getName() : "Unknown";
        TextUtil.send(sender, "&6Cursed Energy: &e" + profile.getCurrentCursedEnergy() + "&7/&e" + profile.getMaxCursedEnergy());
        TextUtil.send(sender, "&6Technique: &e" + techniqueName + " &7(" + profile.getTechniqueId() + ")");
        Map<String, Long> cooldowns = cooldownManager.getCooldowns(player.getUniqueId());
        if (cooldowns.isEmpty()) {
            TextUtil.send(sender, "&7Cooldowns: none");
            return;
        }
        long now = System.currentTimeMillis();
        String summary = cooldowns.entrySet().stream()
                .map(entry -> {
                    long remaining = Math.max(0, entry.getValue() - now);
                    return entry.getKey() + ": " + (remaining / 1000.0) + "s";
                })
                .collect(Collectors.joining(", "));
        TextUtil.send(sender, "&7Cooldowns: " + summary);
    }

    private void handleCast(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            TextUtil.send(sender, "&cOnly players can use this command.");
            return;
        }
        if (!sender.hasPermission("cursedcombat.cast")) {
            TextUtil.send(sender, "&cYou do not have permission to cast abilities via command.");
            return;
        }
        if (args.length < 2) {
            TextUtil.send(sender, "&eUsage: /curse cast <abilityId>");
            return;
        }
        abilityCaster.castAbility(player, args[1].toLowerCase());
    }

    private void handleSetTechnique(CommandSender sender, String[] args) {
        if (!sender.hasPermission("cursedcombat.admin")) {
            TextUtil.send(sender, "&cYou do not have permission to set techniques.");
            return;
        }
        if (args.length < 3) {
            TextUtil.send(sender, "&eUsage: /curse settechnique <player> <techniqueId>");
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            TextUtil.send(sender, "&cPlayer not found.");
            return;
        }
        TechniqueDefinition technique = techniqueService.getTechnique(args[2]);
        if (technique == null) {
            TextUtil.send(sender, "&cUnknown technique.");
            return;
        }
        PlayerProfile profile = playerDataService.getProfile(target.getUniqueId());
        if (profile == null) {
            TextUtil.send(sender, "&cTarget profile not loaded.");
            return;
        }
        profile.setTechniqueId(technique.getId());
        playerDataService.saveProfile(target);
        TextUtil.send(sender, "&aTechnique set to " + technique.getName() + ".");
        TextUtil.send(target, "&aYour technique is now " + technique.getName() + ".");
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("cursedcombat.admin")) {
            TextUtil.send(sender, "&cYou do not have permission to reload.");
            return;
        }
        plugin.reloadConfig();
        playerDataService.reloadDefaults();
        techniqueService.reload();
        barrierZoneService.start();
        TextUtil.send(sender, "&aCursed Combat configuration reloaded.");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("stats", "cast", "settechnique", "reload");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("cast")) {
            return abilityRegistry.getAll().stream()
                    .map(ability -> ability.getId())
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("settechnique")) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("settechnique")) {
            return new ArrayList<>(techniqueService.getTechniques().keySet());
        }
        return Collections.emptyList();
    }
}
