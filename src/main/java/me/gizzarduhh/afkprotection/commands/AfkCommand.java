package me.gizzarduhh.afkprotection.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.gizzarduhh.afkprotection.AfkProtection;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class AfkCommand {
    private final AfkProtection plugin;

    public AfkCommand(AfkProtection plugin) {
        this.plugin = plugin;
    }

    public LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("afk")
                .requires(source ->
                        source.getSender().hasPermission("afkprotection.command.afk"))
                .executes(this::afkCommandLogic);
    }

    private int afkCommandLogic(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        Entity executor = ctx.getSource().getExecutor();
        int delay = plugin.getConfig().getInt("afk.delay", 0);

        // Can only be executed on online players
        if (!(executor instanceof Player player) || !player.isOnline()) {
            sender.sendPlainMessage("Only players can be AFK!");
            return 0;
        }
        // Cannot already be afk
        if (plugin.afkTimer.isAfk(player)) {
            sender.sendPlainMessage("You are already AFK!");
            return 0;
        }
        // If no delay, just set em AFK
        if (delay == 0) {
            plugin.afkTimer.setAfkTime(player, plugin.getConfig().getInt("afk.timer"));
            return 1;
        }

        // Start executors timer *delay* seconds away from afk status
        int timerStart = plugin.getConfig().getInt("afk.timer") - delay;

        sender.sendPlainMessage("Going AFK in " + delay + " seconds...");
        plugin.afkTimer.setAfkTime(player, timerStart);

        // Notify the user if their afk was canceled
        plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
            if (!player.isOnline() || plugin.afkTimer.isAfk(player)) {
                task.cancel();
            }

            if (this.plugin.afkTimer.getAfkTime(player) < timerStart) {
                sender.sendPlainMessage("AFK canceled, you have moved or interacted.");
                task.cancel();
            }
        }, 0, 2);
        return 1;
    }

}
