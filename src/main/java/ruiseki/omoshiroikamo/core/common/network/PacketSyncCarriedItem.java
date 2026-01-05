package ruiseki.omoshiroikamo.core.common.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketSyncCarriedItem implements IMessage, IMessageHandler<PacketSyncCarriedItem, IMessage> {

    private ItemStack stack;

    public PacketSyncCarriedItem() {}

    public PacketSyncCarriedItem(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.stack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, stack);
    }

    @Override
    public IMessage onMessage(PacketSyncCarriedItem msg, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        player.inventory.setItemStack(msg.stack);
        player.inventory.markDirty();

        return null;
    }
}
