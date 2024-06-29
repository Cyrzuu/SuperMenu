package me.cyrzu.git.supermenu.inventory;

import me.cyrzu.git.supermenu.MenuMoveableSlot;
import me.cyrzu.git.supermenu.SuperMenu;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public abstract class AbstractMoveableMenu extends AbstractMenu {

    private final @NotNull Map<Integer, MenuMoveableSlot> moveableSlots;

    public AbstractMoveableMenu(int rows) {
        this(rows, "");
    }

    public AbstractMoveableMenu(int rows, String title) {
        super(rows, title);
        this.moveableSlots = new HashMap<>();
    }

    public AbstractMoveableMenu(int rows, Component title) {
        super(rows, title);
        this.moveableSlots = new HashMap<>();
    }

    public AbstractMoveableMenu(@NotNull InventoryType type) {
        this(type, "");
    }

    public AbstractMoveableMenu(@NotNull InventoryType type, @NotNull String title) {
        this(type, Component.text(title));
    }

    public AbstractMoveableMenu(@NotNull InventoryType type, @NotNull Component title) {
        super(type, title);
        this.moveableSlots = new HashMap<>();
    }

    public final void setMoveableSlot(int slot) {
        setMoveableSlot(slot, null, null);
    }

    public final void setMoveableSlot(int slot, @Nullable BiFunction<Player, ItemStack, Boolean> put) {
        setMoveableSlot(slot, put, null);
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
