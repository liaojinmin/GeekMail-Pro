package me.geek.mail.common.DataBase;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 作者: 老廖
 * 时间: 2022/7/23
 **/
public abstract class DataSub {
    public abstract Connection getConnection() throws SQLException;
    public abstract void onLoad();
    public abstract void onStop();
}
