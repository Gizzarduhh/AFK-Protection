package me.Gizzarduhh.afkProtection.hook;

import me.Gizzarduhh.afkProtection.AFKProtection;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import org.bukkit.entity.Player;

public class LuckPermsAPI
{
    private final AFKProtection plugin;
    private static LuckPerms luckPerms;

    public LuckPermsAPI(AFKProtection plugin) {
        this.plugin = plugin;
        luckPerms = LuckPermsProvider.get();
    }

    public void addTags(Player player) {
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        if (plugin.getConfig().getBoolean("prefix.enabled")) {
            user.data()
                    .add(PrefixNode.builder(
                            plugin.getConfig().getString("prefix.value", ""),
                            plugin.getConfig().getInt("prefix.weight", 1))
                            .value(true)
                            .build());
            luckPerms.getUserManager().saveUser(user);
        }
        if (plugin.getConfig().getBoolean("suffix.enabled")) {
            user.data()
                    .add(SuffixNode.builder(
                            plugin.getConfig().getString("suffix.value", ""),
                            plugin.getConfig().getInt("suffix.weight", 1))
                            .value(true)
                            .build());
            luckPerms.getUserManager().saveUser(user);
        }
    }

    public void removeTags(Player player) {
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        if (plugin.getConfig().getBoolean("prefix.enabled")) {
            user.data()
                    .remove(PrefixNode.builder(
                            plugin.getConfig().getString("prefix.value", ""),
                            plugin.getConfig().getInt("prefix.weight", 1))
                            .value(true)
                            .build());
            luckPerms.getUserManager().saveUser(user);
        }
        if (plugin.getConfig().getBoolean("suffix.enabled")) {
            user.data()
                    .remove(SuffixNode.builder(
                            plugin.getConfig().getString("suffix.value", ""),
                            plugin.getConfig().getInt("suffix.weight", 1))
                            .value(true)
                            .build());
            luckPerms.getUserManager().saveUser(user);
        }
    }
}
