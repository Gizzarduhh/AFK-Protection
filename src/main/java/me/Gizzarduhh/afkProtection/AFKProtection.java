package me.Gizzarduhh.afkProtection;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.util.Tick;
import me.Gizzarduhh.afkProtection.hook.LuckPermsAPI;
import me.Gizzarduhh.afkProtection.listener.PlayerListener;
import me.Gizzarduhh.afkProtection.task.AFKTimer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;


public final class AFKProtection extends JavaPlugin {

    private final NamespacedKey AFK_TIMER_KEY = new NamespacedKey(this, "afk_timer");
    private LuckPermsAPI luckPermsAPI;
    private Configuration config;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        config = getConfig();

        // LuckPerms API
        if (getServer().getPluginManager().isPluginEnabled("LuckPerms"))
            luckPermsAPI = new LuckPermsAPI(this);

        // PlaceholderAPI
//        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
//            PlaceholderAPIExpansion placeholderAPIExpansion = new PlaceholderAPIExpansion(this);
//            placeholderAPIExpansion.register();
//        }

        // Listener and Timer
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new AFKTimer(this), 20, 20);

        // Command
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            LiteralArgumentBuilder<CommandSourceStack> afkCommand = Commands.literal("afk")
                    .executes(ctx -> {
                        CommandSender sender = ctx.getSource().getSender();
                        Entity executor = ctx.getSource().getExecutor();
                        int delay = config.getInt("afk.delay", 0);

                        // Can only be executed on players
                        if (!(executor instanceof Player player)) {
                            sender.sendPlainMessage("Only players can be AFK!");
                            return Command.SINGLE_SUCCESS;
                        }

                        // Set the executor as AFK after delay
                        sender.sendPlainMessage("Going AFK in " + delay + " seconds...");
                        this.getServer().getScheduler().runTaskLater(this, () -> {
                            if (player.isOnline()) {
                                player.getPersistentDataContainer().set(
                                        AFK_TIMER_KEY,
                                        PersistentDataType.INTEGER,
                                        config.getInt("afk.timer")
                                );
                            }
                        }, Tick.tick().fromDuration(Duration.ofSeconds(delay)));
                        return Command.SINGLE_SUCCESS;
                    });

            commands.registrar().register(afkCommand.build());
        });
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getOnlinePlayers().forEach(this::cleanup);
    }

    public void cleanup(Player player) {
        player.getPersistentDataContainer().remove(AFK_TIMER_KEY);
        removeLuckPermsTags(player);
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
        if (config.getBoolean("messages.enabled")) {
            if (isAFK(player))
                getServer().broadcast(
                        Component
                                .text(config.getString("messages.+afk", "%player% has gone afk")
                                        .replace("%player%",player.getName()))
                                .color(NamedTextColor.YELLOW)
                );
            else
                getServer().broadcast(
                        Component
                                .text(config.getString("messages.-afk", "%player% has returned")
                                        .replace("%player%",player.getName()))
                                .color(NamedTextColor.YELLOW)
                );
        }
    }

    public boolean isAFK(Player player) {
        return player.getPersistentDataContainer().getOrDefault(AFK_TIMER_KEY, PersistentDataType.INTEGER, 0) > config.getInt("afk.timer");
    }

    public void resetAfkTime(Player player) {
        boolean wasAFK = false;
        if (isAFK(player)) {
            wasAFK = true;
            removeLuckPermsTags(player);
        }

        player.getPersistentDataContainer().set(AFK_TIMER_KEY, PersistentDataType.INTEGER, 0);
        if (wasAFK) broadcastAFKStatus(player);
    }

    public void updateAfkTime(Player player) {
        if (!isAFK(player)) {
            PersistentDataContainer pdc = player.getPersistentDataContainer();
            pdc.set(AFK_TIMER_KEY, PersistentDataType.INTEGER, pdc.getOrDefault(AFK_TIMER_KEY, PersistentDataType.INTEGER, 0) + 1);

            // If not AFK before, are you now?
            if (isAFK(player)) {
                addLuckPermsTags(player);
                broadcastAFKStatus(player);
            }
        }
    }
}

