package me.Gizzarduhh.afkProtection;

import me.Gizzarduhh.afkProtection.listener.PlayerListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PrefixNode;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public final class AFKProtection extends JavaPlugin {

    private final NamespacedKey AFK_TIMER_KEY = new NamespacedKey(this, "afk_timer");
    private LuckPerms luckPerms;
    private int afkTimer;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        afkTimer = getConfig().getInt("afk.timer");

        // LuckPerms API
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) luckPerms = provider.getProvider();

        // Listener and Timer Schedule
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new AFKTimer(this), 20, 20);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getServer().getOnlinePlayers().forEach(this::clearData);
    }

    public void addPrefix(Player player) {
        if (getConfig().getBoolean("prefix.enabled")) {
            User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
            user.data().add(PrefixNode.builder(getConfig().getString("prefix.value", ""),getConfig().getInt("prefix.weight")).build());
            luckPerms.getUserManager().saveUser(user);
        }
    }

    public void removePrefix(Player player) {
        if (getConfig().getBoolean("prefix.enabled")) {
            User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
            user.data().remove(PrefixNode.builder(getConfig().getString("prefix.value", ""),getConfig().getInt("prefix.weight")).build());
            luckPerms.getUserManager().saveUser(user);
        }
    }

    public void clearData(Player player) {
        player.getPersistentDataContainer().remove(AFK_TIMER_KEY);
        removePrefix(player);
    }

    public boolean isAFK(Player player) {
        return player.getPersistentDataContainer().getOrDefault(AFK_TIMER_KEY, PersistentDataType.INTEGER, 0) > afkTimer;
    }

    public void resetAfkTimer(Player player) {
        if (isAFK(player)) {
            removePrefix(player);
            if (getConfig().getBoolean("messages.enabled"))
                getServer().broadcast(Component.text(player.getName() + getConfig().getString("messages.-afk")).color(NamedTextColor.YELLOW));
        }
        player.getPersistentDataContainer().set(AFK_TIMER_KEY, PersistentDataType.INTEGER, 0);
    }

    public void updateAfkTimer(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();

        if (!isAFK(player)) {
            pdc.set(AFK_TIMER_KEY, PersistentDataType.INTEGER, pdc.getOrDefault(AFK_TIMER_KEY, PersistentDataType.INTEGER, 0) + 1);

            // If not AFK before, are you now?
            if (isAFK(player)) {
                addPrefix(player);
                if (getConfig().getBoolean("messages.enabled"))
                     getServer().broadcast(Component.text(player.getName() + getConfig().getString("messages.+afk")).color(NamedTextColor.YELLOW));
            }
        }
    }
}
