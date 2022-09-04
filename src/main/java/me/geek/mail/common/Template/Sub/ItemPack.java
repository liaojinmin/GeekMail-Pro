package me.geek.mail.common.Template.Sub;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 作者: 老廖
 * 时间: 2022/8/19
 **/
public final class ItemPack implements Temp {
    private final String PackID;
    private final String Condition;
    private final String Action;
    private final String Deny;

    private final String Title;
    private final String Text;
   // private final MailType type;
    private final ItemStack[] appendix;

    public ItemPack(@NotNull String packId, @NotNull String condition, @NotNull String action, @NotNull String deny, @NotNull String title, @NotNull String text, @NotNull ItemStack... app) {
        this.PackID = packId;

        this.Condition = condition;
        this.Action = action;
        this.Deny = deny;

        this.Title = title;
        this.Text = text;
    //    this.type = type;
        this.appendix = app;
    }


    @Override
    public String getPackID() {
        return this.PackID;
    }

    @Override
    public String getCondition() {
        return this.Condition;
    }

    @Override
    public String getAction() {
        return this.Action;
    }

    @Override
    public String getDeny() {
        return this.Deny;
    }

    @Override
    public String getTitle() {
        return this.Title;
    }

    @Override
    public String getText() {
        return this.Text;
    }

   // @Override
   // public MailType getType() {
      //  return this.type;
   // }

    @Override
    public String getAppendix() {
        return null;
    }

    @Override
    public ItemStack[] getItemAppendix() {
        return this.appendix;
    }
}
