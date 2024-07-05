package me.cyrzu.git.supermenu.inventory;

import lombok.Getter;
import lombok.Setter;
import me.cyrzu.git.supermenu.ItemButtonState;
import me.cyrzu.git.supermenu.MenuTask;
import me.cyrzu.git.supermenu.SuperMenu;
import me.cyrzu.git.supermenu.button.ButtonHandler;
import me.cyrzu.git.supermenu.button.EmptyButton;
import me.cyrzu.git.supermenu.button.ItemButton;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
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

    @Setter
    @Getter
    @NotNull
    private ItemStack[] lastContents = new ItemStack[0];

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

    public AbstractMenu(@NotNull InventoryType type) {
        this(type, "");
    }

    public AbstractMenu(@NotNull InventoryType type, @NotNull String title) {
        this(type, Component.text(title));
    }

    public AbstractMenu(@NotNull InventoryType type, @NotNull Component title) {
        this.superMenu = SuperMenu.getManager();
        this.inventory = Bukkit.createInventory(null, type, title);
        this.buttons = new HashMap<>();
        this.cooldown = new CooldownManager(150L);
    }

    protected void onStart() { }

    protected boolean onFunctionClick(@NotNull Player player, @NotNull InventoryClickEvent event) {
        return true;
    }

    protected boolean onClick(@NotNull Player player, int slot) {
        return true;
    }

    public final void onClick(@NotNull Player player, @NotNull InventoryClickEvent event) {
        if(!this.onFunctionClick(player, event)) {
            return;
        }

        if(!this.onClick(player, event.getRawSlot())) {
            return;
        }

        ButtonHandler button = buttons.get(event.getRawSlot());
        if(button != null) {
            button.runClick(player, event);
        }
    }

    public final void onClose(Runnable function) {
        this.close = (player, menu) -> {
            function.run();
            return true;
        };
    }

    public final void onClose(Supplier<Boolean> function) {
        this.close = (player, menu) -> function.get();
    }

    public final void onClose(Predicate<@NotNull Player> canClose) {
        this.close = (player, menu) -> canClose.test(player);
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

    public final void setTask(@NotNull Runnable runnable, long period, boolean async) {
        this.menuTask = new MenuTask(superMenu.getInstance(), runnable, period, async);
    }

    public final void setDisabledCooldownSlots(@NotNull Integer... slots) {
        cooldown.disabledCooldown(slots);
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

    public final boolean hasCooldown(@NotNull Player player, int slot) {
        boolean b = cooldown.hasCooldown(player, slot);
        if(!b) cooldown.setCooldown(player);
        return b;
    }

    public final void setButtons(@Nullable ItemStack itemStack, @NotNull Runnable fun, @NotNull Integer... slots) {
        this.setButtons(itemStack, fun, Arrays.asList(slots));
    }

    public final void setButtons(@Nullable ItemStack itemStack, @NotNull Runnable fun, @NotNull Collection<Integer> slots) {
        for (Integer slot : slots) {
            this.setButton(slot, itemStack, fun);
        }
    }

    public final void setButtons(@Nullable ItemStack itemStack, @NotNull Consumer<ItemButtonState> fun, @NotNull Integer... slots) {
        this.setButtons(itemStack, fun, Arrays.asList(slots));
    }

    public final void setButtons(@Nullable ItemStack itemStack, @NotNull Consumer<ItemButtonState> fun, @NotNull Collection<Integer> slots) {
        for (Integer slot : slots) {
            this.setButton(slot, itemStack, fun);
        }
    }

    public final void setButtons(ButtonHandler buttonHandler, @NotNull Integer... slots) {
        this.setButtons(buttonHandler, Arrays.asList(slots));
    }

    public final void setButtons(ButtonHandler buttonHandler, @NotNull Collection<Integer> slots) {
        for (Integer slot : slots) {
            this.setButton(slot, buttonHandler);
        }
    }

    public final void setButton(int slot, @Nullable ItemStack itemStack, @NotNull Runnable fun) {
        this.setButton(slot, itemStack, state -> fun.run());
    }

    public final void setButton(int slot, @Nullable ItemStack itemStack, @NotNull Consumer<ItemButtonState> fun) {
        this.setButton(slot, itemStack == null || itemStack.getType().isAir() ?
                new EmptyButton(fun) : new ItemButton(itemStack, fun));
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
