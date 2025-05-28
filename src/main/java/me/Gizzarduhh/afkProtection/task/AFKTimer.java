package me.Gizzarduhh.afkProtection.task;

import me.Gizzarduhh.afkProtection.AFKProtection;

public class AFKTimer implements Runnable {

    private final AFKProtection plugin;

    public AFKTimer(AFKProtection plugin)
    {this.plugin = plugin;}

    public void run() {
        plugin.getServer().getOnlinePlayers().forEach(plugin::updateAfkTime);
    }
}
