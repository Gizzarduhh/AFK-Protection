package me.Gizzarduhh.afkProtection.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.Gizzarduhh.afkProtection.AFKProtection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {

    private final AFKProtection plugin;

    public PlayerListener(AFKProtection plugin)
    {this.plugin = plugin;}

    @EventHandler
    void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER && plugin.isAFK((Player) event.getEntity()))
            event.setCancelled(true);
    }

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
    void onPlayerInput(PlayerInputEvent event){
        plugin.resetAfkTimer(event.getPlayer());
    }

    @EventHandler
    void onPlayerInteract(PlayerInteractEvent event){
        plugin.resetAfkTimer(event.getPlayer());
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event){
        plugin.clearData(event.getPlayer());
    }

    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event){
        plugin.clearData(event.getPlayer());
    }

    @EventHandler
    void onPlayerKick(PlayerKickEvent event){
        plugin.clearData(event.getPlayer());
    }
}
