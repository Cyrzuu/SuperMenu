package me.cyrzu.git.supermenu.button;

import me.cyrzu.git.supermenu.ItemButtonState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemButton extends ButtonHandler {

    private final @NotNull Consumer<@NotNull ItemButtonState> action;

    public ItemButton(@NotNull ItemStack stack, @NotNull Runnable function) {
        this(stack, state -> function.run());
    }

    public ItemButton(@NotNull ItemStack stack, @NotNull Consumer<@NotNull ItemButtonState> function) {
        super(stack.getType().isAir() ? new ItemStack(Material.STONE) : stack);
        this.action = function;
    }

    @Override
    public void runClick(@NotNull Player player, @NotNull InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if(item == null || item.getType().isAir()) {
            return;
        }

        action.accept(new ItemButtonState(event.getInventory(), player, event.getRawSlot(), event.getClick(), item));
    }

}
