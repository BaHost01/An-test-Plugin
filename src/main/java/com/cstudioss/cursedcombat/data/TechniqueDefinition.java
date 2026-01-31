package com.cstudioss.cursedcombat.data;

import java.util.List;

public class TechniqueDefinition {
    private final String id;
    private final String name;
    private final List<String> abilityIds;

    public TechniqueDefinition(String id, String name, List<String> abilityIds) {
        this.id = id;
        this.name = name;
        this.abilityIds = abilityIds;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getAbilityIds() {
        return abilityIds;
    }
}
