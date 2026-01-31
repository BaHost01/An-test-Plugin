package com.cstudioss.cursedcombat.combat;

public enum StatusEffectType {
    STUNNED("stunned"),
    WEAKENED("weakened");

    private final String id;

    StatusEffectType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
