package me.cyrzu.git.supermenu.inventory;

import net.kyori.adventure.text.Component;

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

}
