package me.Gizzarduhh.afkProtection.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.util.Tick;
import me.Gizzarduhh.afkProtection.AFKProtection;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.time.Duration;

public class AFKCommand {
    private final AFKProtection plugin;
    private final Configuration config;

    // Constructor to get your main plugin instance
    public AFKCommand(AFKProtection plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("afk")
                .executes(this::AFKCommandLogic);
    }

    private int AFKCommandLogic(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        Entity executor = ctx.getSource().getExecutor();
        int delay = this.plugin.getConfig().getInt("afk.delay", 0);

        // Can only be executed on players
        if (!(executor instanceof Player player)) {
            sender.sendPlainMessage("Only players can be AFK!");
            return Command.SINGLE_SUCCESS;
        }

        // Set the executor as AFK after delay
        sender.sendPlainMessage("Going AFK in " + delay + " seconds...");
        this.plugin.afkTimer.setAFKTime(player, config.getInt("afk.timer") - delay);
        this.plugin.getServer().getScheduler().runTaskLaterAsynchronously(this.plugin, () -> {
            if (this.plugin.afkTimer.getAFKTime(player) < config.getInt("afk.timer")) {
                sender.sendPlainMessage("AFK was canceled, you have moved or interacted.");
            }
        }, Tick.tick().fromDuration(Duration.ofSeconds(delay)));
        return Command.SINGLE_SUCCESS;
    }

}
