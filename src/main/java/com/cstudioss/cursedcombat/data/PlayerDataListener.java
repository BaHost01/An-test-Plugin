package com.cstudioss.cursedcombat.data;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDataListener implements Listener {
    private final PlayerDataService playerDataService;

    public PlayerDataListener(PlayerDataService playerDataService) {
        this.playerDataService = playerDataService;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerDataService.loadProfile(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerDataService.saveProfile(event.getPlayer());
        playerDataService.removeProfile(event.getPlayer().getUniqueId());
    }
}
