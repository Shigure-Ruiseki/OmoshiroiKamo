package ruiseki.omoshiroikamo.core.common.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketQuickDraw implements IMessage, IMessageHandler<PacketQuickDraw, IMessage> {

    public int slot;
    public ItemStack stack;

    public PacketQuickDraw() {}

    public PacketQuickDraw(int slot, ItemStack stack) {
        this.slot = slot;
        this.stack = stack;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slot = buf.readInt();
        stack = ItemStack.loadItemStackFromNBT(PacketUtil.readNBTTagCompound(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slot);
        if (stack == null) {
            PacketUtil.writeNBTTagCompound(null, buf);
        } else {
            PacketUtil.writeNBTTagCompound(stack.writeToNBT(new NBTTagCompound()), buf);
        }
    }

    @Override
    public IMessage onMessage(PacketQuickDraw message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        player.inventory.setInventorySlotContents(message.slot, message.stack);
        return null;
    }
}
