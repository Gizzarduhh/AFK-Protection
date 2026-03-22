package me.Gizzarduhh.afkProtection.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.Gizzarduhh.afkProtection.AFKProtection;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class AFKCommand {
    private final AFKProtection plugin;
    private final Configuration config;

    public AFKCommand(AFKProtection plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("afk")
                .requires(source -> source.getSender().hasPermission("afkprotection.command.afk"))
                .executes(this::AFKCommandLogic);
    }

    private int AFKCommandLogic(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        Entity executor = ctx.getSource().getExecutor();
        int delay = config.getInt("afk.delay", 0);
        int timerStart = config.getInt("afk.timer") - delay;


        // Can only be executed on players
        if (!(executor instanceof Player player)) {
            sender.sendPlainMessage("Only players can be AFK!");
            return 0;
        }

        // Start executors timer *delay* seconds away from afk status
        sender.sendPlainMessage("Going AFK in " + delay + " seconds...");
        this.plugin.afkTimer.setAFKTime(player, timerStart);

        // Notify the user if their afk was canceled
        this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, task -> {
            if (!player.isOnline() || this.plugin.afkTimer.isAFK(player)) {
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
