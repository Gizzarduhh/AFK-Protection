package me.gizzarduhh.afkprotection.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.gizzarduhh.afkprotection.AfkProtection;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class AfkCommand {
    private final AfkProtection plugin;

    public AfkCommand(AfkProtection plugin) {
        this.plugin = plugin;
    }

    public LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("afk")
                .requires(source ->
                        source.getSender().hasPermission("afkprotection.command.afk"))
                .executes(this::afkCommandLogic)
                .build();
    }

    private int afkCommandLogic(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        Entity executor = ctx.getSource().getExecutor();
        int delay = plugin.getConfig().getInt("afk.delay", 0);

        // Can only be executed on online players
        if (!(executor instanceof Player player) || !player.isOnline()) {
            String failMsg = plugin.getConfig().getString(
                    "messages.notaplayer",
                    "&4Only players can be AFK!");

            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(failMsg));

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

        // Send AFK pending message
        String pendingMsgRaw = plugin.getConfig().getString(
                "messages.pending",
                "&7Going AFK in " + delay + " seconds...");
        String pendingMsgParsed = pendingMsgRaw.replace("%delay%", String.valueOf(delay))
        .replace("%player%", executor.getName());

        if (plugin.placeholderApi != null) {
            pendingMsgParsed = plugin.placeholderApi.parse(player, pendingMsgParsed);
        }

        sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(pendingMsgParsed));

        // Start executors timer *delay* seconds away from afk status
        int timerStart = plugin.getConfig().getInt("afk.timer") - delay;

        plugin.afkTimer.setAfkTime(player, timerStart);

        // Notify the user if their afk was canceled
        plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
            if (!player.isOnline() || plugin.afkTimer.isAfk(player)) {
                task.cancel();
            }

            if (this.plugin.afkTimer.getAfkTime(player) < timerStart) {
                String cancelMsgRaw = plugin.getConfig().getString(
                        "messages.canceled",
                        "&4AFK canceled, you moved!");
                String cancelMsgParsed = cancelMsgRaw.replace("%player%", executor.getName());

                if (plugin.placeholderApi != null) {
                    cancelMsgParsed = plugin.placeholderApi.parse(player, cancelMsgParsed);
                }

                sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(cancelMsgParsed));
                task.cancel();
            }
        }, 0, 2);
        return 1;
    }

}
