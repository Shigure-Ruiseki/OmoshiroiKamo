package ruiseki.omoshiroikamo.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.blockpos.BlockPos;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import ruiseki.omoshiroikamo.api.io.IIoConfigurable;
import ruiseki.omoshiroikamo.api.io.IoMode;
import ruiseki.omoshiroikamo.api.io.IoType;

public class PacketIoMode implements IMessage, IMessageHandler<PacketIoMode, IMessage> {

    private int x;
    private int y;
    private int z;
    private IoMode mode;
    private IoType type;
    private ForgeDirection face;

    public PacketIoMode() {}

    public PacketIoMode(IIoConfigurable cont, IoType type) {
        BlockPos location = cont.getLocation();
        this.x = location.x;
        this.y = location.y;
        this.z = location.z;
        this.mode = IoMode.NONE;
        this.type = type;
        this.face = ForgeDirection.UNKNOWN;
    }

    public PacketIoMode(IIoConfigurable cont, ForgeDirection face, IoType type) {
        BlockPos location = cont.getLocation();
        this.x = location.x;
        this.y = location.y;
        this.z = location.z;
        this.face = face;
        this.type = type;
        this.mode = cont.getIoMode(face, type);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeShort((short) mode.ordinal());
        buf.writeShort((short) face.ordinal());
        buf.writeShort((short) type.ordinal());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        mode = IoMode.values()[buf.readShort()];
        face = ForgeDirection.values()[buf.readShort()];
        type = IoType.values()[buf.readShort()];
    }

    @Override
    public IMessage onMessage(PacketIoMode message, MessageContext ctx) {
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        TileEntity te = player.worldObj.getTileEntity(message.x, message.y, message.z);
        if (PacketUtil.isInvalidPacketForGui(ctx, te, getClass())) {
            return null;
        }
        if (te instanceof IIoConfigurable io) {
            if (message.face == ForgeDirection.UNKNOWN) {
                io.clearAllIoModes(message.type);
            } else {
                io.setIoMode(message.face, message.mode, message.type);
            }
        }
        return null;
    }
}
