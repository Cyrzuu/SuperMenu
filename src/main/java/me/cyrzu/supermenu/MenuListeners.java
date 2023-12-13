package me.cyrzu.supermenu;

import me.cyrzu.supermenu.inventory.MenuHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class MenuListeners implements Listener {

    private final MenuManager menuManager;

    public MenuListeners(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        MenuHandler inventory = menuManager.getMenuHandler(event.getInventory());
        Inventory clickedInventory = event.getClickedInventory();

        if(inventory != null) {
            int rawSlot = event.getRawSlot();
            if(clickedInventory != null) {

                if(clickedInventory.getType() == InventoryType.PLAYER && event.isShiftClick()) {
                    event.setCancelled(true);
                    return;
                }

                MenuMoveableSlot menuMoveableSlot = inventory.getMoveableSlot(rawSlot);
                if(menuMoveableSlot != null) {
                    if(!menuMoveableSlot.canMove(player, event))
                        event.setCancelled(true);

                    return;
                } else if(!inventory.hasMoveableSlots() || clickedInventory.getType() != InventoryType.PLAYER) {
                    event.setCancelled(true);
                }
            }

            if(!inventory.hasMoveableSlots() && inventory.getButton(rawSlot) != null) {
                event.setCancelled(true);
            }

            if(inventory.hasCooldown(player, true)) {
                return;
            }

            inventory.onClick(player, event);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        MenuHandler menuHandler = menuManager.getMenuHandler(event.getInventory());
        int size = event.getInventory().getSize() - 1;

        if(menuHandler != null && event.getRawSlots().size() == 1) {
            event.getRawSlots().stream().findFirst().ifPresent(slot -> {
                MenuMoveableSlot moveableSlot = menuHandler.getMoveableSlot(slot);
                if((moveableSlot == null || !moveableSlot.canMove(player, event)) && slot < size) event.setCancelled(true);
            });
        }else if(menuHandler != null && event.getRawSlots().stream().anyMatch(slot -> slot < size)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent e) {
        MenuHandler menuHandler = menuManager.getMenuHandler(e.getInventory());

        if(menuHandler != null && menuHandler.isUnregisterOnClose()) {
            menuManager.unregister(menuHandler);
        }
    }

}
