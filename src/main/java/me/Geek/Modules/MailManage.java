package me.Geek.Modules;

import me.Geek.Configuration.ConfigManager;
import me.Geek.Configuration.LangManager;
import me.Geek.GeekMail;
import me.Geek.Libs.Menu.MItem;
import me.Geek.api.mail.Mail;
import me.Geek.api.mail.MailType;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import taboolib.common.platform.ProxyPlayer;
import taboolib.common.platform.function.AdapterKt;
import taboolib.library.xseries.XSound;
import taboolib.module.chat.TellrawJson;
import taboolib.module.nms.MinecraftVersion;
import taboolib.module.nms.NMSKt;
import taboolib.module.nms.type.ToastBackground;
import taboolib.module.nms.type.ToastFrame;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 作者: 老廖
 * 时间: 2022/7/29
 **/
public final class MailManage {

  private static final Map<UUID, List<Mail>> senderCache = new ConcurrentHashMap<>();
    private static final Map<UUID, List<Mail>> targetCache = new ConcurrentHashMap<>();


    /**
     *  此方法会先判断缓存中是否存在对应 目标UID 的数据 再进行存入
     *
     * @param senderUuid 发送者ID
     * @param mail 邮件
     */
    public static void addSenderCache(UUID senderUuid, Mail mail) {
        if (senderCache.containsKey(senderUuid)) {
            senderCache.forEach((key, value) -> {
                if (key.equals(senderUuid)) {
                    value.add(mail);
                }
            });
        } else {
            List<Mail> mail1 = new ArrayList<>();
            mail1.add(mail);
            senderCache.put(senderUuid, mail1);
        }
    }

    /**
     *  此方法会先判断缓存中是否存在对应 目标UID 的数据 再进行存入
     *
     * @param targetUuid 目标ID
     * @param mail 邮件
     */
    public static void addTargetCache(UUID targetUuid, Mail mail) {
        if (targetCache.containsKey(targetUuid)) {
            targetCache.forEach((key, value) -> {
                if (key.equals(targetUuid)) {
                    value.add(mail);
                }
            });
        } else {
            List<Mail> mail1 = new ArrayList<>();
            mail1.add(mail);
            targetCache.put(targetUuid, mail1);
        }
    }

    // 直接替换缓存中的数据
    public static void UpTargetCache(UUID targetUuid, List<Mail> mail) {
        targetCache.put(targetUuid, mail);
    }
    public static void remTargetCache(UUID targetUuid) {
        targetCache.remove(targetUuid);
    }

    public static void remIndexTofTarget(UUID targetUuid, UUID mailID) {
        if (targetCache.containsKey(targetUuid)) {
            targetCache.forEach((key, value) -> {
                if (key.equals(targetUuid)) {
                    value.removeIf(it -> it.getMailID().equals(mailID));
                }
            });
        } else {
            GeekMail.say("缓存 null 异常");
        }
    }
    public static void remSenderCache(UUID sender) {
        senderCache.remove(sender);
    }
    public static void remIndexTofSender(UUID senderUuid, UUID mailID) {
        if (targetCache.containsKey(senderUuid)) {
            senderCache.forEach((key, value) -> {
                value.removeIf(it -> it.getMailID().equals(mailID));
            });
        } else {
            GeekMail.say("缓存 null 异常");
        }
    }

    public static List<Mail> getTargetCache(UUID uuid) {
        if (targetCache.containsKey(uuid)) {
            return new ArrayList<>(targetCache.get(uuid));
        }
       return new ArrayList<>();
    }

    public static List<Mail> getSenderCache(UUID uuid) {
        if (senderCache.containsKey(uuid)) {
            return new ArrayList<>(senderCache.get(uuid));
        }
        return new ArrayList<>();
    }




    public static boolean hasTargetCache(UUID uuid) {
        return targetCache.containsKey(uuid);
    }


