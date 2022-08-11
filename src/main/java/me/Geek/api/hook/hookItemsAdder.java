package me.Geek.api.hook;

import dev.lone.itemsadder.api.CustomStack;
import me.Geek.GeekMail;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

/**
 * 作者: 老廖
 * 时间: 2022/8/11
 **/
public final class hookItemsAdder {

    public static ItemStack getItemsAdder(String id) {
        return CustomStack.getInstance(id).getItemStack();
    }
    public static void hook() {
        GeekMail.say("&7软依赖 &fItemsAdder &7已兼容.");
    }

}
