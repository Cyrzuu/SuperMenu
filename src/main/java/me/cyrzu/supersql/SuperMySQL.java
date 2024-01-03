package me.cyrzu.supersql;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SuperMySQL extends SuperSQL {

    public SuperMySQL(String host, String port, String database, String user, String password) {
        super(getConnection(host, port, database, user, password));
    }

    @NotNull
    private static Connection getConnection(String host, String port, String database, String user, String password) {
        String url = String.format("jdbc:mysql://%s:%s/%s?autoReconnect=true", host, port, database);
        try(Connection connection = DriverManager.getConnection(url, user, password)) {
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
