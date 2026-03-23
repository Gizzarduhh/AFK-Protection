package me.Gizzarduhh.afkProtection.task;

import me.Gizzarduhh.afkProtection.AFKProtection;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class AFKTimer implements Runnable {
    private final AFKProtection plugin;
    private final NamespacedKey AFK_TIMER_KEY;

    public AFKTimer(AFKProtection plugin) {
        this.plugin = plugin;
        AFK_TIMER_KEY = new NamespacedKey(this.plugin, "afk_timer");
    }

    public void run() {
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            // Since we do not store an AFK status, we need to stop counting already AFK players
            if (!isAFK(player)) {
                countAfkTime(player);
                // After count, check and update status
                if (isAFK(player)) {
                    plugin.addLuckPermsTags(player);
                    plugin.broadcastAFKStatus(player);
                }
            }
        });
    }

    public void countAfkTime(Player player) {
        if (!isAFK(player)) {
            PersistentDataContainer pdc = player.getPersistentDataContainer();
            pdc.set(
                    AFK_TIMER_KEY,
                    PersistentDataType.INTEGER,
                    pdc.getOrDefault(AFK_TIMER_KEY, PersistentDataType.INTEGER, 0) + 1);

        }
    }

    public void resetAfkTime(Player player) {
        // Since we do not store an AFK status, we need to check status first
        boolean wasAFK = isAFK(player);
        player.getPersistentDataContainer().set(
                AFK_TIMER_KEY,
                PersistentDataType.INTEGER,
                0);
        if (wasAFK) {
            plugin.removeLuckPermsTags(player);
            plugin.broadcastAFKStatus(player);
        }
    }

    public void setAFKTime(Player player, int time) {
        player.getPersistentDataContainer().set(
                AFK_TIMER_KEY,
                PersistentDataType.INTEGER,
                time);
    }

    public int getAFKTime(Player player) {
        return player.getPersistentDataContainer().getOrDefault(
                AFK_TIMER_KEY,
                PersistentDataType.INTEGER, 0);
    }

    public boolean isAFK(Player player) {
        return player.getPersistentDataContainer().getOrDefault(
                AFK_TIMER_KEY,
                PersistentDataType.INTEGER,
                0) > plugin.getConfig().getInt("afk.timer");
    }
}
