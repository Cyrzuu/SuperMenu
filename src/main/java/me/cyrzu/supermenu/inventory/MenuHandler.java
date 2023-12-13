package me.cyrzu.supermenu.inventory;

import lombok.Getter;
import me.cyrzu.supermenu.MenuManager;
import me.cyrzu.supermenu.MenuMoveableSlot;
import me.cyrzu.supermenu.MenuTask;
import me.cyrzu.supermenu.button.ButtonHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;


public abstract class MenuHandler {

    private final MenuManager menuManager;

    protected final @NotNull Inventory inventory;

    protected final @NotNull Map<Integer, ButtonHandler> buttons;

    private final @NotNull Map<Integer, MenuMoveableSlot> moveableSlots;

    protected @Nullable MenuTask menuTask = null;

    protected @Nullable Permission permission = null;

    private final @NotNull Map<UUID, @NotNull Long> cooldown;

    @Getter
    private boolean unregisterOnClose = true;

    public MenuHandler(int rows) {
        this(rows, "");
    }

    public MenuHandler(int rows, String title) {
        this.menuManager = MenuManager.getManager();
        this.inventory = Bukkit.createInventory(null, Math.min(6, rows) * 9, title);
        this.buttons = new HashMap<>();
        this.moveableSlots = new HashMap<>();
        this.cooldown = new HashMap<>();
    }

    public final void onClick(@NotNull Player player, @NotNull InventoryClickEvent event) {
        ButtonHandler button = buttons.get(event.getRawSlot());
        if(button != null) button.runClick(player, event);
    }

    public MenuHandler setButton(int slot, ButtonHandler buttonHandler) {
        int min = Math.min(inventory.getSize() - 1, slot);
        inventory.setItem(min, buttonHandler.getStack());
        buttons.put(min, buttonHandler);
        return this;
    }

    public MenuHandler setItem(int slot, @NotNull ItemStack stack) {
        inventory.setItem(Math.min(inventory.getSize() - 1, slot), stack);
        return this;
    }

    public MenuHandler setMoveableSlot(int slot) {
        return setMoveableSlot(slot, null, null);
    }

    public MenuHandler setMoveableSlot(int slot, @Nullable BiFunction<Player, ItemStack, Boolean> put) {
        return setMoveableSlot(slot, put, null);
    }

    public MenuHandler setMoveableSlot(int slot, @Nullable BiFunction<Player, ItemStack, Boolean> put,
                                       @Nullable BiFunction<Player, ItemStack, Boolean> take) {
        moveableSlots.put(slot, new MenuMoveableSlot(put, take));
        return this;
    }

    public MenuHandler setTask(long period, @NotNull Runnable runnable) {
        this.menuTask = new MenuTask(menuManager.getInstance(), period, runnable);
        return this;
    }

    public MenuHandler setTask(@NotNull Runnable runnable) {
        this.menuTask = new MenuTask(menuManager.getInstance(), runnable);
        return this;
    }
    public MenuHandler setPermission(@NotNull Permission permission) {
        this.permission = permission;
        return this;
    }

    public MenuHandler setUnregisterOnClose(boolean unregister) {
        this.unregisterOnClose = unregister;
        return this;
    }

    public final boolean hasPermission(@NotNull HumanEntity player) {
        return permission == null || player.hasPermission(permission);
    }

    public final boolean hasPermission(@NotNull Player player) {
        return permission == null || player.hasPermission(permission);
    }

    public final void cancelTask() {
        if (menuTask == null) {
            return;
        }

        menuTask.cancel();
    }

    public final @Nullable MenuMoveableSlot getMoveableSlot(int slot) {
        return moveableSlots.get(slot);
    }

    public final @Nullable ButtonHandler getButton(int slot) {
        return buttons.get(slot);
    }

    public final MenuHandler start() {
        MenuManager.registerMenu(this);

        if(menuTask != null) {
            menuTask.run();
        }

        return this;
    }

    public final void open(Player player) {
        player.openInventory(inventory);
    }

    public final @NotNull Inventory getInventory() {
        return inventory;
    }

    public boolean hasMoveableSlots() {
        return !moveableSlots.isEmpty();
    }

    public boolean hasCooldown(@NotNull Player player, boolean put) {
        UUID uuid = player.getUniqueId();
        long cooldown = this.cooldown.getOrDefault(uuid, -1L);
        long time = System.currentTimeMillis();

        if (cooldown != -1L && time < cooldown) {
            return true;
        } else {
            if(put) {
                this.cooldown.put(uuid, System.currentTimeMillis() + 150L);
            }

            return false;
        }
    }


}
