package ruiseki.omoshiroikamo.common.network;

import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.api.energy.IEnergyTile;

public class PacketEnergy extends MessageTileEntity<TileEntity>
    implements IMessage, IMessageHandler<PacketEnergy, IMessage> {

    private int storedEnergy;

    public PacketEnergy() {}

    public PacketEnergy(IEnergyTile tile) {
        super(tile.getTileEntity());
        storedEnergy = tile.getEnergyStored();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        buf.writeInt(storedEnergy);

    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        storedEnergy = buf.readInt();
    }

    @Override
    public IMessage onMessage(PacketEnergy message, MessageContext ctx) {
        TileEntity te = message.getTileEntity(OmoshiroiKamo.proxy.getClientWorld());
        if (te instanceof IEnergyTile me) {
            me.setEnergyStored(message.storedEnergy);
        }
        return null;
    }
}
