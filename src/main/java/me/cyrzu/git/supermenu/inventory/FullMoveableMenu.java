package me.cyrzu.git.supermenu.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

public class FullMoveableMenu extends AbstractFullMoveableMenu {

    public FullMoveableMenu(int rows) {
        super(rows);
    }

    public FullMoveableMenu(int rows, String title) {
        super(rows, title);
    }

    public FullMoveableMenu(int rows, Component title) {
        super(rows, title);
    }

    public FullMoveableMenu(@NotNull InventoryType type) {
        super(type);
    }

    public FullMoveableMenu(@NotNull InventoryType type, @NotNull String title) {
        super(type, title);
    }

    public FullMoveableMenu(@NotNull InventoryType type, @NotNull Component title) {
        super(type, title);
    }

}
