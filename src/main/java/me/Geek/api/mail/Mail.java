package me.Geek.api.mail;


import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 作者: 老廖
 * 时间: 2022/7/28
 **/
public interface Mail extends Cloneable {
    UUID getMailID();

    MailType getMailType();

    String getTitle();

    UUID getSender();

    UUID getTarget();

    String getText();

    String getState();

    void setState(String state);

    void setTarget(UUID target);
    void setMailID(UUID mailID);

    String getAppendix();
    String getType();

    default double getMoney() {
        return 0;
    }

    default int getPoints() {
        return 0;
    }

    default int getExp() {
        return 0;
    }

    default ItemStack[] getItemStacks() {
        return new ItemStack[0];
    }

    default List<String> getCommand() {
        return new ArrayList<>();
    }

    void SendMail();

    void giveAppendix();



}
