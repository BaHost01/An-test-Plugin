package com.cstudioss.cursedcombat.data;

import com.cstudioss.cursedcombat.util.MathUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataService {
    private final JavaPlugin plugin;
    private final Map<UUID, PlayerProfile> profiles = new ConcurrentHashMap<>();
    private final NamespacedKey techniqueKey;
    private final NamespacedKey maxCeKey;
    private final NamespacedKey currentCeKey;
    private final NamespacedKey regenKey;

    private double defaultMaxCe;
    private double defaultRegen;
    private String defaultTechnique;

    public PlayerDataService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.techniqueKey = new NamespacedKey(plugin, "technique_id");
        this.maxCeKey = new NamespacedKey(plugin, "max_ce");
        this.currentCeKey = new NamespacedKey(plugin, "current_ce");
        this.regenKey = new NamespacedKey(plugin, "regen_per_second");
    }

    public void reloadDefaults() {
        defaultMaxCe = plugin.getConfig().getDouble("cursed-energy.max", 100.0);
        defaultRegen = plugin.getConfig().getDouble("cursed-energy.regen-per-second", 5.0);
        defaultTechnique = plugin.getConfig().getString("cursed-energy.starting-technique", "basic");
    }

    public PlayerProfile getProfile(UUID playerId) {
        return profiles.get(playerId);
    }

    public void loadProfile(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        String technique = container.getOrDefault(techniqueKey, PersistentDataType.STRING, defaultTechnique);
        double maxCe = container.getOrDefault(maxCeKey, PersistentDataType.DOUBLE, defaultMaxCe);
        double currentCe = container.getOrDefault(currentCeKey, PersistentDataType.DOUBLE, maxCe);
        double regen = container.getOrDefault(regenKey, PersistentDataType.DOUBLE, defaultRegen);
        currentCe = MathUtil.clamp(currentCe, 0, maxCe);
        PlayerProfile profile = new PlayerProfile(player.getUniqueId(), technique, maxCe, currentCe, regen);
        profiles.put(player.getUniqueId(), profile);
    }

    public void saveProfile(Player player) {
        PlayerProfile profile = profiles.get(player.getUniqueId());
        if (profile == null) {
            return;
        }
        PersistentDataContainer container = player.getPersistentDataContainer();
        container.set(techniqueKey, PersistentDataType.STRING, profile.getTechniqueId());
        container.set(maxCeKey, PersistentDataType.DOUBLE, profile.getMaxCursedEnergy());
        container.set(currentCeKey, PersistentDataType.DOUBLE, profile.getCurrentCursedEnergy());
        container.set(regenKey, PersistentDataType.DOUBLE, profile.getRegenPerSecond());
    }

    public void removeProfile(UUID playerId) {
        profiles.remove(playerId);
    }

    public void tickRegen() {
        for (PlayerProfile profile : profiles.values()) {
            double max = profile.getMaxCursedEnergy();
            double current = profile.getCurrentCursedEnergy();
            if (current >= max) {
                continue;
            }
            double regen = profile.getRegenPerSecond();
            profile.setCurrentCursedEnergy(MathUtil.clamp(current + regen, 0, max));
        }
    }
}
