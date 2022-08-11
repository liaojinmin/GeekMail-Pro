package me.Geek.Libs.Menu.Sub;

import me.Geek.Libs.Menu.Sub.Micon;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者: 老廖
 * 时间: 2022/7/5
 **/
public final class MTag {
    /**
     * 菜单文件名称
     */
    private String MenuTag;
    private String Title;
    private String Layout;
    private int Size;
    private String Bindings;
    private List<Micon> miconObj;
    private ItemStack[] itemStacks = new ItemStack[0];

    private String Type;


    public MTag(String menuTag, String title, String layout, int size, String bindings, List<Micon> miconObj, String type) {
        this.Title = title;
        this.MenuTag = menuTag;
        this.Layout = layout;
        this.Size = size;
        this.Bindings = bindings;
        this.miconObj = miconObj;
        this.Type = type;
    }

    public MTag() {
        MenuTag = " ";
        Title = "未知标题";
        Layout = " ";
        Size = 8;
        Bindings = " ";
        miconObj = new ArrayList<>();
        Type = " ";
    }

    public String getMenuTag() {
        return MenuTag;
    }
    public  ItemStack[] getItemStacks() {
        return this.itemStacks;
    }

    public String getTitle() {
        return Title;
    }

    public String getLayout() {
        return Layout;
    }

    public int getSize() {
        return Size;
    }

    public String getBindings() {
        return Bindings;
    }

    public List<Micon> getMiconObj() {
        return this.miconObj;
    }
    public String getType() {
        return this.Type;
    }




    public void setMenuTag(String menuTag) {
        MenuTag = menuTag;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setLayout(String layout) {
        Layout = layout;
    }

    public void setSize(int size) {
        Size = size;
    }

    public void setBindings(String bindings) {
        Bindings = bindings;
    }

    public void setMiconObj(List<Micon> miconObj) {
        this.miconObj = miconObj;
    }
    public void setItemStacks(ItemStack[] itemStacks) {
        this.itemStacks = itemStacks;
    }

    public void setType(String type) {
        this.Type = type;
    }
}
