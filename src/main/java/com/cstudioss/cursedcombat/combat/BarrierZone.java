package com.cstudioss.cursedcombat.combat;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class BarrierZone {
    private final UUID casterId;
    private final Location center;
    private final double radius;
    private final long expiresAt;
    private final World world;

    public BarrierZone(UUID casterId, Location center, double radius, long expiresAt, World world) {
        this.casterId = casterId;
        this.center = center;
        this.radius = radius;
        this.expiresAt = expiresAt;
        this.world = world;
    }

    public UUID getCasterId() {
        return casterId;
    }

    public Location getCenter() {
        return center;
    }

    public double getRadius() {
        return radius;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public World getWorld() {
        return world;
    }
}
