package ruiseki.omoshiroikamo.common.network;

import net.minecraft.nbt.NBTTagCompound;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.common.block.cow.TEStall;

public class PacketStall extends MessageTileEntity<TEStall> implements IMessageHandler<PacketStall, IMessage> {

    private NBTTagCompound nbtRoot;

    public PacketStall() {}

    public PacketStall(TEStall tile) {
        super(tile);
        nbtRoot = new NBTTagCompound();
        if (tile.tank.getFluidAmount() > 0) {
            NBTTagCompound tankRoot = new NBTTagCompound();
            tile.tank.writeToNBT(tankRoot);
            nbtRoot.setTag("tank", tankRoot);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        PacketUtil.writeNBTTagCompound(nbtRoot, buf);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        nbtRoot = PacketUtil.readNBTTagCompound(buf);
    }

    @Override
    public IMessage onMessage(PacketStall message, MessageContext ctx) {
        TEStall tile = message.getTileEntity(OmoshiroiKamo.proxy.getClientWorld());
        if (message.nbtRoot.hasKey("tank")) {
            NBTTagCompound tankRoot = message.nbtRoot.getCompoundTag("tank");
            tile.tank.readFromNBT(tankRoot);
        } else {
            tile.tank.setFluid(null);
        }
        return null;
    }
}
