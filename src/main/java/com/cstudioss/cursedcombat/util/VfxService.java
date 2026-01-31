package com.cstudioss.cursedcombat.util;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class VfxService {
    private final JavaPlugin plugin;
    private final ParticleThrottler particleThrottler;

    public VfxService(JavaPlugin plugin, ParticleThrottler particleThrottler) {
        this.plugin = plugin;
        this.particleThrottler = particleThrottler;
    }

    public void play(String path, Location location, UUID throttleId) {
        if (location == null) {
            return;
        }
        ConfigurationSection section = plugin.getConfig().getConfigurationSection(path);
        if (section == null || !section.getBoolean("enabled", true)) {
            return;
        }
        World world = location.getWorld();
        if (world == null) {
            return;
        }
        if (throttleId != null && !particleThrottler.canSpawn(throttleId)) {
            return;
        }
        String particleName = section.getString("particle.type", "");
        if (!particleName.isBlank()) {
            try {
                Particle particle = Particle.valueOf(particleName.toUpperCase());
                int count = section.getInt("particle.count", 12);
                double offsetX = section.getDouble("particle.offset.x", 0.2);
                double offsetY = section.getDouble("particle.offset.y", 0.2);
                double offsetZ = section.getDouble("particle.offset.z", 0.2);
                double extra = section.getDouble("particle.extra", 0.01);
                world.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra);
            } catch (IllegalArgumentException ignored) {
                plugin.getLogger().warning("Invalid particle type in config: " + particleName);
            }
        }
        String soundName = section.getString("sound.type", "");
        if (!soundName.isBlank()) {
            try {
                Sound sound = Sound.valueOf(soundName.toUpperCase());
                float volume = (float) section.getDouble("sound.volume", 0.8);
                float pitch = (float) section.getDouble("sound.pitch", 1.0);
                world.playSound(location, sound, volume, pitch);
            } catch (IllegalArgumentException ignored) {
                plugin.getLogger().warning("Invalid sound type in config: " + soundName);
            }
        }
    }
}
