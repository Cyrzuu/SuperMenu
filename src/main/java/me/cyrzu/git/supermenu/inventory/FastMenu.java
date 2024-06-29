package me.cyrzu.git.supermenu.inventory;

import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

public class FastMenu extends AbstractMoveableMenu {

    public FastMenu(int rows, Component title) {
        super(rows, title);
    }

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

    public FastMenu(@NotNull InventoryType type, @NotNull Component title) {
        super(type, title);
    }

}
