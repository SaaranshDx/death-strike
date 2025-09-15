package com.saaranshxd.deathstrike.deathstrike;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class deathstrike implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        // You can now access the player who died and the items they dropped
        // For example, you can send a message to the player who killed them
        Player killer = player.getKiller();
        if (killer != null) {
            killer.sendMessage("You killed " + player.getName());
        }
    }
}