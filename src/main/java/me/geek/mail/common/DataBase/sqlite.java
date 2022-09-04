package me.geek.mail.common.DataBase;

import com.zaxxer.hikari.HikariDataSource;
import me.geek.mail.Configuration.ConfigManager;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 作者: 老廖
 * 时间: 2022/7/23
 **/
public class sqlite extends DataSub{

    private HikariDataSource SQLITE;
    public sqlite() {
        super();
    }

    @Override
    public void onLoad() {
        final String SqliteUrl = "jdbc:sqlite:" + ConfigManager.plugin.getDataFolder() + File.separator +"GeekData.db";
        SQLITE = new HikariDataSource();
        SQLITE.setDataSourceClassName("org.sqlite.SQLiteDataSource");
        SQLITE.addDataSourceProperty("url", SqliteUrl);
        //附件参数
        SQLITE.setMaximumPoolSize(ConfigManager.MAXIMUM_POOL_SIZE);
        SQLITE.setMinimumIdle(ConfigManager.MINIMUM_IDLE);
        SQLITE.setMaxLifetime(ConfigManager.MAXIMUM_LIFETIME);
        SQLITE.setKeepaliveTime(ConfigManager.KEEPALIVE_TIME);
        SQLITE.setConnectionTimeout(ConfigManager.CONNECTION_TIMEOUT);
        SQLITE.setPoolName("GeekMail-SQLITE");
        createSqliteTables();
    }

    @Override
    public Connection getConnection() throws SQLException {
        return SQLITE.getConnection();
    }

    @Override
    public void onStop() {
        if (SQLITE != null) SQLITE.close();
    }

    private void createSqliteTables() {
        try (Connection connection = getConnection()) {
            try (Statement statement = connection.createStatement()){
                statement.execute("PRAGMA foreign_keys = ON;");
                statement.execute("PRAGMA encoding = 'UTF-8';");
                statement.execute("CREATE TABLE IF NOT EXISTS `maildata` (" +
                        " `id` integer PRIMARY KEY, " +
                        " `mail_id` CHAR(36) NOT NULL, " +
                        " `state` VARCHAR(256) NOT NULL, " +
                        " `type` text NOT NULL, " +
                        " `sender` CHAR(36) NOT NULL, " +
                        " `target` CHAR(36) NOT NULL, " +
                        " `title` text NOT NULL, " +
                        " `text` longtext NOT NULL, " +
                        " `additional` text NOT NULL, " +
                        " `item` longtext NOT NULL, " +
                        " `commands` longtext NOT NULL," +
                        " `sendertime` BIGINT(20) NOT NULL," +
                        " `gettime` BIGINT(20) NOT NULL);");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
