package me.geek.mail.common.Menu.Sub;

import java.util.List;

/**
 * 作者: 老廖
 * 时间: 2022/7/5
 **/
public final class Micon {
    private final String Icon;
    private final IconType Type;
    private final String Mats;
    private final int Data;
    private final String Name;
    private final List<String> Lore;

    public Micon(String icon, IconType type, String mats, int data, String name, List<String> lore) {
        this.Icon = icon;
        this.Type = type;
        this.Mats = mats;
        this.Data = data;
        this.Name = name;
        this.Lore = lore;
    }

    public String getIcon() {
        return Icon;
    }
    public IconType getType() {
        return this.Type;
    }

    public String getMats() {
        return Mats;
    }

    public int getData() {
        return Data;
    }

    public String getName() {
        return Name;
    }

    public List<String> getLore() {
        return Lore;
    }

}
