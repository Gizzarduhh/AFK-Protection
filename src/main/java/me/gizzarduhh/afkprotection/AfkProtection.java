package me.gizzarduhh.afkprotection;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.gizzarduhh.afkprotection.commands.AfkCommand;
import me.gizzarduhh.afkprotection.commands.AfkProtCommand;
import me.gizzarduhh.afkprotection.hook.LuckPermsApi;
import me.gizzarduhh.afkprotection.hook.PlaceholderApi;
import me.gizzarduhh.afkprotection.listener.PlayerListener;
import me.gizzarduhh.afkprotection.task.AfkTimer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public final class AfkProtection extends JavaPlugin {
    private LuckPermsApi luckPermsApi;
    public PlaceholderApi placeholderApi;
    public AfkTimer afkTimer;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();

        // LuckPerms API
        if (getServer().getPluginManager().isPluginEnabled("LuckPerms")) {
            luckPermsApi = new LuckPermsApi(this);
            getLogger().info("Successfully hooked into LuckPerms!");
        }

        // PlaceholderAPI
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            placeholderApi = new PlaceholderApi();
            getLogger().info("Successfully hooked into PlaceholderAPI!");
        }

        // Listener and Timer
        afkTimer = new AfkTimer(this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, afkTimer, 20, 20);

        // Command
        AfkCommand afkCommand = new AfkCommand(this);
        AfkProtCommand afkProtCommand = new AfkProtCommand(this);
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(
                    afkCommand.createCommand(),
                    "Set yourself as AFK"
            );
            commands.registrar().register(
                    afkProtCommand.createCommand(),
                    "Reload the plugin"
            );
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
        if (!getConfig().getBoolean("messages.broadcast")) return;

        String raw = afkTimer.isAfk(player) ?
                getConfig().getString("messages.+afk", "&e%player% has gone afk") :
                getConfig().getString("messages.-afk", "&e%player% has returned");
        String parsed = raw.replace("%player%", player.getName());

        if (placeholderApi != null) {
            parsed = placeholderApi.parse(player, parsed);
        }

        getServer().broadcast(LegacyComponentSerializer.legacyAmpersand().deserialize(parsed));
    }
}