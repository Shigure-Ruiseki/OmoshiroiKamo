package ruiseki.omoshiroikamo.module.machinery.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.api.block.ISidedIO;

public class PacketToggleSide implements IMessage, IMessageHandler<PacketToggleSide, IMessage> {

    public byte side;
    public BlockPos pos;

    public PacketToggleSide() {}

    public PacketToggleSide(ISidedIO tile, ForgeDirection side) {
        this.pos = tile.getPos();
        this.side = (byte) side.ordinal();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPos.fromLong(buf.readLong());
        side = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeByte(side);
    }

    @Override
    public IMessage onMessage(PacketToggleSide message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        World world = player.worldObj;
        TileEntity te = world.getTileEntity(message.pos.x, message.pos.y, message.pos.z);
        if (te instanceof ISidedIO io) {
            io.toggleSide(ForgeDirection.getOrientation(message.side));
        }
        return null;
    }
}
