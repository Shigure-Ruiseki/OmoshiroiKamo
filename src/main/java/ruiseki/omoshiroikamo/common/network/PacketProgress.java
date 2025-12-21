package ruiseki.omoshiroikamo.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.api.client.IProgressTile;

public class PacketProgress implements IMessageHandler<PacketProgress, IMessage>, IMessage {

    private BlockPos pos;
    private float progress;

    public PacketProgress() {}

    public PacketProgress(IProgressTile tile) {
        pos = tile.getPos();
        progress = tile.getProgress();
        if (progress == 0) {
            progress = -1;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(progress);
        buf.writeLong(pos.toLong());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        progress = buf.readFloat();
        pos = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public IMessage onMessage(PacketProgress message, MessageContext ctx) {
        EntityPlayer player = OmoshiroiKamo.proxy.getClientPlayer();
        TileEntity tile = player.worldObj.getTileEntity(message.pos.x, message.pos.y, message.pos.z);
        if (tile instanceof IProgressTile) {
            ((IProgressTile) tile).setProgress(message.progress);
        }
        return null;
    }
}
