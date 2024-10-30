package me.cyrzu.git.supermenu.inventory;

import me.cyrzu.git.supermenu.Range;
import me.cyrzu.git.supermenu.SuperMenu;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public abstract class AbstractFullMoveableMenu extends AbstractMenu {

    @NotNull
    private final static NamespacedKey key = new NamespacedKey(SuperMenu.getManager().getInstance(), "supermenu");

    private final @NotNull Set<Integer> disabled;

    @Nullable
    private Predicate<ItemStack> filter;

    public AbstractFullMoveableMenu(int rows) {
        this(rows, "");
    }

    public AbstractFullMoveableMenu(int rows, String title) {
        super(rows, title);
        this.disabled = new HashSet<>();
    }

    public AbstractFullMoveableMenu(@NotNull InventoryType type) {
        this(type, "");
    }


    public AbstractFullMoveableMenu(@NotNull InventoryType type, @NotNull String title) {
        super(type, title);
        this.disabled = new HashSet<>();
    }

    public void setDisabledSlots(@NotNull IntStream stream) {
        stream.forEach(this::setDisabledSlot);
    }

    public void setDisabledSlots(int... slots) {
        Arrays.stream(slots).forEach(this::setDisabledSlot);
    }

    public void setDisabledSlots(Collection<Integer> slots) {
        slots.forEach(this::setDisabledSlot);
    }

    public void setDisabledSlots(Range... ranges) {
        for (Range range : ranges) {
            this.setDisabledSlots(range.getStream());
        }
    }

    public void setDisabledSlot(int slot) {
        this.disabled.add(slot);
    }

    public boolean isDisabled(int slot) {
        return this.disabled.contains(slot);
    }

    public Set<Integer> getDisableds() {
        return Set.copyOf(this.disabled);
    }

    public void setDisabledSlotsItem(@NotNull ItemStack itemStack) {
        this.setItem(itemStack, this.disabled);
    }

    public void clearAndSetEnabled(int... slots) {
        this.clearAndSetEnabled(IntStream.of(slots).boxed().toList());
    }

    public void clearAndSetEnabled(@NotNull Collection<Integer> slots) {
        if(this.started) {
            return;
        }

        this.disabled.clear();
        IntStream.range(0, inventory.getSize())
            .filter(slot -> !slots.contains(slot))
            .forEach(this::setDisabledSlot);
    }

    public void setFilter(@NotNull Predicate<ItemStack> filter) {
        this.filter = filter;
    }

    public boolean isFiltered(@Nullable ItemStack itemStack) {
        return itemStack == null || (this.filter == null || this.filter.test(itemStack));
    }

    public List<ItemStack> getItems(boolean removeItems) {
        return this.getItems(removeItems, true);
    }

    public List<ItemStack> getItems(boolean removeItems, boolean removeAir) {
        List<ItemStack> items = new ArrayList<>();
        ItemStack air = new ItemStack(Material.AIR);

        int index = 0;
        for (ItemStack itemStack : this.inventory) {
            if(itemStack == null || (itemStack.getType() == Material.AIR && removeAir) || this.isDisabled(index)) {
                index++;
                continue;
            }

            items.add(itemStack);
            if(removeItems) {
                inventory.setItem(index, air);
            }

            index++;
        }

        return items;
    }

    @Override
    protected void onStart() {
        ItemStack itemStack = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta != null) {
            itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "moveablemenu");
            itemStack.setItemMeta(itemMeta);
        }

        for (Integer slot : this.getDisableds()) {
            ItemStack slotItem = inventory.getItem(slot);
            if(slotItem != null && slotItem.getType() != Material.AIR) {
                continue;
            }

            inventory.setItem(slot, itemStack);
        }
    }

}
