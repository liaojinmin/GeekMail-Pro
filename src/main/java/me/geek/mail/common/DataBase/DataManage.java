package me.geek.mail.common.DataBase;

import com.google.common.base.Joiner;
import me.geek.mail.Configuration.ConfigManager;

import me.geek.mail.api.mail.MailManage;
import me.geek.mail.common.serialize.base64.StreamSerializer;
import me.geek.mail.api.mail.MailSub;
import org.bukkit.OfflinePlayer;

import org.jetbrains.annotations.NotNull;

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

    public synchronized static void insert(@NotNull MailSub mailDate) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement p = connection.prepareStatement(
                    "INSERT INTO maildata(`mail_id`,`state`,`type`,`sender`,`target`,`title`,`text`,`additional`,`item`,`commands`,`sendertime`,`gettime`) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)")) {
                p.setString(1,  mailDate.getMailID().toString());
                p.setString(2, mailDate.getState());
                p.setString(3, mailDate.getName());
                p.setString(4, mailDate.getSender().toString());
                p.setString(5, mailDate.getTarget().toString());
                p.setString(6, mailDate.getTitle());
                p.setString(7, mailDate.getText());
                p.setString(8, mailDate.getAdditional());
                if (mailDate.getItemStacks() != null) {
                    p.setString(9, StreamSerializer.serializeItemStacks(mailDate.getItemStacks()));
                } else {
                    p.setString(9, "null");
                }
                if (mailDate.getCommand() != null) {
                    p.setString(10, Joiner.on(",").join(mailDate.getCommand()));
                } else {
                    p.setString(10, "");
                }
                p.setString(11, mailDate.getSenderTime());
                p.setString(12, mailDate.getGetTime());
                p.execute();

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
            try (PreparedStatement s = c.prepareStatement("DELETE FROM `maildata` WHERE `mail_id`=?;")) {
                s.setString(1, String.valueOf(mail_id));
                s.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * 删除所有满足条件的邮件
     * 删除数据库中所有指定状态的邮件
     * @param targetID 邮目标玩家
     * @param state 需要删除的邮件状态
     */
    public static void delete(@NotNull UUID targetID, String state) {
        try (Connection c = getConnection()) {
            try (PreparedStatement s = c.prepareStatement("DELETE FROM `maildata` WHERE `target`=? AND `state`=?;")) {
                s.setString(1, String.valueOf(targetID));
                s.setString(2, state);
                s.execute();
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
    public synchronized static List<MailSub> selectTarget(@NotNull UUID targetUid) {
        List<MailSub> mail;
        try (Connection c = getConnection()) {
            try (PreparedStatement s = c.prepareStatement("SELECT * FROM `maildata` WHERE target=?;")) {
                s.setString(1, String.valueOf(targetUid));
                ResultSet r = s.executeQuery();
                if (!r.isBeforeFirst()) return null;
                mail = new ArrayList<>();
                while (r.next()) {
                    final String MailID = r.getString("mail_id");
                    final String state = r.getString("state");
                    final String type = r.getString("type");
                    final String sender = r.getString("sender");
                    final String target = r.getString("target");
                    final String title = r.getString("title");
                    final String text = r.getString("text");
                    final String additional = r.getString("additional");
                    final String itemStacks = r.getString("item");
                    final String commands = r.getString("commands");
                    final String senderTime = r.getString("sendertime");
                    final String getTime = r.getString("gettime");
                    final MailSub data = MailManage.buildMailClass(MailID, type, title, text, sender, target, state, additional, senderTime, getTime, itemStacks, commands);
                    mail.add(data);
                }
                return mail;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 邮件ID
    public synchronized static void update(@NotNull MailSub mail) {
        try (Connection c = getConnection()) {
            try (PreparedStatement s = c.prepareStatement("UPDATE `maildata` SET `state`=?,`getTime`=? WHERE `mail_id`=?;")) {
                s.setString(1, mail.getState());
                s.setString(2, mail.getGetTime());
                s.setString(3, String.valueOf(mail.getMailID()));
                s.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public synchronized static void insert(@NotNull MailSub mailDate, @NotNull OfflinePlayer[] players) {
        try (Connection connection = getConnection()) {
            try (PreparedStatement p = connection.prepareStatement(
                    "INSERT INTO maildata(`mail_id`,`state`,`type`,`sender`,`target`,`title`,`text`,`additional`,`item`,`commands`,`sendertime`,`gettime`) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)"))
            {
                final String sender = mailDate.getSender().toString();
                String items = "null";
                String command = "";
                for (OfflinePlayer ps : players) {
                    final String mailID = UUID.randomUUID().toString();
                    final String target = ps.getUniqueId().toString();
                    p.setString(1, mailID);
                    p.setString(2, mailDate.getState());
                    p.setString(3, mailDate.getName());
                    p.setString(4, sender);
                    p.setString(5, target);
                    p.setString(6, mailDate.getTitle());
                    p.setString(7, mailDate.getText());
                    p.setString(8, mailDate.getAdditional());
                    if (mailDate.getItemStacks() != null) {
                        items = StreamSerializer.serializeItemStacks(mailDate.getItemStacks());
                    }
                    p.setString(9, items);
                    if (mailDate.getCommand() != null) {
                        command = Joiner.on(",").join(mailDate.getCommand());
                    }
                    p.setString(10, command);

                    p.setString(11, mailDate.getSenderTime());
                    p.setString(12, mailDate.getGetTime());
                    if (ps.isOnline()) {
                        MailManage.addTargetCache(ps.getUniqueId(), MailManage.buildMailClass(
                                mailID, mailDate.getName(), mailDate.getTitle(), mailDate.getText(), sender, target, mailDate.getState(),
                                mailDate.getAdditional(), mailDate.getSenderTime(), mailDate.getGetTime(), items, command
                        ));
                        MailManage.INSTANCE.sendMailMessage( mailDate.getTitle(),  mailDate.getText(), null, ps.getPlayer());
                    }
                    p.addBatch();
                }
                p.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
