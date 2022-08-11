package me.Geek.Libs.Template.Sub;

import me.Geek.api.mail.MailType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
    private final MailType type;
    private final String appendix;


    public TempPack(@NotNull String packId, @NotNull String condition, @NotNull String action, @NotNull String deny, @NotNull String title, @NotNull String text, @NotNull MailType type, @NotNull String app) {
        this.PackID = packId;

        this.Condition = condition;
        this.Action = action;
        this.Deny = deny;

        this.Title = title;
        this.Text = text;
        this.type = type;
        this.appendix = app;
    }

    public String getPackID() {
        return PackID;
    }

    public String getCondition() {
        return Condition;
    }

    public String getAction() {
        return Action;
    }
    public String getDeny() {
        return Deny;
    }


    public String getTitle() {
        return Title;
    }

    public String getText() {
        return Text;
    }

    @NotNull
    public MailType getType() {
        return type;
    }

    public String getAppendix() {
        return appendix;
    }
}
