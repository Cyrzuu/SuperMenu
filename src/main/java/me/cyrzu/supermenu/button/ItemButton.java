package me.cyrzu.supermenu.button;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import supermenu.ItemButtonState;

import java.util.function.BiConsumer;

public class ItemButton extends ButtonHandler {

    private final @NotNull BiConsumer<@NotNull Player, @NotNull ItemButtonState> action;

    public ItemButton(@NotNull ItemStack stack, @NotNull BiConsumer<@NotNull Player, @NotNull ItemButtonState> action) {
        super(stack.getType().isAir() ? new ItemStack(Material.STONE) : stack);
        this.action = action;
    }

    @Override
    public void runClick(@NotNull Player player, @NotNull InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if(item == null || item.getType().isAir()) {
            return;
        }

        action.accept(player, new ItemButtonState(event.getInventory(), event.getRawSlot(), event.getClick(), item));
    }

}
