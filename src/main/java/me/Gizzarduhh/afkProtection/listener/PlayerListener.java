package me.Gizzarduhh.afkProtection.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.Gizzarduhh.afkProtection.AFKProtection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final AFKProtection plugin;

    public PlayerListener(AFKProtection plugin)
    {this.plugin = plugin;}

    @EventHandler
    void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() != null && event.getTarget().getType() == EntityType.PLAYER && plugin.isAFK((Player) event.getTarget()))
            event.setCancelled(true);
    }

    @EventHandler
    void onPlayerChat(AsyncChatEvent event){
        plugin.resetAfkTimer(event.getPlayer());
    }

    @EventHandler
    void onPlayerMove(PlayerMoveEvent event){
        plugin.resetAfkTimer(event.getPlayer());
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event){
        plugin.removeKey(event.getPlayer());
    }

    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event){
        plugin.removeKey(event.getPlayer());
    }

    @EventHandler
    void onPlayerKick(PlayerKickEvent event){
        plugin.removeKey(event.getPlayer());
    }
}
