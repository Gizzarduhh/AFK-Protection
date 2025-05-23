package me.Gizzarduhh.afkProtection;

public class AFKTimer implements Runnable {

    private final AFKProtection plugin;

    public AFKTimer(AFKProtection plugin)
    {this.plugin = plugin;}

    public void run() {
        plugin.getServer().getOnlinePlayers().forEach(plugin::updateAfkTime);
    }
}
