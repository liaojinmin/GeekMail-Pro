package me.geek.mail.common.Menu;

import com.google.common.base.Joiner;
import me.geek.mail.Configuration.ConfigManager;

import me.geek.mail.common.Menu.Sub.IconType;
import me.geek.mail.common.Menu.Sub.Msession;
import me.geek.mail.common.Menu.Sub.Micon;
import me.geek.mail.api.utils.HexUtils;
import me.geek.mail.GeekMail;

import me.geek.mail.api.hook.hookPlugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;



/**
 * 作者: 老廖
 * 时间: 2022/7/23
 **/
public final class Menu {

    private final ItemStack itemStack = new ItemStack(Material.AIR);

    // 缓存的菜单页面 键 = 菜单唤起指令
    private final Map<String, Msession> MenuCache = new HashMap<>();

    // 缓存的菜单打开指令 key = 菜单绑定的命令  value = 菜单名称
    private final Map<String, String> MenuCmd = new HashMap<>();
    public String cmd;
    public final List<Player> isOpen = new ArrayList<>();

    public Menu() {
        saveDefaultMenu();
        onLoadMenu();
    }

    /**
     * 为玩家构建指定页数的的界面
     * @param player 目标玩家
     * @param MenuTag 菜单标签
     * @return 返回界面
     */
    public Inventory Build(Player player, String MenuTag) {
        Msession tag = MenuCache.get(MenuTag);
        ItemStack[] item = tag.getItemStacks();
        Inventory inventory = Bukkit.createInventory(player, tag.getSize(), tag.getTitle());
        if (item.length >= 1) {
            inventory.setContents(item);
        }
        return inventory;
    }
    public Msession getMenuTag(String MenuID) {
        return this.MenuCache.get(MenuID);
    }

    public String getMenuCommand(String MenuID) {
        return this.MenuCmd.get(MenuID);
    }

    public void CloseGui() {
        Bukkit.getOnlinePlayers().forEach( (player) -> {
            if (this.isOpen.contains(player)) {
                player.closeInventory();
            }
        });
    }
    public void onReload() {
        this.MenuCache.clear();
        this.MenuCmd.clear();
        this.isOpen.clear();
        onLoadMenu();
    }



    private void onLoadMenu() {
        long start = System.currentTimeMillis();
        List<Micon> miconObjs = new ArrayList<>();

        List<File> list = new ArrayList<>();

        ForFile(new File(ConfigManager.plugin.getDataFolder(),"menu"), list);

        for (File f : list) {
            String var100 = f.getName();
            String MenuTag = var100.substring(0, var100.indexOf("."));

            FileConfiguration var1 = YamlConfiguration.loadConfiguration(new File(ConfigManager.plugin.getDataFolder()+File.separator+"menu", MenuTag+".yml"));

            Object[] objects = var1.getConfigurationSection("Icons").getKeys(false).toArray();
            // 初始化菜单标签对象
            Msession tag = new Msession();
            tag.setMenuTag(MenuTag);
            tag.setTitle(var1.getString("TITLE").replace("&","§"));
            tag.setLayout(var1.getStringList("Layout").toString()
                    .replace("[","")
                    .replace("]","")
                    .replace(", ",""));
            tag.setSize(var1.getStringList("Layout").size() * 9);
            tag.setBindings(var1.getString("Bindings.Commands"));
            tag.setType(var1.getString("TYPE"));
            cmd = var1.getString("Bindings.Commands");

            miconObjs.clear();
            // 载入图标
            for (Object o : objects) {
              //  GeekMail.say("载入图标 "+o.toString());
                Micon miconObj = new Micon(
                        o.toString(),
                        getIconType(var1.getString("Icons." + o + ".Type", "null")),
                        var1.getString("Icons." + o + ".display." + "mats", "PAPER"),
                        var1.getInt("Icons." + o + ".display." + "data", 0),
                        var1.getString("Icons." + o + ".display." + "name", "未知").replace("&", "§"),
                        Arrays.asList(Joiner.on(",").join(var1.getStringList("Icons." + o + ".display." + "lore"))
                                .replace("&", "§")
                                .split(","))
                );
                miconObjs.add(miconObj);
            }
            tag.setMiconObj(miconObjs);
            tag.setItemStacks(onBuildStack(miconObjs, tag.getLayout(), tag.getSize()));
            MenuCache.put(MenuTag, tag);
            MenuCmd.put(tag.getBindings() ,MenuTag);
        }
        GeekMail.say("§7菜单界面加载完成 §8(耗时" + (System.currentTimeMillis() - start) + " ms)");

    }


