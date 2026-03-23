package me.Gizzarduhh.afkProtection.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.Gizzarduhh.afkProtection.AFKProtection;

public class AFKProtCommand {
    private final AFKProtection plugin;

    public AFKProtCommand(AFKProtection plugin) {
        this.plugin = plugin;
    }

    public LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("afkprot")
                .then(Commands.literal("reload")
                        .requires(source ->
                                source.getSender().hasPermission("afkprotection.command.reload"))
                        .executes(ctx -> {
                            // Reload config
                            plugin.reloadConfig();
                            // Reset everyone's timer
                            plugin.getServer().getOnlinePlayers().forEach(plugin.afkTimer::resetAfkTime);
                            ctx.getSource().getSender().sendPlainMessage("AFKProtection reloaded!");
                            return 1;
                        })
                );
    }
}
