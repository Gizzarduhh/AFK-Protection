package me.Gizzarduhh.afkProtection.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.Gizzarduhh.afkProtection.AFKProtection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {

    private final AFKProtection plugin;

    public PlayerListener(AFKProtection plugin)
    {this.plugin = plugin;}

    @EventHandler
    void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player && plugin.afkTimer.isAFK(player))
            event.setCancelled(true);
    }

    @EventHandler
    void onEntityTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player player && plugin.afkTimer.isAFK(player))
            event.setCancelled(true);
    }

    @EventHandler
    void onPlayerChat(AsyncChatEvent event) {
        plugin.afkTimer.resetAfkTime(event.getPlayer());
    }

    @EventHandler
    void onPlayerInput(PlayerInputEvent event) {
        plugin.afkTimer.resetAfkTime(event.getPlayer());
    }

    @EventHandler
    void onPlayerInteract(PlayerInteractEvent event) {
        plugin.afkTimer.resetAfkTime(event.getPlayer());
    }

    @EventHandler
    public void onPlayerInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player)
            plugin.afkTimer.resetAfkTime(player);
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        plugin.afkTimer.resetAfkTime(event.getPlayer());
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event)  {
        plugin.afkTimer.resetAfkTime(event.getPlayer());
    }
    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event){
        plugin.afkTimer.resetAfkTime(event.getPlayer());
    }
}
