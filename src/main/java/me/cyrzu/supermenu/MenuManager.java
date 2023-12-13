package me.cyrzu.supermenu;

import me.cyrzu.supermenu.inventory.MenuHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MenuManager {

    private @Nullable static MenuManager manager = null;

    public static void registerManager(JavaPlugin instance) {
        if(manager != null) {
            throw new RuntimeException("Manager is registered");
        }

        manager = new MenuManager(instance);
    }

    public static @NotNull MenuManager getManager() {
        if(manager == null) {
            throw new RuntimeException("Manager is not registered");
        }

        return manager;
    }

    public static void registerMenu(@NotNull MenuHandler menuHandler) {
        if(manager == null) {
            throw new RuntimeException("MenuManager is not registered");
        }

        manager.register(menuHandler);
    }

    private final JavaPlugin instance;

    private final Map<Inventory, MenuHandler> inventories;

    private MenuManager(JavaPlugin instance) {
        this.instance = instance;
        this.inventories = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(new MenuListeners(this), instance);
    }

    private void register(@NotNull MenuHandler handler) {
        inventories.put(handler.getInventory(), handler);
    }

    public void unregister(@NotNull MenuHandler handler) {
        handler.cancelTask();
        new ArrayList<>(handler.getInventory().getViewers()).forEach(HumanEntity::closeInventory);
        inventories.remove(handler.getInventory());
    }

    public @Nullable MenuHandler getMenuHandler(Inventory inventory) {
        return inventories.get(inventory);
    }

    public JavaPlugin getInstance() {
        return instance;
    }

}
