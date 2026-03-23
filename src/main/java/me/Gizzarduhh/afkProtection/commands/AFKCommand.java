package me.Gizzarduhh.afkProtection.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.Gizzarduhh.afkProtection.AFKProtection;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class AFKCommand {
    private final AFKProtection plugin;

    public AFKCommand(AFKProtection plugin) {
        this.plugin = plugin;
    }

    public LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("afk")
                .requires(source ->
                        source.getSender().hasPermission("afkprotection.command.afk"))
                .executes(this::AFKCommandLogic);
    }

    private int AFKCommandLogic(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        Entity executor = ctx.getSource().getExecutor();
        int delay = plugin.getConfig().getInt("afk.delay", 0);
        int timerStart = plugin.getConfig().getInt("afk.timer") - delay;


        // Can only be executed on online players
        if (!(executor instanceof Player player) || !player.isOnline()) {
            sender.sendPlainMessage("Only players can be AFK!");
            return 0;
        }
        // Cannot already be afk
        if (plugin.afkTimer.isAFK(player)) {
            sender.sendPlainMessage("You are already AFK!");
            return 0;
        }

        // Start executors timer *delay* seconds away from afk status
        sender.sendPlainMessage("Going AFK in " + delay + " seconds...");
        plugin.afkTimer.setAFKTime(player, timerStart);

        // Notify the user if their afk was canceled
        plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
            if (!player.isOnline() || plugin.afkTimer.isAFK(player)) {
                task.cancel();
            }

            if (this.plugin.afkTimer.getAFKTime(player) < timerStart) {
                sender.sendPlainMessage("AFK was canceled, you have moved or interacted.");
                task.cancel();
            }
        } , 0,5);
        return 1;
    }

}
