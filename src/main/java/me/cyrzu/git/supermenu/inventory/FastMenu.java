package me.cyrzu.git.supermenu.inventory;

import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

public class FastMenu extends AbstractMoveableMenu {

    public FastMenu(int rows, String title) {
        super(rows, title);
    }

    public FastMenu(int rows) {
        super(rows);
    }

    public FastMenu(@NotNull InventoryType type) {
        super(type);
    }

    public FastMenu(@NotNull InventoryType type, @NotNull String title) {
        super(type, title);
    }

}
