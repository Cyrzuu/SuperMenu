package me.cyrzu.supermenu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import supermenu.inventory.AbstractMenu;
import supermenu.inventory.AbstractMoveableMenu;

import java.util.Objects;

public class MenuListeners implements Listener {

    private final MenuManager menuManager;

    public MenuListeners(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        AbstractMenu inventory = menuManager.getMenuHandler(event.getInventory());
        Inventory clickedInventory = event.getClickedInventory();

        if(inventory instanceof AbstractMoveableMenu moveableMenu && moveableMenu.hasMoveableSlots()) {
            int rawSlot = event.getRawSlot();

            ItemStack cursor = event.getCursor();
            if(cursor != null && event.getClick() == ClickType.DOUBLE_CLICK) {
                event.setCancelled(true);
                return;
            }

            if(clickedInventory != null) {
                if(clickedInventory.getType() == InventoryType.PLAYER) {
                    if(event.isShiftClick()) {
                        event.setCancelled(true);
                    }

                    return;
                }

                MenuMoveableSlot menuMoveableSlot = moveableMenu.getMoveableSlot(rawSlot);
                if(menuMoveableSlot != null) {
                    if(!menuMoveableSlot.canMove(player, event))
                        event.setCancelled(true);

                    return;
                } else if(clickedInventory.getType() != InventoryType.PLAYER) {
                    event.setCancelled(true);
                }
            }

            if(inventory.getButton(rawSlot) != null) {
                event.setCancelled(true);
            }
        }

        if(inventory != null) {
            event.setCancelled(true);

            if(!inventory.hasCooldown(player)) {
                inventory.onClick(player, event);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        AbstractMenu menuHandler = menuManager.getMenuHandler(event.getInventory());
        int size = event.getInventory().getSize() - 1;

        if(menuHandler instanceof AbstractMoveableMenu moveableMenu) {
            if(event.getRawSlots().size() == 1) {
                event.getRawSlots().stream().findFirst().ifPresent(slot -> {
                    MenuMoveableSlot moveableSlot = moveableMenu.getMoveableSlot(slot);
                    if((moveableSlot == null || !moveableSlot.canMove(player, event)) && slot < size) {
                        event.setCancelled(true);
                    }
                });
            } else if(event.getRawSlots().stream().anyMatch(slot -> slot < size)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        AbstractMenu menuHandler = menuManager.getMenuHandler(e.getInventory());

        if(menuHandler != null) {
            var close = menuHandler.getClose();
            if(close != null && !close.test(player, menuHandler)) {
                Bukkit.getScheduler().runTask(menuManager.getInstance(), () -> menuHandler.open(player));
                return;
            }

            if(menuHandler.isUnregisterOnClose()) {
                menuManager.unregister(menuHandler);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPluginDisable(PluginDisableEvent event) {
        if(!Objects.equals(event.getPlugin(), menuManager.getInstance())) {
            return;
        }

        menuManager.unregisterAll();
    }

}
