package ruiseki.omoshiroikamo.module.cable.common.network.item;

import java.io.IOException;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

public final class ItemStackKey {

    public final Item item;
    public final int meta;
    public final NBTTagCompound tag;

    public final int hash;

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
            stack.hasTagCompound() ? stack.getTagCompound() : null);
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

    public ItemStack toStack(int count) {
        ItemStack stack = new ItemStack(this.item, count, this.meta);

        if (this.tag != null) {
            stack.setTagCompound((NBTTagCompound) this.tag.copy());
        }

        return stack;
    }

    public void write(PacketBuffer buf) throws IOException {
        buf.writeShort(Item.getIdFromItem(item));
        buf.writeShort(meta);

        if (tag != null) {
            buf.writeBoolean(true);
            buf.writeNBTTagCompoundToBuffer(tag);
        } else {
            buf.writeBoolean(false);
        }
    }

    public static ItemStackKey read(PacketBuffer buf) throws IOException {
        Item item = Item.getItemById(buf.readShort());
        int meta = buf.readShort();

        NBTTagCompound tag = null;
        if (buf.readBoolean()) {
            tag = buf.readNBTTagCompoundFromBuffer();
        }

        return new ItemStackKey(item, meta, tag);
    }
}
