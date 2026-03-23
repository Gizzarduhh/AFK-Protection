package me.Gizzarduhh.afkProtection;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.Gizzarduhh.afkProtection.commands.AFKCommand;
import me.Gizzarduhh.afkProtection.commands.AFKProtCommand;
import me.Gizzarduhh.afkProtection.hook.LuckPermsAPI;
import me.Gizzarduhh.afkProtection.listener.PlayerListener;
import me.Gizzarduhh.afkProtection.task.AFKTimer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public final class AFKProtection extends JavaPlugin {
    private LuckPermsAPI luckPermsAPI;
    public AFKTimer afkTimer;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();

        // LuckPerms API
        if (getServer().getPluginManager().isPluginEnabled("LuckPerms"))
            luckPermsAPI = new LuckPermsAPI(this);

        // PlaceholderAPI
        //if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
        //    PlaceholderAPIExpansion placeholderAPIExpansion = new PlaceholderAPIExpansion(this);
        //    placeholderAPIExpansion.register();
        //}

        // Listener and Timer
        afkTimer = new AFKTimer(this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, afkTimer, 20, 20);

        // Command
        AFKCommand afkCommand = new AFKCommand(this);
        AFKProtCommand afkProtCommand = new AFKProtCommand(this);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(afkCommand.createCommand().build());
            commands.register(afkProtCommand.createCommand().build());
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getOnlinePlayers().forEach(
                player -> afkTimer.setAFKTime(player, 0)
        );
    }

    public void addLuckPermsTags(Player player) {
        if (luckPermsAPI != null)
            luckPermsAPI.addTags(player);
    }

    public void removeLuckPermsTags(Player player) {
        if (luckPermsAPI != null)
            luckPermsAPI.removeTags(player);
    }

    public void broadcastAFKStatus(Player player) {
        if (getConfig().getBoolean("messages.enabled")) {
            if (afkTimer.isAFK(player))
                getServer().broadcast(
                        Component
                                .text(getConfig().getString("messages.+afk", "%player% has gone afk")
                                        .replace("%player%", player.getName()))
                                .color(NamedTextColor.YELLOW)
                );
            else
                getServer().broadcast(
                        Component
                                .text(getConfig().getString("messages.-afk", "%player% has returned")
                                        .replace("%player%", player.getName()))
                                .color(NamedTextColor.YELLOW)
                );
        }
    }
}