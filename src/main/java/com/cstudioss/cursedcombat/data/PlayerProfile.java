package com.cstudioss.cursedcombat.data;

import java.util.UUID;

public class PlayerProfile {
    private final UUID playerId;
    private String techniqueId;
    private double maxCursedEnergy;
    private double currentCursedEnergy;
    private double regenPerSecond;

    public PlayerProfile(UUID playerId, String techniqueId, double maxCursedEnergy, double currentCursedEnergy, double regenPerSecond) {
        this.playerId = playerId;
        this.techniqueId = techniqueId;
        this.maxCursedEnergy = maxCursedEnergy;
        this.currentCursedEnergy = currentCursedEnergy;
        this.regenPerSecond = regenPerSecond;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getTechniqueId() {
        return techniqueId;
    }

    public void setTechniqueId(String techniqueId) {
        this.techniqueId = techniqueId;
    }

    public double getMaxCursedEnergy() {
        return maxCursedEnergy;
    }

    public void setMaxCursedEnergy(double maxCursedEnergy) {
        this.maxCursedEnergy = maxCursedEnergy;
    }

    public double getCurrentCursedEnergy() {
        return currentCursedEnergy;
    }

    public void setCurrentCursedEnergy(double currentCursedEnergy) {
        this.currentCursedEnergy = currentCursedEnergy;
    }

    public double getRegenPerSecond() {
        return regenPerSecond;
    }

    public void setRegenPerSecond(double regenPerSecond) {
        this.regenPerSecond = regenPerSecond;
    }
}
