package me.Geek.api.hook;

import me.Geek.GeekMail;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * 作者: 老廖
 * 时间: 2022/8/1
 **/
public final class hookPlugin {

    public static Economy money;
    public static PlayerPointsAPI points;


    public static void onHook() {
        hookEconomy();
        hookPlayerPoints();
    }


    private static void hookEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
            if (rsp != null) {
                GeekMail.say("&7软依赖 &fVault &7已兼容.");
                money = rsp.getProvider();
            }}

    }
    private static void hookPlayerPoints() {
        final Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("PlayerPoints");
        if (plugin != null) {
            GeekMail.say("&7软依赖 &fPlayerPoints &7已兼容.");
            PlayerPoints p = (PlayerPoints) plugin;
            points = p.getAPI();
        }
    }
}
