package me.gizzarduhh.afkprotection.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.gizzarduhh.afkprotection.AfkProtection;

public class AfkProtCommand {
    private final AfkProtection plugin;

    public AfkProtCommand(AfkProtection plugin) {
        this.plugin = plugin;
    }

    public LiteralArgumentBuilder<CommandSourceStack> createCommand() {
        return Commands.literal("afkprot")
                .then(Commands.literal("reload")
                        .requires(source ->
                                source.getSender().hasPermission("afkprotection.command.reload"))
                        .executes(ctx -> {
                            // Reset everyone's timer
                            plugin.getServer().getOnlinePlayers().forEach(
                                    plugin.afkTimer::resetAfkTime
                            );
                            // Reload config
                            plugin.reloadConfig();
                            ctx.getSource().getSender().sendPlainMessage("AFKProtection reloaded!");
                            return Command.SINGLE_SUCCESS;
                        })
                );
    }
}
