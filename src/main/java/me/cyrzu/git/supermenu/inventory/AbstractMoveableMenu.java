package me.cyrzu.git.supermenu.inventory;

import me.cyrzu.git.supermenu.MenuMoveableSlot;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

public abstract class AbstractMoveableMenu extends AbstractMenu {

    private final @NotNull Map<Integer, MenuMoveableSlot> moveableSlots;

    public AbstractMoveableMenu(int rows) {
        this(rows, "");
    }

    public AbstractMoveableMenu(int rows, String title) {
        super(rows, title);
        this.moveableSlots = new HashMap<>();
    }

    public AbstractMoveableMenu(@NotNull InventoryType type) {
        this(type, "");
    }


    public AbstractMoveableMenu(@NotNull InventoryType type, @NotNull String title) {
        super(type, title);
        this.moveableSlots = new HashMap<>();
    }

    public final void setMoveableSlots(int[] slots) {
        for (int slot : slots) {
            this.setMoveableSlot(slot);
        }
    }

    public final void setMoveableSlots(@NotNull IntStream stream, @Nullable Function<ItemStack, Boolean> put,
                                       @Nullable Function<ItemStack, Boolean> take) {
        for (int slot : stream.toArray()) {
            this.setMoveableSlot(slot, put, take);
        }
    }

    public final void setMoveableSlots(@NotNull IntStream stream, @Nullable BiFunction<Player, ItemStack, Boolean> put,
                                       @Nullable BiFunction<Player, ItemStack, Boolean> take) {
        for (int slot : stream.toArray()) {
            this.setMoveableSlot(slot, put, take);
        }
    }

    public final void setMoveableSlots(int[] slots, @Nullable Function<ItemStack, Boolean> put,
                                       @Nullable Function<ItemStack, Boolean> take) {
        for (int slot : slots) {
            this.setMoveableSlot(slot, put, take);
        }
    }

    public final void setMoveableSlots(int[] slots, @Nullable BiFunction<Player, ItemStack, Boolean> put,
                                      @Nullable BiFunction<Player, ItemStack, Boolean> take) {
        for (int slot : slots) {
            this.setMoveableSlot(slot, put, take);
        }
    }

    public final void setMoveableSlot(int slot) {
        this.setMoveableSlot(slot, (Function<ItemStack, Boolean>) null, null);
    }

    public final void setMoveableSlot(int slot, @Nullable Function<ItemStack, Boolean> put) {
        this.setMoveableSlot(slot, put == null ? null : (p, i) -> put.apply(i), null);
    }

    public final void setMoveableSlot(int slot, @Nullable BiFunction<Player, ItemStack, Boolean> put) {
        this.setMoveableSlot(slot, put, null);
    }

    public final void setMoveableSlot(int slot, @Nullable Function<ItemStack, Boolean> put,
                                      @Nullable Function<ItemStack, Boolean> take) {
        this.setMoveableSlot(slot, put == null ? null : (p, i) -> put.apply(i), take == null ? null : (p, i) -> take.apply(i));
    }

    public final void setMoveableSlot(int slot, @Nullable BiFunction<Player, ItemStack, Boolean> put,
                                       @Nullable BiFunction<Player, ItemStack, Boolean> take) {
        moveableSlots.put(slot, new MenuMoveableSlot(put, take));
    }

    public final @Nullable MenuMoveableSlot getMoveableSlot(int slot) {
        return moveableSlots.get(slot);
    }

    public final boolean hasMoveableSlots() {
        return !moveableSlots.isEmpty();
    }

}
