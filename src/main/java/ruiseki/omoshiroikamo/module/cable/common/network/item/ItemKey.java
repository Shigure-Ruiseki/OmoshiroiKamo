package ruiseki.omoshiroikamo.module.cable.common.network.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemKey {

    public final Item item;
    public final int meta;
    public final NBTTagCompound tag;

    public ItemKey(ItemStack stack) {
        this.item = stack.getItem();
        this.meta = stack.getItemDamage();
        this.tag = stack.getTagCompound() != null ? (NBTTagCompound) stack.getTagCompound()
            .copy() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ItemKey k)) return false;
        if (item != k.item) return false;
        if (meta != k.meta) return false;
        if (tag == null && k.tag == null) return true;
        if (tag != null && k.tag != null) return tag.equals(k.tag);
        return false;
    }

    @Override
    public int hashCode() {
        int h = item.hashCode() * 31 + meta;
        if (tag != null) h = h * 31 + tag.hashCode();
        return h;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(item.getUnlocalizedName());
        sb.append(":")
            .append(meta);

        if (tag != null && !tag.hasNoTags()) {
            sb.append(" ")
                .append(tag.toString());
        }

        return sb.toString();
    }

}
