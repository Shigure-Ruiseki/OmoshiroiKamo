package ruiseki.omoshiroikamo.core.common.network;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import ruiseki.omoshiroikamo.api.client.IContainerWithTileEntity;

public class PacketUtil {

    public static NBTTagCompound readNBTTagCompound(ByteBuf dataIn) {
        try {
            short size = dataIn.readShort();
            if (size < 0) {
                return null;
            } else {
                byte[] buffer = new byte[size];
                dataIn.readBytes(buffer);
                return CompressedStreamTools.func_152457_a(buffer, NBTSizeTracker.field_152451_a);
            }
        } catch (IOException e) {
            FMLCommonHandler.instance()
                .raiseException(e, "Custom Packet", true);
            return null;
        }
    }

    public static void writeNBTTagCompound(NBTTagCompound compound, ByteBuf dataout) {
        try {
            if (compound == null) {
                dataout.writeShort(-1);
            } else {
                byte[] buf = CompressedStreamTools.compress(compound);
                dataout.writeShort((short) buf.length);
                dataout.writeBytes(buf);
            }
        } catch (IOException e) {
            FMLCommonHandler.instance()
                .raiseException(e, "PacketUtil.readTileEntityPacket.writeNBTTagCompound", true);
        }
    }

    public static byte[] readByteArray(ByteBuf buf) {
        int size = buf.readMedium();
        byte[] res = new byte[size];
        buf.readBytes(res);
        return res;
    }

    public static void writeByteArray(ByteBuf buf, byte[] arr) {
        buf.writeMedium(arr.length);
        buf.writeBytes(arr);
    }

    public static boolean isInvalidPacketForGui(MessageContext ctx, TileEntity receivedTile, Class<?> messageClass) {
        if (receivedTile == null) {
            // Invalid, but not harmful
            return true;
        }
        if (ctx.side == Side.CLIENT) {
            return false;
        }
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        Container container = player.openContainer;
        if (!(container instanceof IContainerWithTileEntity)) {
            return true;
        }
        TileEntity expectedTile = ((IContainerWithTileEntity) container).getTileEntity();
        if (receivedTile != expectedTile) {
            return true;
        }
        return false;
    }
}
