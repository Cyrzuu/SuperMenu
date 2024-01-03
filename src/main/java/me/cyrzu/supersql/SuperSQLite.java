package me.cyrzu.supersql;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SuperSQLite extends SuperSQL {

    public SuperSQLite(@NotNull File file) {
        super(getConnection(file));
    }

    @NotNull
    private static Connection getConnection(@NotNull File file) {
        try {
            if(!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
                throw new RuntimeException("Error creating parent file");
            }

            if(!file.exists() && !file.createNewFile()) {
                throw new RuntimeException("Error creating file");
            }

            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:" + file);
        } catch (IOException | SQLException | ClassNotFoundException e) {
            throw new IllegalArgumentException("failed to connect to the SQLite database\n"+e.getMessage());
        }
    }
}
