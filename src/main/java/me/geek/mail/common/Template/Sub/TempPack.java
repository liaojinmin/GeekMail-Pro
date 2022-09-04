package me.geek.mail.common.Template.Sub;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 作者: 老廖
 * 时间: 2022/8/8
 **/
public final class TempPack implements Temp{

    private final String PackID;
    private final String Condition;
    private final String Action;
    private final String Deny;

    private final String Title;
    private final String Text;
    private final String appendix;


    public TempPack(@NotNull String packId, @NotNull String condition, @NotNull String action, @NotNull String deny, @NotNull String title, @NotNull String text, @NotNull String app) {
        this.PackID = packId;

        this.Condition = condition;
        this.Action = action;
        this.Deny = deny;

        this.Title = title;
        this.Text = text;

        this.appendix = app;
    }

    @Override
    public String getPackID() {
        return PackID;
    }

    @Override
    public String getCondition() {
        return Condition;
    }

    @Override
    public String getAction() {
        return Action;
    }
    @Override
    public String getDeny() {
        return Deny;
    }


    @Override
    public String getTitle() {
        return Title;
    }

    @Override
    public String getText() {
        return Text;
    }



    @Override
    public String getAppendix() {
        return appendix;
    }

    @Override
    public ItemStack[] getItemAppendix() {
        return new ItemStack[0];
    }
}
