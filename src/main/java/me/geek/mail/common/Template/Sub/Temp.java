package me.geek.mail.common.Template.Sub;

import org.bukkit.inventory.ItemStack;

/**
 * 作者: 老廖
 * 时间: 2022/8/8
 **/
public interface Temp {
    String getPackID();
    String getCondition();
    String getAction();
    String getDeny();
    String getTitle();
    String getText();

    String getAppendix();
    ItemStack[] getItemAppendix();


}
