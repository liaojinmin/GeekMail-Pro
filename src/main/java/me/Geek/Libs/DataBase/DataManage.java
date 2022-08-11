package me.Geek.Libs.DataBase;

import com.google.common.base.Joiner;
import me.Geek.Configuration.ConfigManager;
import me.Geek.GeekMail;
import me.Geek.Libs.serialize.base64.StreamSerializer;
import me.Geek.Modules.*;
import me.Geek.api.mail.Mail;
import me.Geek.api.mail.MailType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.*;

/**
 * 作者: 老廖
 * 时间: 2022/7/23
 **/
public final class DataManage {
    private static DataSub dataSub;


  //  public DataManage() { start();}

    public static Connection getConnection() throws SQLException {
        return dataSub.getConnection();
    }

    public static void closeData() {
        dataSub.onStop();
    }

    public static void start() {
        if (ConfigManager.DATA_TYPE.equalsIgnoreCase("mysql")) {
            dataSub = new mysql();
            dataSub.onLoad();
        } else {
            dataSub = new sqlite();
            dataSub.onLoad();
        }
    }

    public static void insert(@NotNull Mail mailDate, ItemStack... itemStacks) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement p = connection.prepareStatement(
                    "INSERT INTO mail_data(`mail_id`,`state`,`type`,`sender`,`target`,`title`,`text`,`money`,`points`,`exp`,`item`,`commands`) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)")) {
               // GeekMail.say("insert- 开始插入数据库");
                final String mailId = mailDate.getMailID().toString();
                final String state = mailDate.getState();
                final MailType type = mailDate.getMailType();
                final String sender = mailDate.getSender().toString();
                final String target = mailDate.getTarget().toString();
                final String title = mailDate.getTitle();
                final String text = mailDate.getText();
                final double money = mailDate.getMoney();
                final int points = mailDate.getPoints();
                final int exp = mailDate.getExp();
                final String command = Joiner.on(",").join(mailDate.getCommand());
                p.setString(1, mailId);
                p.setString(2, state);
                p.setString(3, type.toString());
                p.setString(4, sender);
                p.setString(5, target);
                p.setString(6, title);
                p.setString(7, text);
                p.setDouble(8, money);
                p.setInt(9, points);
                p.setInt(10, exp);
                if (itemStacks.length != 0) {
                    p.setString(11, StreamSerializer.serializeItemStacks(itemStacks));
                 //   GeekMail.say("不是空");
                } else {
                    p.setString(11, "null");
                }
                p.setString(12, command);
                p.execute();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void insert(@NotNull Mail mailDate, Collection<? extends Player> players, ItemStack... itemStacks) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement p = connection.prepareStatement(
                    "INSERT INTO mail_data(`mail_id`,`state`,`type`,`sender`,`target`,`title`,`text`,`money`,`points`,`exp`,`item`,`commands`) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)"))
            {
              //  GeekMail.say("insert- 开始插入数据库");
                String mailId;
                final String state = mailDate.getState();
                final MailType type = mailDate.getMailType();
                final String sender = mailDate.getSender().toString();
                final String title = mailDate.getTitle();
                final String text = mailDate.getText();
                final double money = mailDate.getMoney();
                final int points = mailDate.getPoints();
                final int exp = mailDate.getExp();
                final String command = Joiner.on(",").join(mailDate.getCommand());

                for (Player player1 : players) {
                    UUID uuid = player1.getUniqueId();
                    // 获取邮件唯一ID
                    mailId = UUID.randomUUID().toString();
                    // 修改邮件目标
                    mailDate.setTarget(uuid);
                    // 添加目标玩家缓存
                    MailManage.addTargetCache(uuid, mailDate);
                    // 上库操作
                    p.setString(1, mailId);
                    p.setString(2, state);
                    p.setString(3, type.toString());
                    p.setString(4, sender);
                    // target
                    p.setString(5, String.valueOf(uuid));
                    p.setString(6, title);
                    p.setString(7, text);
                    p.setDouble(8, money);
                    p.setInt(9, points);
                    p.setInt(10, exp);
                    if (itemStacks.length != 0) {
                        p.setString(11, StreamSerializer.serializeItemStacks(itemStacks));
                    } else {
                        p.setString(11, "null");
                    }
                    p.setString(12, command);
                    MailManage.SendMailMessage(mailDate, null, player1);
                    p.addBatch();

                }
                p.executeBatch();
               // GeekMail.say("insert- 多条插入完成");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除数据库中指定邮件ID的邮件
     * @param mail_id 邮件唯一标识
     */
    public static void delete(@NotNull UUID mail_id) {
        try (Connection c = getConnection()) {
            try (PreparedStatement s = c.prepareStatement("DELETE FROM `mail_data` WHERE `mail_id`=?;")) {
               // GeekMail.say("delete- 开始执行 删除: "+ mail_id);
                s.setString(1, String.valueOf(mail_id));
                s.execute();
               // GeekMail.say("delete- 执行完成");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * 删除数据库中指定邮件ID的邮件
     * @param targetID 邮目标玩家
     */
    public static void delete(@NotNull UUID targetID, String state) {
        try (Connection c = getConnection()) {
            try (PreparedStatement s = c.prepareStatement("DELETE FROM `mail_data` WHERE `target`=? AND `state`=?;")) {
                // GeekMail.say("delete- 开始执行 删除: "+ mail_id);
                s.setString(1, String.valueOf(targetID));
                s.setString(2, state);
                s.execute();
                // GeekMail.say("delete- 执行完成");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向数据库查询该玩家的所有邮件信息 。
     * @param targetUid 目标玩家
     * @return 邮件合集
     */
    public static List<Mail> selectTarget(@NotNull UUID targetUid) {
        List<Mail> mail;

        try (Connection c = getConnection()) {
            try (PreparedStatement s = c.prepareStatement("SELECT * FROM `mail_data` WHERE target=?;")) {
                s.setString(1, String.valueOf(targetUid));
                ResultSet r = s.executeQuery();
                if (!r.isBeforeFirst()) return null;
                mail = new ArrayList<>();
                while (r.next()) {
                    final UUID MailID = UUID.fromString(r.getString("mail_id"));
                    final String state = r.getString("state");
                    final MailType type = MailType.valueOf(r.getString("type"));
                    final UUID sender = UUID.fromString(r.getString("sender"));
                    final UUID target = UUID.fromString(r.getString("target"));
                    final String title = r.getString("title");
                    final String text = r.getString("text");
                    final double money = r.getDouble("money");
                    final int points = r.getInt("points");
                    final int exp = r.getInt("exp");
                    final ItemStack[] itemStacks = StreamSerializer.deserializeItemStacks(r.getString("item"));
                    final List<String> commands = Arrays.asList(r.getString("commands").split(","));
                    forType(mail, MailID, state, type, sender, target, title, text, money, points, exp, itemStacks, commands);
                }
                return mail;
            }
        } catch (SQLException | IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 邮件ID
    public static void update(@NotNull UUID mail_id) {
        try (Connection c = getConnection()) {
            try (PreparedStatement s = c.prepareStatement("UPDATE `mail_data` SET `state`=? WHERE `mail_id`=?;")) {
               // GeekMail.say("update- 开始更新数据库");
                s.setString(1, "已提取");
                s.setString(2, String.valueOf(mail_id));
                s.executeUpdate();
              //  GeekMail.say("update- 更新完毕");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void forType(List<Mail> mail, UUID MailID, String state, MailType type, UUID sender, UUID target, String title, String text, double money, int points, int exp, ItemStack[] itemStacks, List<String> commands) {
        switch (type) {
            case MONEY_MAIL: {
                //   GeekMail.say(" 添加数据 MONEY_MAIL");
                mail.add(new MailMoney(MailID, state, sender, target, title, text, money));
                break;
            }
            case POINTS_MAIL: {
                mail.add(new MailPoints(MailID, state, sender, target, title, text, points));
                break;
            }
            case EXP_MAIL: {
                mail.add(new MailExp(MailID, state, sender, target, title, text, exp));
                break;
            }
            case TEXT_MAIL: {
                mail.add(new MailText(MailID, sender, target, title, text));
                break;
            }
            case ITEM_MAIL: {
                mail.add(new MailItem(MailID, state, sender, target, title, text, itemStacks));
                break;
            }
        }
    }



}
