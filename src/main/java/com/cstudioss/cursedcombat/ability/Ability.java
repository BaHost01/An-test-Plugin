package com.cstudioss.cursedcombat.ability;

public interface Ability {
    String getId();

    String getName();

    double getCostCursedEnergy();

    long getCooldownMs();

    CastResult cast(AbilityContext context);
}
