package me.cyrzu.git.supermenu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public record MenuMoveableSlot(@Nullable BiFunction<Player, ItemStack, Boolean> put, @Nullable BiFunction<Player, ItemStack, Boolean> take) {

    private boolean canPut(Player player, @NotNull ItemStack stack) {
        return put == null || stack.getType().isAir() || put.apply(player, stack);
    }

    private boolean canTake(Player player, @NotNull ItemStack stack) {
        return take == null || stack.getType().isAir() || take.apply(player, stack);
    }

    private boolean check(Player player, @Nullable ItemStack put, @Nullable ItemStack take) {
        if(put != null && !put.getType().isAir() && !canPut(player, put)) return false;
        return take == null || take.getType().isAir() || canTake(player, take);
    }

    public boolean canMove(Player player, InventoryEvent event) {
        if(event instanceof InventoryClickEvent click) {
            return canMove(player, click);
        } else if(event instanceof InventoryDragEvent drag) {
            return canMove(player, drag);
        }
        return false;
    }

    private boolean canMove(Player player, @NotNull InventoryClickEvent event) {
        PlayerInventory playerInventory = player.getInventory();
        ClickType click = event.getClick();

        return switch (click) {
            case LEFT, RIGHT -> check(player, event.getCursor(), event.getCurrentItem());
            case DROP, CONTROL_DROP -> check(player, null, event.getCurrentItem());
            case NUMBER_KEY -> check(player, playerInventory.getItem(event.getHotbarButton()), event.getCurrentItem());
            case SWAP_OFFHAND -> check(player, playerInventory.getItemInOffHand(), event.getCurrentItem());
            case CREATIVE -> true;
            default -> false;
        };
    }

    private boolean canMove(Player player, @NotNull InventoryDragEvent event) {
        if(event.getRawSlots().size() != 1) return false;

        return canPut(player, event.getOldCursor());
    }

}
