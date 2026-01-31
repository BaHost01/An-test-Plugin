package com.cstudioss.cursedcombat.ability;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AbilityRegistry {
    private final Map<String, Ability> abilities = new HashMap<>();

    public void register(Ability ability) {
        abilities.put(ability.getId(), ability);
    }

    public Ability get(String id) {
        return abilities.get(id);
    }

    public Collection<Ability> getAll() {
        return Collections.unmodifiableCollection(abilities.values());
    }
}
