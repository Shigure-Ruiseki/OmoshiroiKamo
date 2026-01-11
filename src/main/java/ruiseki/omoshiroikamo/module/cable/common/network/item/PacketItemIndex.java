package ruiseki.omoshiroikamo.module.cable.common.network.item;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketItemIndex implements IMessage, IMessageHandler<PacketItemIndex, IMessage> {

    private Map<ItemStackKey, Integer> items = new HashMap<>();

    public PacketItemIndex() {}

    public PacketItemIndex(Map<ItemStackKey, Integer> snapshot) {
        this.items.putAll(snapshot);
    }

    // ---------- encode ----------
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(items.size());

        for (var e : items.entrySet()) {
            ItemStackKey k = e.getKey();

            buf.writeInt(Item.getIdFromItem(k.item));
            buf.writeInt(k.meta);
            buf.writeInt(e.getValue());

            buf.writeBoolean(k.tag != null);
            if (k.tag != null) {
                ByteBufUtils.writeTag(buf, k.tag);
            }
        }
    }

    // ---------- decode ----------
    @Override
    public void fromBytes(ByteBuf buf) {
        items.clear();
        int size = buf.readInt();

        for (int i = 0; i < size; i++) {
            Item item = Item.getItemById(buf.readInt());
            int meta = buf.readInt();
            int amount = buf.readInt();

            NBTTagCompound tag = null;
            if (buf.readBoolean()) {
                tag = ByteBufUtils.readTag(buf);
            }

            ItemStack stack = new ItemStack(item, 1, meta);
            if (tag != null) stack.setTagCompound(tag);

            items.put(ItemStackKey.of(stack), amount);
        }
    }

    // ---------- handle ----------
    @Override
    public IMessage onMessage(PacketItemIndex message, MessageContext ctx) {
        ItemIndexClient.INSTANCE.update(message.items);
        return null;
    }
}
