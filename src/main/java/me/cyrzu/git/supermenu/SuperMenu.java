package me.cyrzu.git.supermenu;

import me.cyrzu.git.supermenu.inventory.AbstractMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class SuperMenu implements Listener {

    private @Nullable static SuperMenu manager = null;

    public static void registerManager(JavaPlugin instance) {
        if(manager != null) {
            throw new RuntimeException("Manager is registered");
        }

        manager = new SuperMenu(instance);
    }

    public static @NotNull SuperMenu getManager() {
        if(manager == null) {
            throw new RuntimeException("Manager is not registered");
        }

        return manager;
    }

    public static void registerMenu(@NotNull AbstractMenu menuHandler) {
        if(manager == null) {
            throw new RuntimeException("SuperMenu is not registered");
        }

        manager.register(menuHandler);
    }

    private final JavaPlugin instance;

    @NotNull
    private final Map<Inventory, AbstractMenu> inventories;

    private SuperMenu(JavaPlugin instance) {
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
        this.unregister(handler, true);
    }

    public void unregister(@NotNull AbstractMenu handler, boolean force) {
        ItemStack[] contents = handler.getInventory().getContents();
        handler.setLastContents(Arrays.copyOf(contents, contents.length));

        handler.removeButtons();
        handler.fillAll(new ItemStack(Material.AIR));
        inventories.remove(handler.getInventory());

        handler.cancelTask();
        List<HumanEntity> humanEntities = List.copyOf(handler.getInventory().getViewers());
        if(force) {
            humanEntities.forEach(HumanEntity::closeInventory);
            return;
        }

        Inventory inventory = handler.getInventory();
        Bukkit.getScheduler().runTask(instance, () -> humanEntities.forEach(human -> {
            if(!human.getOpenInventory().getTopInventory().equals(inventory)) {
                return;
            }

            human.closeInventory();
        }));
    }

    public @Nullable AbstractMenu getMenuHandler(Inventory inventory) {
        return inventories.get(inventory);
    }



    public JavaPlugin getInstance() {
        return instance;
    }





    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        InventoryView view = event.getView();
        view.setTitle("");
    }

}
