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

    private final int hash;

    private final int tagHash;

    private ItemStackKey(Item item, int meta, NBTTagCompound tag) {
        this.item = item;
        this.meta = meta;

        if (tag != null && tag.hasNoTags()) {
            tag = null;
        }

        this.tag = tag != null ? (NBTTagCompound) tag.copy() : null;

        this.tagHash = this.tag != null ? this.tag.hashCode() : 0;
        this.hash = computeHash();
    }

    public static ItemStackKey of(ItemStack stack) {
        if (stack == null) throw new IllegalArgumentException("stack");

        return new ItemStackKey(
            stack.getItem(),
            stack.getItemDamage(),
            stack.hasTagCompound() ? stack.getTagCompound() : null);
    }

    private int computeHash() {
        int h = Item.getIdFromItem(item);
        h = 31 * h + meta;
        h = 31 * h + tagHash;
        return h;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemStackKey other)) return false;

        if (item != other.item) return false;
        if (meta != other.meta) return false;

        if (tag == null) return other.tag == null;
        if (other.tag == null) return false;

        return tag.equals(other.tag);
    }

    public ItemStack toStack(int count) {
        ItemStack stack = new ItemStack(item, count, meta);
        if (tag != null) {
            stack.setTagCompound((NBTTagCompound) tag.copy());
        }
        return stack;
    }

    public void write(PacketBuffer buf) throws IOException {
        buf.writeVarIntToBuffer(Item.getIdFromItem(item));
        buf.writeVarIntToBuffer(meta);

        if (tag != null) {
            buf.writeBoolean(true);
            buf.writeNBTTagCompoundToBuffer(tag);
        } else {
            buf.writeBoolean(false);
        }
    }

    public static ItemStackKey read(PacketBuffer buf) throws IOException {
        Item item = Item.getItemById(buf.readVarIntFromBuffer());
        int meta = buf.readVarIntFromBuffer();

        NBTTagCompound tag = null;
        if (buf.readBoolean()) {
            tag = buf.readNBTTagCompoundFromBuffer();
        }

        return new ItemStackKey(item, meta, tag);
    }
}
