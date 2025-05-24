package me.Gizzarduhh.afkProtection;

import me.Gizzarduhh.afkProtection.hook.LuckPermsAPI;
import me.Gizzarduhh.afkProtection.listener.PlayerListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;


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

        // Listener and Timer
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new AFKTimer(this), 20, 20);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getOnlinePlayers().forEach(this::clearData);
    }

    public void clearData(Player player) {
        player.getPersistentDataContainer().remove(AFK_TIMER_KEY);
        removeAFKEffects(player);
    }

    public void addAFKEffects(Player player) {
        player.setCollidable(false);
        if (luckPermsAPI != null)
            luckPermsAPI.addTags(player);
    }

    public void removeAFKEffects(Player player) {
        player.setCollidable(true);
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
            removeAFKEffects(player);
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
                addAFKEffects(player);
                broadcastAFKStatus(player);
            }
        }
    }
}

