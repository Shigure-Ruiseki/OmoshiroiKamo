package ruiseki.omoshiroikamo.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import com.enderio.core.common.network.NetworkUtil;
import com.enderio.core.common.util.BlockCoord;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractStorageTE;

public class PacketFluidTanks implements IMessage, IMessageHandler<PacketFluidTanks, IMessage> {

    private int x, y, z;
    private NBTTagCompound nbtRoot;

    public PacketFluidTanks() {}

    public PacketFluidTanks(AbstractStorageTE tile) {
        BlockCoord bc = tile.getLocation();
        this.x = bc.x;
        this.y = bc.y;
        this.z = bc.z;

        nbtRoot = new NBTTagCompound();
        NBTTagCompound tanksTag = new NBTTagCompound();

        for (int i = 0; i < tile.fluidTanks.length; i++) {
            NBTTagCompound tankTag = new NBTTagCompound();
            tile.fluidTanks[i].writeToNBT(tankTag);
            tanksTag.setTag("Tank" + i, tankTag);
        }
        nbtRoot.setTag("FluidTanks", tanksTag);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        NetworkUtil.writeNBTTagCompound(nbtRoot, buf);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        nbtRoot = NetworkUtil.readNBTTagCompound(buf);
    }

    @Override
    public IMessage onMessage(PacketFluidTanks message, MessageContext ctx) {
        EntityPlayer player = ctx.side == Side.SERVER ? ctx.getServerHandler().playerEntity
            : Minecraft.getMinecraft().thePlayer;

        if (player == null) {
            return null;
        }
        TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
        if (!(te instanceof AbstractStorageTE storage)) {
            return null;
        }

        NBTTagCompound tanksTag = message.nbtRoot.getCompoundTag("FluidTanks");
        for (int i = 0; i < storage.fluidTanks.length; i++) {
            String key = "Tank" + i;
            if (tanksTag.hasKey(key)) {
                storage.fluidTanks[i].readFromNBT(tanksTag.getCompoundTag(key));
            } else {
                storage.fluidTanks[i].setFluid(null);
            }
        }

        return null;
    }
}