    /**
     * 发送邮件
     * @param type 邮件种类
     * @param MailID 邮件唯一ID
     * @param sender 发送者
     * @param target 接收者
     * @param title 标题
     * @param text 文本
     * @param Ex 扩展参数， 可为 int， double
     * @param global 是否全局发送
     * @param players 玩家(为 ITEM_MAIL 打开GUI提供)
     */
    public static void SendMail(MailType type, UUID MailID, UUID sender, UUID target, String title, String text, Object Ex, Boolean global, Player... players) {
        int ints = 0;
        double doubles = 0.0;
        if (Ex instanceof Double) {
            doubles = (Double) Ex;
        }
        if (Ex instanceof Integer) {
            ints = (Integer) Ex;
        }
        switch (type) {
            case MONEY_MAIL: {
                if (global) {
                    new MailMoney(MailID, sender, target, title, text, doubles).SendGlobalMail();
                } else {
                    new MailMoney(MailID, sender, target, title, text, doubles).SendMail();
                }
                return;
            }
            case POINTS_MAIL: {
                if (global) {
                    new MailPoints(MailID, sender, target, title, text, ints).SendGlobalMail();
                } else {
                    new MailPoints(MailID, sender, target, title, text, ints).SendMail();
                }
                return;
            }
            case EXP_MAIL: {
                if (global) {
                    new MailExp(MailID, sender, target, title, text, ints).SendGlobalMail();
                } else {
                    new MailExp(MailID, sender, target, title, text, ints).SendMail();
                }
                return;
            }
            case TEXT_MAIL: {
                if (global) {
                    new MailText(MailID, sender, target, title, text).SendGlobalMail();
                } else {
                    new MailText(MailID, sender, target, title, text).SendMail();
                }
                return;
            }
            case ITEM_MAIL: {
                new MItem(players[0], sender, target, title, text);
            }
        }


    }

    public static void SendMailMessage(@NotNull Mail mail, Player... player) {
        try {

            if (player[0] != null) {
                for (String msg : LangManager.SENDER_MSG) {
                    if (msg.contains("[target]")) {
                        if (player[1] == null) {
                            player[0].sendMessage(msg.replace("[target]", ""));
                        } else {
                            player[0].sendMessage(msg.replace("[target]", player[1].getName()));
                        }
                    } else {
                        player[0].sendMessage(msg);
                    }
                    MailManage.Sound(player[0], "BLOCK_NOTE_BLOCK_HARP",1f, 1f);
                }
            }
            if (player[1] != null) {
                TellrawJson tellrawJson = new TellrawJson();
                ProxyPlayer proxyPlayer = AdapterKt.adaptPlayer(player[1]);
                for (String msg : LangManager.TARGET_MSG) {
                    if (msg.contains("[title]")) {
                        tellrawJson
                                .append(msg
                                        .replace("[title]", mail.getTitle() + "\n"))
                                .hoverText(mail.getText())
                                .runCommand("/" + GeekMail.menu.cmd);
                    } else {
                        tellrawJson.append(msg + "\n");
                    }
                }
                if (MinecraftVersion.INSTANCE.getMajorLegacy() >= 11300) {
                    NMSKt.sendToast(player[1], Material.BOOK,"你有新的邮件待查看！", ToastFrame.TASK, ToastBackground.END);
                }
                proxyPlayer.playSound(proxyPlayer.getLocation(), "BLOCK_NOTE_BLOCK_HARP", 1f, 1f);
                proxyPlayer.sendRawMessage(tellrawJson.toRawMessage());
            }
        } catch (IllegalArgumentException ignored){}
    }

    public static void CreateBlock(String location) {
        try {
            FileConfiguration data = YamlConfiguration.loadConfiguration(ConfigManager.getYml());
            data.set("Block", location);
            data.save(ConfigManager.getYml());
            ConfigManager.location = location;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Sound(Player player, String name, Float volume, Float potch) {
        XSound sound;
        try {
            sound = XSound.valueOf(name);
        } catch (Throwable e) {
            GeekMail.say("未知音效: "+name);
            return;
        }
        sound.play(player, volume, potch);
    }

}
