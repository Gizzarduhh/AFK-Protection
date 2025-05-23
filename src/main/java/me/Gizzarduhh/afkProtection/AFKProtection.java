package me.Gizzarduhh.afkProtection;

import me.Gizzarduhh.afkProtection.listener.PlayerListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;


public final class AFKProtection extends JavaPlugin {

    private final NamespacedKey AFK_TIMER_KEY = new NamespacedKey(this, "afk_timer");
    private Configuration config;
    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        config = getConfig();

        // LuckPerms API
        if (getServer().getPluginManager().isPluginEnabled("LuckPerms"))
            luckPerms = LuckPermsProvider.get();

        // Listener and Timer Schedule
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
        removeTag(player);
    }

    public void addTag(Player player) {
        if (config.getBoolean("prefix.enabled")) {
            User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
            user.data().add(PrefixNode.builder(config.getString("prefix.value", ""),config.getInt("prefix.weight")).value(true).build());
            luckPerms.getUserManager().saveUser(user);
        }
        if (config.getBoolean("suffix.enabled")) {
            User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
            user.data().add(SuffixNode.builder(config.getString("suffix.value", ""),config.getInt("suffix.weight")).value(true).build());
            luckPerms.getUserManager().saveUser(user);
        }
    }

    public void removeTag(Player player) {
        if (config.getBoolean("prefix.enabled")) {
            User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
            user.data().remove(PrefixNode.builder(config.getString("prefix.value", ""),config.getInt("prefix.weight")).value(true).build());
            luckPerms.getUserManager().saveUser(user);
        }
        if (config.getBoolean("suffix.enabled")) {
            User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
            user.data().remove(SuffixNode.builder(config.getString("suffix.value", ""),config.getInt("suffix.weight")).value(true).build());
            luckPerms.getUserManager().saveUser(user);
        }
    }

    public boolean isAFK(Player player) {
        return player.getPersistentDataContainer().getOrDefault(AFK_TIMER_KEY, PersistentDataType.INTEGER, 0) > config.getInt("afk.timer");
    }

    public void resetAfkTime(Player player) {
        if (isAFK(player)) {
            removeTag(player);
            if (config.getBoolean("messages.enabled"))
                getServer().broadcast(Component.text(player.getName() + config.getString("messages.-afk")).color(NamedTextColor.YELLOW));
        }
        player.getPersistentDataContainer().set(AFK_TIMER_KEY, PersistentDataType.INTEGER, 0);
    }

    public void updateAfkTime(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();

        if (!isAFK(player)) {
            pdc.set(AFK_TIMER_KEY, PersistentDataType.INTEGER, pdc.getOrDefault(AFK_TIMER_KEY, PersistentDataType.INTEGER, 0) + 1);

            // If not AFK before, are you now?
            if (isAFK(player)) {
                addTag(player);
                if (config.getBoolean("messages.enabled"))
                     getServer().broadcast(Component.text(player.getName() + config.getString("messages.+afk")).color(NamedTextColor.YELLOW));
            }
        }
    }
}
