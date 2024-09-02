package me.cyrzu.git.supermenu.inventory;

import lombok.Getter;
import me.cyrzu.git.supermenu.ItemButtonState;
import me.cyrzu.git.supermenu.Range;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PageMenu<E> extends AbstractMoveableMenu {

    @Getter
    private int pages;

    @Getter
    private int currentPage = 0;

    @NotNull
    private final Slots slots;

    @NotNull
    private final ArrayList<E> objects;

    @NotNull
    private final BiFunction<@NotNull E, @NotNull Integer, @NotNull ItemStack> biFunction;

    @Nullable
    private BiConsumer<@NotNull E, @NotNull ItemButtonState> objectClick;

    protected int nextPageSlot = -1;

    @Nullable
    private Runnable next;

    protected int previousPageSlot = -1;

    @Nullable
    private Runnable previous;

    @Nullable
    private ItemStack emptySlot;

    @Nullable
    private ItemStack nextPageItem;

    @Nullable
    private ItemStack emptyNextPageItem;

    @Nullable
    private ItemStack previousPageItem;

    @Nullable
    private ItemStack emptyPreviousPageItem;

    public PageMenu(int rows, @NotNull Collection<E> objects, @NotNull Function<@NotNull E, @NotNull ItemStack> function) {
        this(rows, objects, ((e, integer) -> function.apply(e)));
    }

    public PageMenu(int rows, @NotNull Collection<E> objects, @NotNull String title, @NotNull Function<@NotNull E, @NotNull ItemStack> function) {
        this(rows, objects, title, ((e, integer) -> function.apply(e)));
    }

    public PageMenu(int rows, @NotNull Collection<E> objects, @NotNull BiFunction<@NotNull E, @NotNull Integer, @NotNull ItemStack> function) {
        this(rows, objects, "", function);
    }

    public PageMenu(int rows, @NotNull Collection<E> objects, @NotNull String title, @NotNull BiFunction<@NotNull E, @NotNull Integer, @NotNull ItemStack> function) {
        super(rows, title);

        this.biFunction = function;
        this.slots = new Slots(this.getInventory());
        this.pages = calculatePages(objects.size(), slots.size());
        this.objects = new ArrayList<>(objects);
    }

    public PageMenu(@NotNull InventoryType type, @NotNull Collection<E> objects, @NotNull BiFunction<@NotNull E, @NotNull Integer, @NotNull ItemStack> function) {
        this(type, objects, "", function);
    }

    public PageMenu(@NotNull InventoryType type, @NotNull Collection<E> objects, @NotNull String title, @NotNull BiFunction<@NotNull E, @NotNull Integer, @NotNull ItemStack> function) {
        super(type, title);

        this.biFunction = function;
        this.slots = new Slots(this.getInventory());
        this.pages = this.calculatePages(objects.size(), slots.size());
        this.objects = new ArrayList<>(objects);
    }

    public int calculatePages(int sizeList, int numberOfSlots) {
        double ceil = Math.ceil((double) sizeList / numberOfSlots);
        return (int) Math.max(0, ceil);
    }

    @Override
    protected boolean onFunctionClick(@NotNull Player player, @NotNull InventoryClickEvent event) {
        ItemStack currentItem = event.getCurrentItem();
        int slot = event.getRawSlot();

        if(nextPageSlot != -1 && slot == nextPageSlot) {
            if(!this.hasNextPage()) {
                return false;
            }

            this.nextPage();
            return true;
        }

        if(previousPageSlot != -1 && slot == previousPageSlot) {
            if(!this.hasPreviousPage()) {
                return false;
            }

            this.previousPage();
            return true;
        }

        if(buttons.get(slot) != null) {
            return true;
        }

        if(objectClick == null) {
            return false;
        }

        int i1 = slots.indexOf(slot);
        if(i1 >= 0) {
            int index = (currentPage * slots.size()) + i1;
            if (index >= 0 && index < objects.size()) {
                E e = objects.get(index);
                objectClick.accept(e, new ItemButtonState(this.inventory, player, slot, event.getClick(), currentItem != null ? currentItem : new ItemStack(Material.STONE)));
            }
        }

        return true;
    }


    @Override
    protected void onStart() {
        this.updateSlots();
    }

    public final void onClickObject(@NotNull Consumer<@NotNull E> fun) {
        this.onClickObject((e, state) -> fun.accept(e));
    }

    public final void onClickObject(@NotNull BiConsumer<@NotNull E, @NotNull ItemButtonState> fun) {
        this.objectClick = fun;
    }

    public void setNextPageButton(int slot, @NotNull ItemStack stack) {
        this.setNextPageButton(slot, stack, () -> {});
    }

    public void setNextPageButton(int slot, @NotNull ItemStack stack, @Nullable Runnable runnable) {
        nextPageSlot = Math.min(inventory.getSize() - 1, Math.max(0, slot));
        inventory.setItem(nextPageSlot, stack);

        this.next = runnable;
    }

    public void setNextPageButtons(int slot, @NotNull ItemStack item, @NotNull ItemStack itemEmpty, @Nullable Runnable fun) {
        this.nextPageSlot = Math.min(inventory.getSize() - 1, Math.max(0, slot));
        this.inventory.setItem(this.nextPageSlot, this.hasNextPage() ? item : itemEmpty);

        this.next = fun;
    }

    public void setPreviousPageButton(int slot, @NotNull ItemStack stack) {
        setPreviousPageButton(slot, stack, () -> {});
    }

    public void setPreviousPageButton(int slot, @NotNull ItemStack stack, @Nullable Runnable fun) {
        previousPageSlot = Math.min(inventory.getSize() - 1, Math.max(0, slot));
        inventory.setItem(previousPageSlot, stack);

        this.previous = fun;
    }

    public void setPreviousPageButtons(int slot, @NotNull ItemStack item, @NotNull ItemStack itemEmpty, @Nullable Runnable fun) {
        this.previousPageSlot = Math.min(inventory.getSize() - 1, Math.max(0, slot));
        this.inventory.setItem(this.previousPageSlot, this.hasPreviousPage() ? item : itemEmpty);

        this.previous = fun;
    }

    public void setSlots(@NotNull Collection<@NotNull Integer> slots) {
        if(started) return;
        this.slots.setSlots(slots);
        this.pages = Math.max(1, (int) Math.ceil((double) objects.size() / slots.size()));
    }

    public void setSlots(@NotNull Range... ranges) {
        if(started) return;
        slots.setSlots(Arrays.stream(ranges).flatMapToInt(Range::getStream));
        this.pages = calculatePages(objects.size(), slots.size());
    }

    public void setSlots(int... integers) {
        if(started) return;
        slots.setSlots(integers);
        this.pages = Math.max(1, (int) Math.ceil((double) objects.size() / slots.size()));
    }

    public void setSlots(@NotNull IntStream stream) {
        if(started) return;
        slots.setSlots(stream);
        this.pages = Math.max(1, (int) Math.ceil((double) objects.size() / slots.size()));
    }

    public void setObjects(@NotNull Collection<E> objects) {
        this.pages = calculatePages(objects.size(), slots.size());
        this.objects.clear();
        this.objects.addAll(objects);

        if(!this.hasPage(currentPage)) {
            this.firstPage();
            return;
        }

        this.updateSlots();
    }

    public boolean hasNextPage() {
        return currentPage < (pages - 1);
    }

    public boolean hasPreviousPage() {
        return currentPage > 0;
    }

    public boolean hasPage(int page) {
        return page < pages;
    }

    public void nextPage() {
        if(!this.hasNextPage()) {
            throw new RuntimeException("Next page not found");
        }

        currentPage++;
        this.updateSlots();

        if(next != null) {
            next.run();
        }
    }

    public void lastPage() {
        this.currentPage = pages - 1;
        this.updateSlots();
    }

    public void previousPage() {
        if(!this.hasPreviousPage()) {
            throw new RuntimeException("Previos page not found");
        }

        currentPage--;
        this.updateSlots();

        if(previous != null) {
            previous.run();
        }
    }

    public void firstPage() {
        this.currentPage = 0;
        this.updateSlots();
    }

    public void setPage(int page) {
        this.currentPage = Math.max(0, Math.min(this.pages, page));
        this.updateSlots();
    }

    private void updateSlots() {
        ItemStack empty = emptySlot != null ? emptySlot : new ItemStack(Material.AIR);
        slots.getSlots().forEach(slot -> this.setItem(slot, empty));

        Integer[] slots = this.slots.getSlots().toArray(Integer[]::new);
        int[] objectsIndex = this.getObjectsIndex();
        int index = 0;

        for (int i : objectsIndex) {
            E object = objects.get(i);
            ItemStack stack = biFunction.apply(object, i);
            this.setItem(slots[index++], stack);
        }

        if(this.nextPageSlot >= 0 && this.nextPageItem != null && this.emptyNextPageItem != null) {
            this.setItem(this.nextPageSlot, this.hasNextPage() ? this.nextPageItem : this.emptyNextPageItem);
        }

        if(this.previousPageSlot >= 0 && this.previousPageItem != null && this.emptyPreviousPageItem != null) {
            this.setItem(this.nextPageSlot, this.hasPreviousPage() ? this.previousPageItem : this.emptyPreviousPageItem);
        }

    }

    private int[] getObjectsIndex() {
        int start = currentPage * slots.size();
        int end = Math.min((currentPage + 1) * slots.size(), objects.size());
        return IntStream.range(start, end).toArray();
    }

    private static class Slots {

        @NotNull
        private ArrayList<Integer> slots = new ArrayList<>(List.of(0));

        @Getter
        @NotNull
        private Integer[] arraySlots = new Integer[]{0};

        public Slots(@NotNull Inventory inventory) {
            this.setSlots(IntStream.range(0, inventory.getSize())
                    .boxed()
                    .toList());
        }

        public void setSlots(@NotNull Collection<@NotNull Integer> slots) {
            this.slots = new ArrayList<>(slots);
            this.arraySlots = slots.toArray(Integer[]::new);
        }

        public void setSlots(@NotNull Range... ranges) {
            this.setSlots(Arrays.stream(ranges)
                    .flatMap(range -> range.get().stream())
                    .collect(Collectors.toSet()));
        }

        public void setSlots(@NotNull IntStream stream) {
            this.setSlots(stream.boxed()
                    .distinct()
                    .toList());
        }

        public void setSlots(int... integers) {
            if(integers.length == 0) {
                return;
            }

            this.setSlots(IntStream.of(integers));
        }

        public int indexOf(int slot) {
            return slots.indexOf(slot);
        }

        public int size() {
            return slots.size();
        }

        public Collection<Integer> getSlots() {
            return Collections.unmodifiableCollection(slots);
        }

    }

}
