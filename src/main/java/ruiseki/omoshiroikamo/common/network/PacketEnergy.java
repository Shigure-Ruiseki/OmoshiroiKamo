package ruiseki.omoshiroikamo.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.api.energy.IEnergyTile;

public class PacketEnergy implements IMessage, IMessageHandler<PacketEnergy, IMessage> {

    private int x;
    private int y;
    private int z;
    private int storedEnergy;

    public PacketEnergy() {}

    public PacketEnergy(IEnergyTile tile) {
        BlockPos pos = tile.getLocation();
        x = pos.x;
        y = pos.y;
        z = pos.z;
        storedEnergy = tile.getEnergyStored();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(storedEnergy);

    }

    @Override
    public void toBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        storedEnergy = buf.readInt();

    }

    @Override
    public IMessage onMessage(PacketEnergy message, MessageContext ctx) {
        EntityPlayer player = OmoshiroiKamo.proxy.getClientPlayer();
        TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
        if (te instanceof IEnergyTile me) {
            me.setEnergyStored(message.storedEnergy);
        }
        return null;
    }
}
