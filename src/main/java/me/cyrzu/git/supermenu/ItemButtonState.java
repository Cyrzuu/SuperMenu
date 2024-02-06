package me.cyrzu.git.supermenu;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemButtonState {

    @NotNull
    private final Inventory inventory;

    @Getter
    @NotNull
    private final Player WhoClicked;

    private final int slot;

    @Getter
    private final ClickType type;

    @Getter
    @NotNull
    private ItemStack stack;

    public ItemButtonState(@NotNull Inventory inventory, @NotNull Player WhoClicked, int slot, ClickType type, @NotNull ItemStack stack) {
        this.inventory = inventory;
        this.WhoClicked = WhoClicked;
        this.type = type;
        this.stack = stack;
        this.slot = slot;
    }

    public void setStack(@Nullable ItemStack stack) {
        if(stack == null) return;
        inventory.setItem(slot, stack);
        this.stack = stack;
    }

}
