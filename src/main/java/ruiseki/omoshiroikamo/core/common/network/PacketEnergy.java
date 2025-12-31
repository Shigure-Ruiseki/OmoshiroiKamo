package ruiseki.omoshiroikamo.core.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.api.energy.IOKEnergyTile;

public class PacketEnergy implements IMessage, IMessageHandler<PacketEnergy, IMessage> {

    private BlockPos pos;
    private int storedEnergy;

    public PacketEnergy() {}

    public PacketEnergy(IOKEnergyTile tile) {
        storedEnergy = tile.getEnergyStored();
        pos = tile.getPos();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(storedEnergy);
        buf.writeLong(pos.toLong());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        storedEnergy = buf.readInt();
        pos = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public IMessage onMessage(PacketEnergy message, MessageContext ctx) {
        EntityPlayer player = OmoshiroiKamo.proxy.getClientPlayer();
        if (player == null) return null;
        TileEntity tile = player.worldObj.getTileEntity(message.pos.x, message.pos.y, message.pos.z);
        if (tile instanceof IOKEnergyTile te) {
            te.setEnergyStored(message.storedEnergy);
        }
        return null;
    }
}
