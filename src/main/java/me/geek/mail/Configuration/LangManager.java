package me.geek.mail.Configuration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 作者: 老廖
 * 时间: 2022/8/4
 **/
public final class LangManager {
    private final static File dir = new File(ConfigManager.plugin.getDataFolder(), "zh_CN.yml");

    public static void onload() {
        if (!dir.exists()) {
            ConfigManager.plugin.saveResource("zh_CN.yml", false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(dir);
        //lang
        SENDER_MSG = getStringList(config.getString("Msg.sender"));
        TARGET_MSG = getStringList(config.getString("Msg.target"));
        JoinMsg = getStringList(config.getString("Msg.join"));
        demMail = getStringList(config.getString("Msg.demMail"));

        // action
        RUN_GET_ITEM = getStringList(config.getString("action.run.getItem"));
        RUN_DELETE = getStringList(config.getString("action.run.delete"));

        DENY_GET_ITEM = getStringList(config.getString("action.deny.getItem"));
        DENY_DELETE = getStringList(config.getString("action.deny.delete"));
    }
    public static void onReload() {
        onload();
    }


    // lang
    public static List<String> SENDER_MSG;
    public static List<String> TARGET_MSG;
    public static List<String> JoinMsg;
    public static List<String> demMail;

    // GUIAction action
    public static List<String> RUN_GET_ITEM;
    public static List<String> RUN_DELETE;

    // GUIAction deny
    public static List<String> DENY_GET_ITEM;
    public static List<String> DENY_DELETE;



    public static List<String> getStringList(String s) {
        if (s != null) {
            return Arrays.asList(s.replace("&", "§").split("\n"));
        }
        return new ArrayList<>();
    }
}
