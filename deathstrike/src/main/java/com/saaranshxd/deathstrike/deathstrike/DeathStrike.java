package com.saaranshxd.deathstrike.deathstrike;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class DeathStrike extends JavaPlugin implements Listener {

    private boolean lightningEnabled = true;
    private boolean soundEnabled = true;
    private boolean messageEnabled = true;
    private int lightningDelay = 10; // ticks (0.5 seconds)

    @Override
    public void onEnable() {
        // Register events
        getServer().getPluginManager().registerEvents(this, this);

        // Load configuration
        saveDefaultConfig();
        loadConfig();

        getLogger().info("DeathStrike plugin has been enabled!");
        getLogger().info("Lightning strikes will occur at death locations!");
    }

    @Override
    public void onDisable() {
        getLogger().info("DeathStrike plugin has been disabled!");
    }

    private void loadConfig() {
        FileConfiguration config = getConfig();
        lightningEnabled = config.getBoolean("lightning.enabled", true);
        soundEnabled = config.getBoolean("sound.enabled", true);
        messageEnabled = config.getBoolean("message.enabled", true);
        lightningDelay = config.getInt("lightning.delay", 10);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!lightningEnabled) return;

        Player player = event.getEntity();
        Location deathLocation = player.getLocation();

        // Schedule lightning strike with configurable delay
        getServer().getScheduler().runTaskLater(this, () -> {
            // Strike lightning at death location
            player.getWorld().strikeLightning(deathLocation);

            // Play thunder sound if enabled
            if (soundEnabled) {
                player.getWorld().playSound(deathLocation, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1.0f, 1.0f);
            }

            // Send message to all players if enabled
            if (messageEnabled) {
                String message = "§6⚡ §c" + player.getName() + " §6has been struck by divine lightning! ⚡";
                getServer().broadcastMessage(message);
            }

        }, lightningDelay);

        // Log the event
        getLogger().info("Lightning will strike at " + player.getName() + "'s death location: " +
                deathLocation.getBlockX() + ", " + deathLocation.getBlockY() + ", " + deathLocation.getBlockZ());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("deathstrike")) {
            if (!sender.hasPermission("deathstrike.admin")) {
                sender.sendMessage("§cYou don't have permission to use this command!");
                return true;
            }

            if (args.length == 0) {
                sender.sendMessage("§6=== DeathStrike Plugin ===");
                sender.sendMessage("§e/deathstrike toggle - Toggle lightning strikes");
                sender.sendMessage("§e/deathstrike reload - Reload configuration");
                sender.sendMessage("§e/deathstrike status - Show current settings");
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "toggle":
                    lightningEnabled = !lightningEnabled;
                    getConfig().set("lightning.enabled", lightningEnabled);
                    saveConfig();
                    sender.sendMessage("§6Lightning strikes " + (lightningEnabled ? "§aenabled" : "§cdisabled"));
                    break;

                case "reload":
                    reloadConfig();
                    loadConfig();
                    sender.sendMessage("§aConfiguration reloaded!");
                    break;

                case "status":
                    sender.sendMessage("§6=== DeathStrike Status ===");
                    sender.sendMessage("§eLightning: " + (lightningEnabled ? "§aEnabled" : "§cDisabled"));
                    sender.sendMessage("§eSound: " + (soundEnabled ? "§aEnabled" : "§cDisabled"));
                    sender.sendMessage("§eMessage: " + (messageEnabled ? "§aEnabled" : "§cDisabled"));
                    sender.sendMessage("§eDelay: §f" + lightningDelay + " ticks");
                    break;

                default:
                    sender.sendMessage("§cUnknown subcommand! Use /deathstrike for help.");
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    public void saveDefaultConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        if (getResource("config.yml") != null) {
            saveResource("config.yml", false);
        } else {
            // Create default config
            FileConfiguration config = getConfig();
            config.set("lightning.enabled", true);
            config.set("lightning.delay", 10);
            config.set("sound.enabled", true);
            config.set("message.enabled", true);
            config.set("message.text", "§6⚡ §c{player} §6has been struck by divine lightning! ⚡");
            saveConfig();
        }
    }
}