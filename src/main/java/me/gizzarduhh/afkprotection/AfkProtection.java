package me.gizzarduhh.afkprotection;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.gizzarduhh.afkprotection.commands.AfkCommand;
import me.gizzarduhh.afkprotection.commands.AfkProtCommand;
import me.gizzarduhh.afkprotection.hook.LuckPermsAPI;
import me.gizzarduhh.afkprotection.listener.PlayerListener;
import me.gizzarduhh.afkprotection.task.AfkTimer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public final class AfkProtection extends JavaPlugin {
    private LuckPermsAPI luckPermsApi;
    public AfkTimer afkTimer;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();

        // LuckPerms API
        if (getServer().getPluginManager().isPluginEnabled("LuckPerms")) {
            luckPermsApi = new LuckPermsAPI(this);
        }

        // PlaceholderAPI
        //if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
        //    PlaceholderAPIExpansion placeholderAPIExpansion = new PlaceholderAPIExpansion(this);
        //    placeholderAPIExpansion.register();
        //}

        // Listener and Timer
        afkTimer = new AfkTimer(this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, afkTimer, 20, 20);

        // Command
        AfkCommand afkCommand = new AfkCommand(this);
        AfkProtCommand afkProtCommand = new AfkProtCommand(this);
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(afkCommand.createCommand().build());
            commands.register(afkProtCommand.createCommand().build());
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getOnlinePlayers().forEach(
                player -> afkTimer.setAfkTime(player, 0)
        );
    }

    public void addLuckPermsTags(Player player) {
        if (luckPermsApi != null) {
            luckPermsApi.addTags(player);
        }
    }

    public void removeLuckPermsTags(Player player) {
        if (luckPermsApi != null) {
            luckPermsApi.removeTags(player);
            }
    }

    public void broadcastAfkStatus(Player player) {
        if (getConfig().getBoolean("messages.enabled")) {
            if (afkTimer.isAfk(player))
                getServer().broadcast(
                        Component
                                .text(getConfig().getString(
                                        "messages.+afk", "%player% has gone afk")
                                        .replace("%player%", player.getName()))
                                .color(NamedTextColor.YELLOW)
                );
            else {
                getServer().broadcast(
                        Component
                                .text(getConfig().getString(
                                        "messages.-afk", "%player% has returned")
                                        .replace("%player%", player.getName()))
                                .color(NamedTextColor.YELLOW)
                );
            }
        }
    }
}