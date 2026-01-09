package ruiseki.omoshiroikamo.module.cable.common.network.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class ItemStackKey {

    private final Item item;
    private final int meta;
    private final NBTTagCompound tag;

    private final int hash;

    private ItemStackKey(Item item, int meta, NBTTagCompound tag) {
        this.item = item;
        this.meta = meta;
        this.tag = tag;
        this.hash = computeHash();
    }

    public static ItemStackKey of(ItemStack stack) {
        return new ItemStackKey(
            stack.getItem(),
            stack.getItemDamage(),
            stack.hasTagCompound() ? stack.getTagCompound() : null
        );
    }

    private int computeHash() {
        int h = Item.getIdFromItem(item);
        h = 31 * h + meta;
        if (tag != null) h = 31 * h + tag.hashCode();
        return h;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemStackKey other)) return false;
        if (item != other.item) return false;
        if (meta != other.meta) return false;
        if (tag == null && other.tag == null) return true;
        if (tag == null || other.tag == null) return false;
        return tag.equals(other.tag);
    }

    @Override
    public int hashCode() {
        return hash;
    }
}

