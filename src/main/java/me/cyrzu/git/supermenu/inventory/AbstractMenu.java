package me.cyrzu.git.supermenu.inventory;

import lombok.Getter;
import me.cyrzu.git.supermenu.CooldownManager;
import me.cyrzu.git.supermenu.MenuTask;
import me.cyrzu.git.supermenu.SuperMenu;
import me.cyrzu.git.supermenu.button.ButtonHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public abstract class AbstractMenu {

    @NotNull
    public static Random random = new Random();

    @Getter
    protected boolean unregistered = false;

    protected boolean started = false;

    @NotNull
    private final SuperMenu superMenu;

    @NotNull
    protected final Inventory inventory;

    @Nullable
    @Getter
    private BiPredicate<@NotNull Player, @NotNull AbstractMenu> close;

    @NotNull
    protected final Map<Integer, ButtonHandler> buttons;

    @Nullable
    protected MenuTask menuTask = null;

    @NotNull
    private final CooldownManager cooldown;

    private boolean unregisterOnClose = true;

    public AbstractMenu(int rows) {
        this(rows, "");
    }

    public AbstractMenu(int rows, String title) {
        this(rows, Component.text(title));
    }

    public AbstractMenu(int rows, Component title) {
        this.superMenu = SuperMenu.getManager();
        this.inventory = Bukkit.createInventory(null, Math.min(6, rows) * 9, title);
        this.buttons = new HashMap<>();
        this.cooldown = new CooldownManager(150L);
    }

    protected void onStart() { }

    protected void onClick(@NotNull Player player, int slot) {

    }

    public final void onClick(@NotNull Player player, @NotNull InventoryClickEvent event) {
        onClick(player, event.getRawSlot());
        ButtonHandler button = buttons.get(event.getRawSlot());
        if(button != null) {
            button.runClick(player, event);
        }
    }

    public final void onClose(BiPredicate<@NotNull Player, @NotNull AbstractMenu> canClose) {
        this.close = canClose;
    }

    public final void setCooldown(int time, TimeUnit unit) {
        cooldown.setNewCooldown(unit.toMillis(time));
    }

    public final void setTask(@NotNull Runnable runnable) {
        this.menuTask = new MenuTask(superMenu.getInstance(), runnable);
    }

    public final void setTask(@NotNull Runnable runnable, long period) {
        this.menuTask = new MenuTask(superMenu.getInstance(), runnable, period);
    }

    public final void unregisterOnClose(boolean unregister) {
        this.unregisterOnClose = unregister;
    }

    public final void cancelTask() {
        if(menuTask != null) menuTask.cancel();
    }

    public final void open(@NotNull Player player) {
        if(!started) {
            SuperMenu.registerMenu(this);
            this.started = true;
            onStart();
            if(menuTask != null) {
                menuTask.run();
            }
        }

        player.openInventory(inventory);
    }

    public final void unregister() {
        this.unregistered = true;
        SuperMenu.getManager().unregister(this);
    }

    @NotNull
    public final Inventory getInventory() {
        return inventory;
    }

    public final boolean isUnregisterOnClose() {
        return unregisterOnClose;
    }

    public final boolean hasCooldown(@NotNull Player player) {
        boolean b = cooldown.hasCooldown(player);
        if(!b) cooldown.setCooldown(player);
        return b;
    }

    public final void setButton(int slot, ButtonHandler buttonHandler) {
        int min = Math.min(inventory.getSize() - 1, slot);
        inventory.setItem(min, buttonHandler.getStack());
        buttons.put(min, buttonHandler);
    }

    @Nullable
    public final ButtonHandler getButton(int slot) {
        return buttons.get(slot);
    }

    public final void removeButtons() {
        buttons.clear();
    }

    public final void removeButton(int slot) {
        buttons.remove(slot);
    }


    public final void setItem(@NotNull ItemStack stack, @NotNull Integer... slots) {
        Arrays.stream(slots).forEach(slot -> setItem(slot, stack));
    }

    public final void setItem(int slot, @NotNull ItemStack stack) {
        inventory.setItem(Math.min(inventory.getSize() - 1, slot), stack);
    }

    @Nullable
    public final ItemStack getStack(int slot) {
        return inventory.getItem(slot);
    }


    public final void fillAll(@NotNull ItemStack stack) {
        fillAll(stack, new Integer[0]);
    }

    public final void fillAll(@NotNull ItemStack stack, @NotNull Integer... ignore) {
        Set<@NotNull Integer> collect = Arrays.stream(ignore).collect(Collectors.toSet());

        for (int i = 0; i < inventory.getSize(); i++) {
            if(collect.contains(i)) {
                continue;
            }

            inventory.setItem(i, stack);
        }
    }

    public final void fillBorder(@NotNull ItemStack stack) {
        int rows = inventory.getSize() / 9;
        if (rows <= 2) {
            return;
        }

        for (int i = 0; i < rows * 9; i++) {
            if (i <= 8 || (i >= rows * 9 - 8 && i <= rows * 9 - 2) || i % 9 == 0 || i % 9 == 8)
                inventory.setItem(i, stack);
        }
    }

    public int randomSlot() {
        return random.nextInt(inventory.getSize());
    }

    public void setTitle(@NotNull String title) {
        inventory.getViewers().forEach(he -> he.getOpenInventory().setTitle(title));
    }

    public void close() {
        ArrayList<HumanEntity> humanEntities = new ArrayList<>(inventory.getViewers());
        humanEntities.forEach(HumanEntity::closeInventory);

        if(!unregistered) {
            unregister();
        }
    }

}
