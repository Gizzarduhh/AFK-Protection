package me.gizzarduhh.afkprotection.hook;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.gizzarduhh.afkprotection.AfkProtection;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class AFKPlaceholderExpansion extends PlaceholderExpansion {

    private final AfkProtection plugin;

    public AFKPlaceholderExpansion(AfkProtection plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getAuthor() {
        return "gizzarduhh";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "afkprot";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null || player.getPlayer() == null || !player.isOnline()) {
            return null;
        }

        boolean isAfk = plugin.afkTimer.isAfk(player.getPlayer());

        // %afkprot_status%
        if (params.equalsIgnoreCase("status")) {
            return isAfk ?
                    plugin.getConfig().getString("papi.status_true","true") :
                    plugin.getConfig().getString("papi.status_false","false");
        }

        // %afkprot_tag%
        if (params.equalsIgnoreCase("tag")) {
            return isAfk ?
                    plugin.getConfig().getString("papi.tag","&7[AFK]") :
                    "";
        }

        return null;
    }
}