package me.gizzarduhh.afkprotection.hook;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class PlaceholderApi {
//    private final AfkProtection plugin;
//
//    public PlaceholderApi(AfkProtection plugin) {
//        this.plugin = plugin;
//    }

    public String parse(Player player, String msg) {
        return PlaceholderAPI.setPlaceholders(player, msg);
    }
}