    private IconType getIconType(String type) {
        if (type != null) {
            String var1 = type.toUpperCase(Locale.ROOT);
            switch (var1) {
                case "TEXT":
                    return IconType.TEXT;
                case "DELETE":
                    return IconType.DELETE;
                case "SEND":
                    return IconType.BACK;
                case "LAST_PAGE":
                    return IconType.LAST_PAGE;
                case "NEXT_PAGE":
                    return IconType.NEXT_PAGE;
            }
        }
        return IconType.NORMAL;
    }
    private ItemStack[] onBuildStack(List<Micon> var1, String Layout, int size) {
        ItemStack[] i = new ItemStack[0];
        List<ItemStack> item = new ArrayList<>(Arrays.asList(i));
        try {
            int index = 0;
            while (index < size) {
                if (Layout.charAt(index) != ' ') {
                    String IconID = String.valueOf(Layout.charAt(index));
                    item.add(index, onBuildItem(IconID, var1));
                } else {
                    item.add(index, itemStack);
                }
                index++;
            }
        } catch (StringIndexOutOfBoundsException ignored){}
        return item.toArray(i);
    }

    private ItemStack onBuildItem(String iconID, List<Micon> miconObj) {
            for (Micon icon : miconObj) {
                if (Objects.equals(icon.getIcon(), iconID)) {
                    if (icon.getType().equals(IconType.TEXT)) {
                        return itemStack;
                    }
                    ItemStack itemStack;
                    try {
                        if (icon.getMats().contains("IA:")) {
                            String[] meta = icon.getMats().split(":");
                            itemStack = hookPlugin.getItemsAdder(meta[1]);
                        } else {
                            itemStack = new ItemStack(Material.valueOf(icon.getMats()), 1, (short) icon.getData());
                        }
                    } catch (IllegalArgumentException ing) {
                      itemStack = new ItemStack(Material.STONE, 1);
                    }
                    ItemMeta itemMeta = itemStack.getItemMeta();
                    if (itemMeta != null) {
                        itemMeta.setDisplayName(HexUtils.colorify(icon.getName()));
                      //  GeekMail.say(HexUtils.kt.parseHex(icon.getName()));
                        List<String> lores = Arrays.asList(HexUtils.colorify(Joiner.on(",").join(icon.getLore()))
                                .split(","));
                        if (lores.size() == 1 && lores.get(0).isEmpty()) {
                            lores = null;
                        }
                        itemMeta.setLore(lores);
                        itemStack.setItemMeta(itemMeta);
                    }
                    return itemStack;
                }
            }
        return itemStack;
    }

    private void ForFile(File f, List<File> list) {
        if (f.isDirectory()) {
            File[] amt = f.listFiles();
            for (File tmp : amt) {
                ForFile(tmp, list);
            }
        } else {
            if (f.getAbsolutePath().endsWith(".yml")) {
                list.add(f);
            }
        }
    }
    private void saveDefaultMenu() {
        File dir = new File(ConfigManager.plugin.getDataFolder(), "menu");
        if (!dir.exists()) dir.mkdirs();
        File menu = new File(dir, "def.yml");
        if (!menu.exists()) {
            ConfigManager.plugin.saveResource("menu"+File.separator+"def.yml", false);
        }
    }
}
