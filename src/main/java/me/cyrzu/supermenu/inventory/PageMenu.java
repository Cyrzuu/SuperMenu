package me.cyrzu.supermenu.inventory;

import lombok.Getter;
import me.cyrzu.supermenu.Range;
import me.cyrzu.supermenu.button.ButtonHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PageMenu<E> extends MenuHandler {

    @Getter
    private int page;

    @Getter
    private int currentPage = 1;

    @NotNull
    private final ArrayList<E> objects = new ArrayList<>();

    @NotNull
    private Set<@NotNull Integer> slots;

    @NotNull
    public final BiFunction<E, @NotNull Integer, @NotNull ItemStack> biFunction;

    @Nullable
    public Consumer<E> objectClick;

    public PageMenu(int rows, @NotNull Collection<E> objects, @NotNull BiFunction<E, @NotNull Integer, @NotNull ItemStack> function) {
        this(rows, objects, "", function);
    }

    public PageMenu(int rows, @NotNull Collection<E> objects, String title, @NotNull BiFunction<E, @NotNull Integer, @NotNull ItemStack> function) {
        super(rows, title);

        this.biFunction = function;
        this.slots = IntStream.rangeClosed(0, 53).boxed().collect(Collectors.toSet());
        this.page = Math.max(1, page);
        this.objects.addAll(objects);
    }

    public PageMenu<E> setSlots(@NotNull Range... ranges) {
        if(ranges == null || ranges.length == 0) {
            return this;
        }

        Set<@NotNull Integer> newSlots = new HashSet<>();
        for (Range range : ranges) {
            newSlots.addAll(range.get());
        }

        if(!newSlots.isEmpty())
            this.slots = newSlots;

        return this;
    }

    public PageMenu<E> setSlots(@NotNull Integer... slots) {
        if(slots == null || slots.length == 0) {
            return this;
        }

        int size = inventory.getSize();
        Set<@NotNull Integer> newSlots = Arrays.stream(slots)
                .filter(slot -> slot >= 0 && slot < size)
                .collect(Collectors.toSet());

        if(!newSlots.isEmpty())
            this.slots = newSlots;

        return this;
    }

    public PageMenu<E> setObjects(@NotNull Collection<E> collection) {
        objects.clear();
        objects.addAll(collection);
        return this;
    }

    public PageMenu<E> setOnClickObject(@NotNull Consumer<E> consumer) {
        this.objectClick = consumer;
        return this;
    }

    public boolean hasNextPage() {
        return currentPage < page;
    }

    public boolean hasPreviosPage() {
        return currentPage > 1;
    }

    public void nextPage() {
        if(!hasNextPage()) {
            throw new RuntimeException("Next page not found");
        }

        currentPage++;
        updatePage();
    }

    public void lastPage() {
        this.currentPage = page;
        updatePage();
    }

    public void previosPage() {
        if(!hasPreviosPage()) {
            throw new RuntimeException("Previos page not found");
        }

        currentPage--;
        updatePage();
    }

    public void firstPage() {
        this.currentPage = 1;
        updatePage();
    }

    public void setPage(int page) {
        this.currentPage = Math.max(1, Math.min(this.page, page));
        updatePage();
    }


    private void updatePage() {

    }

    @Override
    protected void onClick(int slot) {
        int i = List.copyOf(slots).indexOf(slot);
        int index = i * currentPage;

        if(objectClick != null && i >= 0 && index < objects.size()) {
            objectClick.accept(objects.get(index));
        }
    }

    public PageMenu<E> onClose(@NotNull BiConsumer<@NotNull Player, @NotNull Inventory> action) {
        super.onClose(action);
        return this;
    }

    @Override
    public PageMenu<E> setButton(int slot, ButtonHandler button) {
        super.setButton(slot, button);
        return this;
    }

    @Override
    public PageMenu<E> setTask(long period, @NotNull Runnable runnable) {
        super.setTask(period, runnable);
        return this;
    }

    @Override
    public PageMenu<E> setTask(@NotNull Runnable runnable) {
        super.setTask(runnable);
        return this;
    }


    public PageMenu<E> setPermission(@NotNull String permission) {
        super.setPermission(new Permission(permission));
        return this;
    }

    @Override
    public PageMenu<E> setUnregisterOnClose(boolean unregister) {
        super.setUnregisterOnClose(unregister);
        return this;
    }

    @Override
    public PageMenu<E> fillAll(@NotNull ItemStack stack) {
        return fillAll(stack, new Integer[0]);
    }

    @Override
    public PageMenu<E> fillAll(@NotNull ItemStack stack, @NotNull Integer... ignore) {
        super.fillAll(stack, ignore);
        return this;
    }

    @Override
    public PageMenu<E> setMoveableSlot(int slot) {
        return setMoveableSlot(slot, null, null);
    }

    @Override
    public PageMenu<E> setMoveableSlot(int slot, @Nullable BiFunction<Player, ItemStack, Boolean> put) {
        return setMoveableSlot(slot, put, null);
    }

    @Override
    public PageMenu<E> setMoveableSlot(int slot, @Nullable BiFunction<Player, ItemStack, Boolean> put,
                                    @Nullable BiFunction<Player, ItemStack, Boolean> take) {
        super.setMoveableSlot(slot, put, take);
        return this;
    }

    @Override
    public PageMenu<E> setItem(int slot, @NotNull ItemStack stack) {
        super.setItem(slot, stack);
        return this;
    }

}
