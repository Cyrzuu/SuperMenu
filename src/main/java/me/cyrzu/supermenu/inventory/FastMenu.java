package me.cyrzu.supermenu.inventory;

import me.cyrzu.supermenu.button.ButtonHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class FastMenu extends MenuHandler {

    public FastMenu(int rows, String title) {
        super(rows, title);
    }

    public FastMenu(int rows) {
        super(rows);
    }

    @Override
    public FastMenu setButton(int slot, ButtonHandler button) {
        super.setButton(slot, button);
        return this;
    }

    public FastMenu onClose(@NotNull BiConsumer<@NotNull Player, @NotNull Inventory> action) {
        super.onClose(action);
        return this;
    }

    public FastMenu setItem(int slot, @NotNull ItemStack stack) {
        super.setItem(slot, stack);
        return this;
    }

    public FastMenu setMoveableSlot(int slot) {
        return setMoveableSlot(slot, null, null);
    }

    public FastMenu setMoveableSlot(int slot, @Nullable BiFunction<Player, ItemStack, Boolean> put) {
        return setMoveableSlot(slot, put, null);
    }

    public FastMenu setMoveableSlot(int slot, @Nullable BiFunction<Player, ItemStack, Boolean> put,
                                       @Nullable BiFunction<Player, ItemStack, Boolean> take) {
        super.setMoveableSlot(slot, put, take);
        return this;
    }

    @Override
    public FastMenu setTask(long period, @NotNull Runnable runnable) {
        super.setTask(period, runnable);
        return this;
    }

    @Override
    public FastMenu setTask(@NotNull Runnable runnable) {
        super.setTask(runnable);
        return this;
    }


    public FastMenu setPermission(@NotNull String permission) {
        super.setPermission(new Permission(permission));
        return this;
    }

    @Override
    public FastMenu setUnregisterOnClose(boolean unregister) {
        super.setUnregisterOnClose(unregister);
        return this;
    }

    @Override
    public FastMenu fillAll(@NotNull ItemStack stack) {
        return fillAll(stack, new Integer[0]);
    }

    @Override
    public FastMenu fillAll(@NotNull ItemStack stack, @NotNull Integer... ignore) {
        super.fillAll(stack, ignore);
        return this;
    }

    public void fillBorder(@NotNull ItemStack stack) {
        int rows = inventory.getSize() / 9;
        if (rows <= 2) {
            return;
        }

        for (int i = 0; i < rows * 9; i++) {
            if (i <= 8 || (i >= rows * 9 - 8 && i <= rows * 9 - 2) || i % 9 == 0 || i % 9 == 8)
                inventory.setItem(i, stack);
        }
    }

}
