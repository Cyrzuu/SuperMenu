package me.cyrzu.git.supermenu.inventory;

import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

public class FullMoveableMenu extends AbstractFullMoveableMenu {

    public FullMoveableMenu(int rows) {
        super(rows);
    }

    public FullMoveableMenu(int rows, String title) {
        super(rows, title);
    }

    public FullMoveableMenu(@NotNull InventoryType type) {
        super(type);
    }

    public FullMoveableMenu(@NotNull InventoryType type, @NotNull String title) {
        super(type, title);
    }

}
