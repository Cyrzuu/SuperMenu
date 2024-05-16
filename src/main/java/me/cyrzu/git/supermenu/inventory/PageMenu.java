package me.cyrzu.git.supermenu.inventory;

import lombok.Getter;
import me.cyrzu.git.supermenu.ItemButtonState;
import me.cyrzu.git.supermenu.Range;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
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
    private Consumer<@NotNull Integer> next;

    protected int previousPageSlot = -1;

    @Nullable
    private Consumer<@NotNull Integer> previous;

    public PageMenu(int rows, @NotNull Collection<E> objects, @NotNull Function<@NotNull E, @NotNull ItemStack> function) {
        this(rows, objects, ((e, integer) -> function.apply(e)));
    }

    public PageMenu(int rows, @NotNull Collection<E> objects, @NotNull String title, @NotNull Function<@NotNull E, @NotNull ItemStack> function) {
        this(rows, objects, title, ((e, integer) -> function.apply(e)));
    }

    public PageMenu(int rows, @NotNull Collection<E> objects, @NotNull Component title, @NotNull Function<@NotNull E, @NotNull ItemStack> function) {
        this(rows, objects, title, ((e, integer) -> function.apply(e)));
    }

    public PageMenu(int rows, @NotNull Collection<E> objects, @NotNull BiFunction<@NotNull E, @NotNull Integer, @NotNull ItemStack> function) {
        this(rows, objects, "", function);
    }

    public PageMenu(int rows, @NotNull Collection<E> objects, @NotNull String title, @NotNull BiFunction<@NotNull E, @NotNull Integer, @NotNull ItemStack> function) {
        this(rows, objects, Component.text(title), function);
    }

    public PageMenu(int rows, @NotNull Collection<E> objects, @NotNull Component title, @NotNull BiFunction<@NotNull E, @NotNull Integer, @NotNull ItemStack> function) {
        super(rows, title);

        this.biFunction = function;
        this.slots = new Slots(getInventory());
        this.pages = calculatePages(objects.size(), slots.size());
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
        updateSlots();
    }

    public final void onClickObject(@NotNull Consumer<@NotNull E> fun) {
        this.onClickObject((e, state) -> fun.accept(e));
    }

    public final void onClickObject(@NotNull BiConsumer<@NotNull E, @NotNull ItemButtonState> fun) {
        this.objectClick = fun;
    }

    public void setNextPageButton(int slot, @NotNull ItemStack stack) {
        setNextPageButton(slot, stack, null);
    }

    public void setNextPageButton(int slot, @NotNull ItemStack stack, @Nullable Consumer<Integer> onClick) {
        nextPageSlot = Math.min(inventory.getSize() - 1, Math.max(0, slot));
        inventory.setItem(nextPageSlot, stack);

        this.next = onClick;
    }

    public void setPreviousPageButton(int slot, @NotNull ItemStack stack) {
        setPreviousPageButton(slot, stack, null);
    }

    public void setPreviousPageButton(int slot, @NotNull ItemStack stack, @Nullable Consumer<Integer> onClick) {
        previousPageSlot = Math.min(inventory.getSize() - 1, Math.max(0, slot));
        inventory.setItem(previousPageSlot, stack);

        this.previous = onClick;
    }

    public void setSlots(@NotNull Collection<@NotNull Integer> slots) {
        if(started) return;
        this.slots.setSlots(slots);
        this.pages = Math.max(1, (int) Math.ceil((double) objects.size() / slots.size()));
    }

    public void setSlots(@NotNull Range... ranges) {
        if(started) return;
        slots.setSlots(ranges);
        this.pages = calculatePages(objects.size(), slots.size());
    }

    public void setSlots(@NotNull Integer... integers) {
        if(started) return;
        slots.setSlots(integers);
        this.pages = Math.max(1, (int) Math.ceil((double) objects.size() / slots.size()));
    }

    public void setObjects(@NotNull Collection<E> objects) {
        this.pages = calculatePages(objects.size(), slots.size());
        this.objects.clear();
        this.objects.addAll(objects);

        if(!hasPage(currentPage)) {
            firstPage();
            return;
        }

        updateSlots();
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
        if(!hasNextPage()) {
            throw new RuntimeException("Next page not found");
        }

        currentPage++;
        updateSlots();

        if(next != null) {
            next.accept(currentPage);
        }
    }

    public void lastPage() {
        this.currentPage = pages - 1;
        updateSlots();
    }

    public void previousPage() {
        if(!hasPreviousPage()) {
            throw new RuntimeException("Previos page not found");
        }

        currentPage--;
        updateSlots();

        if(previous != null) {
            previous.accept(currentPage);
        }
    }

    public void firstPage() {
        this.currentPage = 0;
        updateSlots();
    }

    public void setPage(int page) {
        this.currentPage = Math.max(0, Math.min(this.pages, page));
        updateSlots();
    }

    private void updateSlots() {
        ItemStack empty = new ItemStack(Material.AIR);
        slots.getSlots().forEach(slot -> setItem(slot, empty));

        Integer[] slots = this.slots.getSlots().toArray(Integer[]::new);
        int[] objectsIndex = getObjectsIndex();
        int index = 0;

        for (int i : objectsIndex) {
            E object = objects.get(i);
            ItemStack stack = biFunction.apply(object, i);
            setItem(slots[index++], stack);
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
            setSlots(IntStream.range(0, inventory.getSize())
                    .boxed()
                    .toList());
        }

        public void setSlots(@NotNull Collection<@NotNull Integer> slots) {
            this.slots = new ArrayList<>(slots);
            this.arraySlots = slots.toArray(Integer[]::new);
        }

        public void setSlots(@NotNull Range... ranges) {
            if(ranges == null || ranges.length == 0) {
                return;
            }

            setSlots(Arrays.stream(ranges)
                    .flatMap(range -> range.get().stream())
                    .distinct()
                    .toList());
        }

        public void setSlots(@NotNull Integer... integers) {
            if(integers == null || integers.length == 0) {
                return;
            }

            setSlots(Arrays.stream(integers)
                    .distinct()
                    .toList());
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
