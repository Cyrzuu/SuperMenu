package me.cyrzu.git.supermenu;

import me.cyrzu.git.supermenu.inventory.AbstractMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;

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

    public static void registerMenu(@NotNull AbstractMenu menuHandler) {
        if(manager == null) {
            throw new RuntimeException("MenuManager is not registered");
        }

        manager.register(menuHandler);
    }

    private final JavaPlugin instance;

    @NotNull
    private final Map<Inventory, AbstractMenu> inventories;

    private MenuManager(JavaPlugin instance) {
        this.instance = instance;
        this.inventories = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(new MenuListeners(this), instance);
    }

    private void register(@NotNull AbstractMenu handler) {
        inventories.put(handler.getInventory(), handler);
    }

    public boolean unregisterIf(@NotNull Predicate<AbstractMenu> filter) {
        boolean removed = false;
        for (Map.Entry<Inventory, AbstractMenu> entry : inventories.entrySet()) {
            if(filter.test(entry.getValue())) {
                inventories.remove(entry.getKey());
                removed = true;
            }
        }

        return removed;
    }

    public void unregisterAll() {
        Iterator<AbstractMenu> iterator = inventories.values().iterator();
        while (iterator.hasNext()) {
            unregister(iterator.next());
        }
    }

    public void unregister(@NotNull AbstractMenu handler) {
        handler.removeButtons();
        handler.fillAll(new ItemStack(Material.AIR));
        inventories.remove(handler.getInventory());

        handler.cancelTask();
        new ArrayList<>(handler.getInventory().getViewers()).forEach(HumanEntity::closeInventory);
    }

    public @Nullable AbstractMenu getMenuHandler(Inventory inventory) {
        return inventories.get(inventory);
    }

    public JavaPlugin getInstance() {
        return instance;
    }

}
