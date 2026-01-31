package com.cstudioss.cursedcombat.data;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TechniqueService {
    private final JavaPlugin plugin;
    private final Map<String, TechniqueDefinition> techniques = new HashMap<>();

    public TechniqueService(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        techniques.clear();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("techniques");
        if (section == null) {
            return;
        }
        for (String key : section.getKeys(false)) {
            ConfigurationSection techSection = section.getConfigurationSection(key);
            if (techSection == null) {
                continue;
            }
            String name = techSection.getString("name", key);
            List<String> abilityIds = new ArrayList<>(techSection.getStringList("abilities"));
            techniques.put(key, new TechniqueDefinition(key, name, abilityIds));
        }
    }

    public TechniqueDefinition getTechnique(String id) {
        return techniques.get(id);
    }

    public Map<String, TechniqueDefinition> getTechniques() {
        return Collections.unmodifiableMap(techniques);
    }
}
