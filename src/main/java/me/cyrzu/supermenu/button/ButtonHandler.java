package me.cyrzu.supermenu.button;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ButtonHandler {

    protected @Nullable ItemStack stack;

    public ButtonHandler(@Nullable ItemStack stack) {
        this.stack = stack;
    }

    public @NotNull ItemStack getStack() {
        return stack == null ? new ItemStack(Material.AIR) : stack.clone();
    }

    public abstract void runClick(@NotNull Player player, @NotNull InventoryClickEvent event);

}
