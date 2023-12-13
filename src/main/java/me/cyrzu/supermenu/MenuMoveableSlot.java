package me.cyrzu.supermenu;

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

public class MenuMoveableSlot {

    private final @Nullable BiFunction<Player, ItemStack, Boolean> put;

    private final @Nullable BiFunction<Player, ItemStack, Boolean> take;

    public MenuMoveableSlot(@Nullable BiFunction<Player, ItemStack, Boolean> put, @Nullable BiFunction<Player, ItemStack, Boolean> take) {
        this.put = put;
        this.take = take;
    }

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
        if(event instanceof InventoryClickEvent) {
            return canMove(player, (InventoryClickEvent) event);
        } else if(event instanceof InventoryDragEvent) {
            return canMove(player, (InventoryDragEvent) event);
        }
        return false;
    }

    private boolean canMove(Player player, @NotNull InventoryClickEvent event) {
        PlayerInventory playerInventory = player.getInventory();
        ClickType click = event.getClick();

        switch (click) {
            case LEFT:
            case RIGHT:
                return check(player, event.getCursor(), event.getCurrentItem());
            case DROP:
            case CONTROL_DROP:
                return check(player, null, event.getCurrentItem());
            case NUMBER_KEY:
                return check(player, playerInventory.getItem(event.getHotbarButton()), event.getCurrentItem());
            case SWAP_OFFHAND:
                return check(player, playerInventory.getItemInOffHand(), event.getCurrentItem());
            case CREATIVE:
                return true;
            default:
                return false;
        }

//        return switch (click) {
//            case LEFT, RIGHT -> check(player, event.getCursor(), event.getCurrentItem());
//            case DROP, CONTROL_DROP -> check(player, null, event.getCurrentItem());
//            case NUMBER_KEY -> check(player, playerInventory.getItem(event.getHotbarButton()), event.getCurrentItem());
//            case SWAP_OFFHAND -> check(player, playerInventory.getItemInOffHand(), event.getCurrentItem());
//            case CREATIVE -> true;
//            default -> false;
//        };
    }

    private boolean canMove(Player player, @NotNull InventoryDragEvent event) {
        if(event.getRawSlots().size() != 1) return false;

        return canPut(player, event.getOldCursor());
    }

}
