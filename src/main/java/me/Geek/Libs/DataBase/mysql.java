package me.Geek.Libs.DataBase;

import com.zaxxer.hikari.HikariDataSource;
import me.Geek.Configuration.ConfigManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 作者: 老廖
 * 时间: 2022/7/23
 **/
public class mysql extends DataSub{
    private HikariDataSource MYSQL;

    public mysql() {
        super();
    }
    @Override
    public Connection getConnection() throws SQLException {
        return MYSQL.getConnection();
    }

    @Override
    public void onLoad() {
        final String MysqlUrl = "jdbc:mysql://" + ConfigManager.MYSQL_HOST + ":" + ConfigManager.MYSQL_PORT + "/" + ConfigManager.MYSQL_DATABASE + ConfigManager.MYSQL_PARAMS;
        MYSQL = new HikariDataSource();
        MYSQL.setJdbcUrl(MysqlUrl);
        MYSQL.setUsername(ConfigManager.MYSQL_USERNAME);
        MYSQL.setPassword(ConfigManager.MYSQL_PASSWORD);
        // 设置驱动
        try {
            MYSQL.setDriverClassName("com.mysql.cj.jdbc.Driver");
        } catch (RuntimeException | NoClassDefFoundError e) {
            MYSQL.setDriverClassName("com.mysql.jdbc.Driver");
        }
        MYSQL.setMaximumPoolSize(ConfigManager.MAXIMUM_POOL_SIZE);
        MYSQL.setMinimumIdle(ConfigManager.MINIMUM_IDLE);
        MYSQL.setMaxLifetime(ConfigManager.MAXIMUM_LIFETIME);
        MYSQL.setKeepaliveTime(ConfigManager.KEEPALIVE_TIME);
        MYSQL.setConnectionTimeout(ConfigManager.CONNECTION_TIMEOUT);
        MYSQL.setPoolName("GeekMail-MYSQL");
        createMysqlTables();
    }

    @Override
    public void onStop() {
        if (MYSQL != null) MYSQL.close();
    }

    private void createMysqlTables() {
        try (Connection connection = getConnection()) {
            try (Statement statement = connection.createStatement()){
                statement.execute("CREATE TABLE IF NOT EXISTS `mail_data` (" +
                        " `id` INT(80) NOT NULL AUTO_INCREMENT, " +
                        " `mail_id` CHAR(36) NOT NULL, " +
                        " `state` VARCHAR(256) NOT NULL, " +
                        " `type` text NOT NULL, " +
                        " `sender` CHAR(36) NOT NULL, " +
                        " `target` CHAR(36) NOT NULL, " +
                        " `title` text NOT NULL, " +
                        " `text` longtext NOT NULL, " +
                        " `money` double NOT NULL DEFAULT '0', " +
                        " `points` integer NOT NULL DEFAULT '0', " +
                        " `exp` integer NOT NULL DEFAULT '0', " +
                        " `item` longtext NOT NULL, " +
                        " `commands` longtext NOT NULL, " +
                        " PRIMARY KEY (`id`))ENGINE=InnoDB DEFAULT CHARSET=utf8;");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
