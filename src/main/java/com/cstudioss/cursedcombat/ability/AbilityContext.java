package com.cstudioss.cursedcombat.ability;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class AbilityContext {
    private final Player caster;
    private final Entity targetEntity;
    private final Location targetLocation;
    private final World world;
    private final long timestamp;

    public AbilityContext(Player caster, Entity targetEntity, Location targetLocation, World world, long timestamp) {
        this.caster = caster;
        this.targetEntity = targetEntity;
        this.targetLocation = targetLocation;
        this.world = world;
        this.timestamp = timestamp;
    }

    public Player getCaster() {
        return caster;
    }

    public Entity getTargetEntity() {
        return targetEntity;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public World getWorld() {
        return world;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
