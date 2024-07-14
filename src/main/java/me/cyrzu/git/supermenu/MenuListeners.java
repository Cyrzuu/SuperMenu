package me.cyrzu.git.supermenu;

import me.cyrzu.git.supermenu.button.ButtonHandler;
import me.cyrzu.git.supermenu.inventory.AbstractFullMoveableMenu;
import me.cyrzu.git.supermenu.inventory.AbstractMenu;
import me.cyrzu.git.supermenu.inventory.AbstractMoveableMenu;
import me.cyrzu.git.supermenu.inventory.FullMoveableMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class MenuListeners implements Listener {

    private final SuperMenu superMenu;

    public MenuListeners(SuperMenu superMenu) {
        this.superMenu = superMenu;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        AbstractMenu inventory = superMenu.getMenuHandler(event.getInventory());
        Inventory clickedInventory = event.getClickedInventory();
        int rawSlot = event.getRawSlot();

        if(inventory instanceof AbstractFullMoveableMenu moveableMenu) {
            if(event.getClick() == ClickType.DOUBLE_CLICK || event.getClick() == ClickType.NUMBER_KEY) {
                event.setCancelled(true);
                return;
            }

            if(clickedInventory == null) {
                return;
            }

            if(!moveableMenu.isDisabled(rawSlot) && !moveableMenu.isFiltered(event.getCurrentItem())) {
                event.setCancelled(true);
                return;
            }

            ButtonHandler button = moveableMenu.getButton(rawSlot);
            if(moveableMenu.isDisabled(rawSlot) || button != null) {
                if(button != null) {
                    button.runClick(player, event);
                }

                event.setCancelled(true);
                return;
            }


            return;
        }

        if(inventory instanceof AbstractMoveableMenu moveableMenu && moveableMenu.hasMoveableSlots()) {
            if(clickedInventory != null) {
                if(clickedInventory.getType() == InventoryType.PLAYER) {
                    if(event.isShiftClick()) {
                        event.setCancelled(true);
                    }

                    return;
                }

                MenuMoveableSlot menuMoveableSlot = moveableMenu.getMoveableSlot(rawSlot);
                if(menuMoveableSlot != null) {
                    if(!menuMoveableSlot.canMove(player, event)) {
                        event.setCancelled(true);
                    }

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

            if(!inventory.hasCooldown(player, rawSlot)) {
                inventory.onClick(player, event);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        Player player = (Player) event.getWhoClicked();
        AbstractMenu menuHandler = superMenu.getMenuHandler(event.getInventory());
        int size = event.getInventory().getSize() - 1;

        if(menuHandler instanceof AbstractFullMoveableMenu moveableMenu) {
            if(event.getRawSlots().size() == 1) {
                event.getRawSlots().stream().findFirst().ifPresent(slot -> {
                    if((moveableMenu.isDisabled(slot)) && slot < size) {
                        event.setCancelled(true);
                    }
                });
            }
        } else if(menuHandler instanceof AbstractMoveableMenu moveableMenu) {
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
        AbstractMenu menuHandler = superMenu.getMenuHandler(e.getInventory());

        if(menuHandler != null) {
            var close = menuHandler.getClose();
            if(close != null && !close.test(player, menuHandler)) {
                Bukkit.getScheduler().runTask(superMenu.getInstance(), () -> menuHandler.open(player));
                return;
            }

            if(menuHandler.isUnregisterOnClose()) {
                superMenu.unregister(menuHandler, false);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPluginDisable(PluginDisableEvent event) {
        if(!Objects.equals(event.getPlugin(), superMenu.getInstance())) {
            return;
        }

        superMenu.unregisterAll();
    }



}
