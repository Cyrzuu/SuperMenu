package me.cyrzu.supermenu.inventory;

import lombok.Getter;
import me.cyrzu.supermenu.Range;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LegacyPageMenu<E> extends AbstractMenu {

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

    public LegacyPageMenu(int rows, @NotNull Collection<E> objects, @NotNull BiFunction<E, @NotNull Integer, @NotNull ItemStack> function) {
        this(rows, objects, "", function);
    }

    public LegacyPageMenu(int rows, @NotNull Collection<E> objects, String title, @NotNull BiFunction<E, @NotNull Integer, @NotNull ItemStack> function) {
        super(rows, title);

        this.biFunction = function;
        this.slots = IntStream.rangeClosed(0, 53).boxed().collect(Collectors.toSet());
        this.page = (int) Math.ceil((double) objects.size() / slots.size());
        this.objects.addAll(objects);
    }

    public LegacyPageMenu<E> setSlots(@NotNull Range... ranges) {
        if(ranges == null || ranges.length == 0) {
            return this;
        }

        Set<@NotNull Integer> newSlots = new HashSet<>();
        for (Range range : ranges) {
            newSlots.addAll(range.get());
        }

        if(!newSlots.isEmpty())
            this.slots = newSlots;

        this.page = (int) Math.ceil((double) objects.size() / newSlots.size());
        firstPage();

        return this;
    }

    public LegacyPageMenu<E> setSlots(@NotNull Integer... slots) {
        if(slots == null || slots.length == 0) {
            return this;
        }

        int size = inventory.getSize();
        Set<@NotNull Integer> newSlots = Arrays.stream(slots)
                .filter(slot -> slot >= 0 && slot < size)
                .collect(Collectors.toSet());

        if(!newSlots.isEmpty())
            this.slots = newSlots;

        this.page = (int) Math.ceil((double) objects.size() / newSlots.size());
        firstPage();

        return this;
    }

    public LegacyPageMenu<E> setObjects(@NotNull Collection<E> collection) {
        objects.clear();
        objects.addAll(collection);
        return this;
    }

    public LegacyPageMenu<E> setOnClickObject(@NotNull Consumer<E> consumer) {
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
        ItemStack stack = new ItemStack(Material.AIR);
        slots.forEach(slot -> setItem(slot, stack));

        int[] slots = getSlotsPage(currentPage, this.slots.size(), objects.size());
        for (int i = 0; i < slots.length; i++) {
            int i1 = ((currentPage - 1) * this.slots.size());
            E e = objects.get(i1 + i);
            ItemStack apply = biFunction.apply(e, i1 + 1);
            setItem(slots[i], apply);
        }
    }

//    private int[] getSlotsPage(int page, Set<?> slotsTable, int pageSize) {
//        int stI = (page - 1) * slotsTable.size(), enI = Math.min(page * slotsTable.size(), pageSize);
//        int[] slotsOnPage = new int[enI - stI];
//        for (int i = stI; i < enI; i++) slotsOnPage[i - stI] = i;
//        return slotsOnPage;
//    }

    private int[] getSlotsPage(int page, int pageSlotCount, int collectionSize) {
        int start = (page - 1) * pageSlotCount;
        int end = Math.min(page * pageSlotCount, collectionSize);
        return IntStream.rangeClosed(start, end).toArray();
    }

}
