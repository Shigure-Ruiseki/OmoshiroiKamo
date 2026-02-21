package ruiseki.omoshiroikamo.module.machinery.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.core.datastructure.BlockPos;
import ruiseki.omoshiroikamo.core.network.CodecField;
import ruiseki.omoshiroikamo.core.network.PacketCodec;
import ruiseki.omoshiroikamo.core.tileentity.ISidedIO;

public class PacketToggleSide extends PacketCodec {

    @CodecField
    public int side;
    @CodecField
    public BlockPos pos;

    public PacketToggleSide() {}

    public PacketToggleSide(ISidedIO tile, ForgeDirection side) {
        this.pos = tile.getPos();
        this.side = side.ordinal();
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {

    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {
        TileEntity te = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (te instanceof ISidedIO io) {
            io.toggleSide(ForgeDirection.getOrientation(side));
        }
    }
}
