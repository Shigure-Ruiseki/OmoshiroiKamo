package ruiseki.omoshiroikamo.module.machinery.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.core.network.CodecField;
import ruiseki.omoshiroikamo.core.network.PacketCodec;
import ruiseki.omoshiroikamo.core.tileentity.ISidedIO;

public class PacketToggleSide extends PacketCodec {

    @CodecField
    public int side;
    @CodecField
    public int x, y, z;
    @CodecField
    public boolean reverse;

    public PacketToggleSide() {}

    public PacketToggleSide(ISidedIO tile, ForgeDirection side) {
        this(tile, side, false);
    }

    public PacketToggleSide(ISidedIO tile, ForgeDirection side, boolean reverse) {
        this.x = tile.getX();
        this.y = tile.getY();
        this.z = tile.getZ();
        this.side = side.ordinal();
        this.reverse = reverse;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {}

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof ISidedIO io) {
            io.toggleSide(ForgeDirection.getOrientation(side), reverse);
        }
    }
}
