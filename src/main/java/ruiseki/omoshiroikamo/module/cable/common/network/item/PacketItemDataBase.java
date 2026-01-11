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

public class PacketItemDataBase implements IMessage, IMessageHandler<PacketItemDataBase, IMessage> {

    public Map<ItemKey, Long> db = new HashMap<>();

    public PacketItemDataBase() {}

    public PacketItemDataBase(Map<ItemKey, Long> db) {
        this.db.putAll(db);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(db.size());
        for (var e : db.entrySet()) {
            ItemKey k = e.getKey();
            buf.writeInt(Item.getIdFromItem(k.item));
            buf.writeInt(k.meta);
            buf.writeLong(e.getValue());

            buf.writeBoolean(k.tag != null);
            if (k.tag != null) {
                ByteBufUtils.writeTag(buf, k.tag);
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        db.clear();
        int size = buf.readInt();

        for (int i = 0; i < size; i++) {
            Item item = Item.getItemById(buf.readInt());
            int meta = buf.readInt();
            long amount = buf.readLong();

            NBTTagCompound tag = null;
            if (buf.readBoolean()) {
                tag = ByteBufUtils.readTag(buf);
            }

            ItemStack stack = new ItemStack(item, 1, meta);
            stack.setTagCompound(tag);

            db.put(new ItemKey(stack), amount);
        }
    }

    @Override
    public IMessage onMessage(PacketItemDataBase message, MessageContext ctx) {
        ClientItemDatabase.INSTANCE.update(message.db);
        return null;
    }
}
