package me.cyrzu.supersql;

import me.cyrzu.supersql.column.IntegerColumn;
import me.cyrzu.supersql.column.StringColumn;
import me.cyrzu.supersql.column.VarcharColumn;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SuperDBManager {

    private @Nullable static SuperDBManager manager = null;

    public static void registerManager(JavaPlugin instance) {
        if(manager != null) {
            throw new RuntimeException("Manager is registered");
        }

        manager = new SuperDBManager(instance);

        SQLTable sqlTable = SQLTable.builder("xD")
                .add(new VarcharColumn("uuid", 36).primaryKey())
                .add(new StringColumn("username").notNull())
                .add(new IntegerColumn("kills"))
                .build();


    }

    public static @NotNull SuperDBManager getManager() {
        if(manager == null) {
            throw new RuntimeException("Manager is not registered");
        }

        return manager;
    }

    private final JavaPlugin instance;

    private SuperDBManager(JavaPlugin instance) {
        this.instance = instance;
    }

    public JavaPlugin getInstance() {
        return instance;
    }

}
