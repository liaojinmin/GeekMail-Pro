package me.Geek.api.mail;

import me.Geek.Configuration.ConfigManager;
import me.Geek.Libs.DataBase.DataManage;
import me.Geek.Modules.MailManage;
import me.Geek.api.hook.hookPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * 作者: 老廖
 * 时间: 2022/8/2
 **/
public abstract class MailSub implements Mail{

    /**
     * 提取附件 - 将邮件附件发送给玩家
     */
    public void giveAppendix() {
        if (this.getState().equals("未提取")) {
            switch (this.getMailType()) {
                case MONEY_MAIL: {
                    hookPlugin.money.depositPlayer(Bukkit.getOfflinePlayer(this.getTarget()), this.getMoney());
                    return;
                }
                case POINTS_MAIL: {
                    hookPlugin.points.give(this.getTarget(), this.getPoints());
                    return;
                }
                case EXP_MAIL: {
                    Player p = Bukkit.getPlayer(this.getTarget());
                    if (p != null) {
                        p.giveExp(this.getExp());
                    }
                }
                case ITEM_MAIL: {
                    Player p = Bukkit.getPlayer(this.getTarget());
                    if (p != null) {
                        p.getInventory().addItem(this.getItemStacks());
                    }
                }
            }
        }
    }

    public void SendMail() {
        Player send = Bukkit.getPlayer(getSender());
        Player target = Bukkit.getPlayer(getTarget());
        DataManage.insert(this, getItemStacks());
        if (target != null) {
            // 如果目标玩家在线则载入缓存
            MailManage.addTargetCache(getTarget(), this);
        }
        if (send != null) {
            // 如果发送者在线则载入缓存
            MailManage.addSenderCache(getSender(), this);
        }
        MailManage.SendMailMessage(this, send, target);
    }
    public void SendGlobalMail() {
        Collection<? extends Player> player = Bukkit.getOnlinePlayers();
        DataManage.insert(this, player, getItemStacks());
    }

}
