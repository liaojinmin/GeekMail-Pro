package me.Geek.Libs.Template;

import me.Geek.GeekMail;
import me.Geek.Libs.Template.Sub.TempPack;
import me.Geek.api.mail.MailType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;

/**
 * 作者: 老廖
 * 时间: 2022/8/7
 **/
public final class Template {

    private static final Map<String, TempPack> TEMP_PACK_MAP = new HashMap<>();
    private static final Plugin plugin = GeekMail.INSTANCE.getInstance();

    public static void onLoad() {
        long start = System.currentTimeMillis();
        TEMP_PACK_MAP.clear();
        File dir = new File(plugin.getDataFolder(), "template");
        if (!dir.exists()) dir.mkdirs();
        File template = new File(dir, "def.yml");
        if (!template.exists()) {
            plugin.saveResource("template"+File.separator+"def.yml", false);
        }

        List<File> list = new ArrayList<>();
        ForFile(dir, list);
        for (File f : list) {
            FileConfiguration var1 = YamlConfiguration.loadConfiguration(f);
            String PackID = var1.getString("Template.ID").replace("&", "§");

            String condition = var1.getString("Template.Require.condition", "false");
            String action = var1.getString("Template.Require.action", "null").replace("&", "§");
            String deny =   var1.getString("Template.Require.deny","null").replace("&", "§");

            String title = var1.getString("Template.package.title").replace("&", "§");
            String text = var1.getString("Template.package.text").replace("&", "§").replace("\n", "");
            MailType type = MailType.valueOf(var1.getString("Template.package.type"));
            String appendix = var1.getString("Template.package.appendix", "0");

            TEMP_PACK_MAP.put(PackID, new TempPack(PackID, condition, action, deny, title, text, type, appendix));

        }
        GeekMail.say("§7邮件模板加载完成 §8(耗时" + (System.currentTimeMillis() - start) + " ms)");
    }


    public static Map<String, TempPack> getTempPackMap() {
        return TEMP_PACK_MAP;
    }

    public static TempPack getTempPack(String key) {
        return TEMP_PACK_MAP.get(key);
    }

    private static void ForFile(File f, List<File> list) {
        if (f.isDirectory()) {
            File[] amt = f.listFiles();
            if (amt != null) {
                for (File tmp : amt) {
                    ForFile(tmp, list);
                }
            }
        } else {
            if (f.getAbsolutePath().endsWith(".yml")) {
                list.add(f);
            }
        }
    }
}
