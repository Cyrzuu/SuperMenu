package me.cyrzu.git.supermenu.button;

import me.cyrzu.git.supermenu.ItemButtonState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class EmptyButton extends ButtonHandler {

    @NotNull
    private final static ItemStack AIR = new ItemStack(Material.AIR);

    private final @NotNull Consumer<@NotNull ItemButtonState> action;

    public EmptyButton(@NotNull Runnable function) {
        this(state -> function.run());
    }

    public EmptyButton(@NotNull Consumer<@NotNull ItemButtonState> function) {
        super(new ItemStack(Material.AIR));
        this.action = function;
    }

    @Override
    public void runClick(@NotNull Player player, @NotNull InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        action.accept(new ItemButtonState(event.getInventory(), player, event.getRawSlot(), event.getClick(), item == null ? AIR.clone() : item));
    }

}
