package me.gizzarduhh.afkprotection.task;

import me.gizzarduhh.afkprotection.AfkProtection;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class AfkTimer implements Runnable {
    private final AfkProtection plugin;
    private final NamespacedKey afkTimerKey;

    public AfkTimer(AfkProtection plugin) {
        this.plugin = plugin;
        afkTimerKey = new NamespacedKey(this.plugin, "afk_timer");
    }

    public void run() {
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            // Since we do not store an AFK status, we need to stop counting already AFK players
            if (!isAfk(player)) {
                countAfkTime(player);
                // After count, check and update status
                if (isAfk(player)) {
                    plugin.addLuckPermsTags(player);
                    plugin.broadcastAfkStatus(player);
                }
            }
        });
    }

    public void countAfkTime(Player player) {
        if (!isAfk(player)) {
            PersistentDataContainer pdc = player.getPersistentDataContainer();
            pdc.set(
                    afkTimerKey,
                    PersistentDataType.INTEGER,
                    pdc.getOrDefault(afkTimerKey, PersistentDataType.INTEGER, 0) + 1);

        }
    }

    public void resetAfkTime(Player player) {
        // Since we do not store an AFK status, we need to check status first
        boolean afk = isAfk(player);
        player.getPersistentDataContainer().set(
                afkTimerKey,
                PersistentDataType.INTEGER,
                0);
        if (afk) {
            plugin.removeLuckPermsTags(player);
            plugin.broadcastAfkStatus(player);
        }
    }

    public void setAfkTime(Player player, int time) {
        player.getPersistentDataContainer().set(
                afkTimerKey,
                PersistentDataType.INTEGER,
                time);
    }

    public int getAfkTime(Player player) {
        return player.getPersistentDataContainer().getOrDefault(
                afkTimerKey,
                PersistentDataType.INTEGER, 0);
    }

    public boolean isAfk(Player player) {
        return player.getPersistentDataContainer().getOrDefault(
                afkTimerKey,
                PersistentDataType.INTEGER,
                0) > plugin.getConfig().getInt("afk.timer");
    }
}
