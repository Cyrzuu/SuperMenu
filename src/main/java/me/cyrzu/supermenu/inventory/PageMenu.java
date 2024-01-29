package me.cyrzu.supermenu.inventory;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import supermenu.Range;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class PageMenu<E> extends AbstractMenu {

    @Getter
    private int pages;

    @Getter
    private int currentPage = 1;

    @NotNull
    private final Slots slots;

    @NotNull
    private final ArrayList<E> objects;

    @NotNull
    public final BiFunction<@NotNull E, @NotNull Integer, @NotNull ItemStack> biFunction;

    @Nullable
    public BiConsumer<@NotNull E, @NotNull Player> objectClick;

    public PageMenu(int rows, @NotNull Collection<E> objects, @NotNull BiFunction<E, @NotNull Integer, @NotNull ItemStack> function) {
        this(rows, objects, "", function);
    }

    public PageMenu(int rows, @NotNull Collection<E> objects, String title, @NotNull BiFunction<E, @NotNull Integer, @NotNull ItemStack> function) {
        super(rows, title);

        this.biFunction = function;
        this.slots = new Slots(getInventory());
        this.pages = Math.max(1, (int) Math.ceil((double) objects.size() / slots.size()));
        this.objects = new ArrayList<>(objects);
    }

    @Override
    protected void onClick(@NotNull Player player, int slot) {
        if(objectClick == null) {
            return;
        }

        int i1 = slots.indexOf(slot);
        if(i1 >= 0) {
            E e = objects.get(((currentPage - 1) * slots.size()) + i1);
            objectClick.accept(e, player);
        }
    }

    @Override
    protected void onStart() {
        updateSlots();
    }

    public final void setOnClickObject(@NotNull BiConsumer<E, Player> consumer) {
        this.objectClick = consumer;
    }

    public void setSlots(@NotNull Collection<@NotNull Integer> slots) {
        if(started) return;
        this.slots.setSlots(slots);
        this.pages = Math.max(1, (int) Math.ceil((double) objects.size() / slots.size()));
    }

    public void setSlots(@NotNull Range... ranges) {
        if(started) return;
        slots.setSlots(ranges);
        this.pages = Math.max(1, (int) Math.ceil((double) objects.size() / slots.size()));
    }

    public void setSlots(@NotNull Integer... integers) {
        if(started) return;
        slots.setSlots(integers);
        this.pages = Math.max(1, (int) Math.ceil((double) objects.size() / slots.size()));
    }

    public boolean hasNextPage() {
        return currentPage < pages;
    }

    public boolean hasPreviosPage() {
        return currentPage > 1;
    }

    public void nextPage() {
        if(!hasNextPage()) {
            throw new RuntimeException("Next page not found");
        }

        currentPage++;
        updateSlots();
    }

    public void lastPage() {
        this.currentPage = pages;
        updateSlots();
    }

    public void previosPage() {
        if(!hasPreviosPage()) {
            throw new RuntimeException("Previos page not found");
        }

        currentPage--;
        updateSlots();
    }

    public void firstPage() {
        this.currentPage = 1;
        updateSlots();
    }

    public void setPage(int page) {
        this.currentPage = Math.max(1, Math.min(this.pages, page));
        updateSlots();
    }

    private void updateSlots() {
        ItemStack airStack = new ItemStack(Material.AIR);
        slots.getSlots().forEach(slot -> setItem(slot, airStack));

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
        int start = (currentPage - 1) * slots.size();
        int end = Math.min(currentPage * slots.size(), objects.size());
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
