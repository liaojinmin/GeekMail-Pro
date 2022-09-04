package me.geek.mail.Configuration;



import me.geek.mail.GeekMail;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;


import java.io.File;
import java.util.UUID;

public final class ConfigManager {
    public static final Plugin plugin = GeekMail.INSTANCE.getInstance();
    private static final File yml = new File(plugin.getDataFolder(), "config.yml");


    public static File getYml() {
        return yml;
    }
    public static void Load() {
        plugin.saveDefaultConfig();
        FileConfiguration config = YamlConfiguration.loadConfiguration(yml);

        DeBug = config.getBoolean("debug", false);
        //sql
        DATA_TYPE = config.getString("data_storage.use_type", "sqlite");
        MYSQL_HOST = config.getString("data_storage.mysql.host", "127.0.0.1");
        MYSQL_PORT = config.getInt("data_storage.mysql.port", 3306);
        MYSQL_DATABASE = config.getString("data_storage.mysql.database", "server_Mail");
        MYSQL_USERNAME = config.getString("data_storage.mysql.username", "root");
        MYSQL_PASSWORD = config.getString("data_storage.mysql.password", "123456");
        MYSQL_PARAMS = config.getString("data_storage.mysql.params", "?autoReconnect=true&useSSL=false");
        MYSQL_DATA_NAME = config.getString("data_storage.mysql.DataName", "player_Mail");
        // hikari
        MAXIMUM_POOL_SIZE = config.getInt("data_storage.hikari_settings.maximum_pool_size", 10);
        MINIMUM_IDLE = config.getInt("data_storage.hikari_settings.minimum_idle", 5);
        MAXIMUM_LIFETIME = config.getInt("data_storage.hikari_settings.maximum_lifetime", 1800000);
        KEEPALIVE_TIME = config.getInt("data_storage.hikari_settings.keepalive_time", 0);
        CONNECTION_TIMEOUT = config.getInt("data_storage.hikari_settings.connection_timeout", 5000);



        // 邮件种类转换语言
        MONEY_MAIL = config.getString("MailType.MONEY_MAIL.tag","&e金币").replace("&", "§");
        POINTS_MAIL = config.getString("MailType.POINTS_MAIL.tag", "&b点券").replace("&", "§");
        EXP_MAIL = config.getString("MailType.EXP_MAIL.tag", "&a经验").replace("&", "§");
        TEXT_MAIL = config.getString("MailType.TEXT_MAIL.tag", "&f文本").replace("&", "§");
        CMD_MAIL = config.getString("MailType.CMD_MAIL.tag", "&c系统").replace("&", "§");
        ITEM_MAIL = config.getString("MailType.ITEM_MAIL.tag", "&6物品").replace("&", "§");
        location = config.getString("Block", " ");

    }


    public static Boolean DeBug;
    // SQL set
    public static String DATA_TYPE;
    // MYSQL
    public static String MYSQL_HOST;
    public static int MYSQL_PORT;
    public static String MYSQL_DATABASE;
    public static String MYSQL_USERNAME;
    public static String MYSQL_PASSWORD;
    public static String MYSQL_PARAMS;
    public static String MYSQL_DATA_NAME;
    // hikari
    public static int MAXIMUM_POOL_SIZE;
    public static int MINIMUM_IDLE;
    public static int MAXIMUM_LIFETIME;
    public static int KEEPALIVE_TIME;
    public static int CONNECTION_TIMEOUT;



    // 邮件种类转换语言
    public static String MONEY_MAIL;
    public static String POINTS_MAIL;
    public static String EXP_MAIL;
    public static String TEXT_MAIL;
    public static String ITEM_MAIL;
    public static String CMD_MAIL;


    // BLOCK
    public static String location;


    public static UUID Console = UUID.fromString("00000000-0000-0000-0000-000000000001");
}
