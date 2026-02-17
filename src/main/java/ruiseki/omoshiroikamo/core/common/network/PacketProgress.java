package ruiseki.omoshiroikamo.core.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.api.client.IProgressTile;
import ruiseki.omoshiroikamo.api.network.CodecField;
import ruiseki.omoshiroikamo.api.network.PacketCodec;

public class PacketProgress extends PacketCodec {

    @CodecField
    private BlockPos pos;
    @CodecField
    private float progress;

    public PacketProgress() {}

    public PacketProgress(IProgressTile tile) {
        pos = tile.getPos();
        progress = tile.getProgress();
        if (progress == 0) {
            progress = -1;
        }
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void actionClient(World world, EntityPlayer player) {
        TileEntity tile = world.getTileEntity(pos.getX(), pos.getY(), pos.getZ());
        if (tile instanceof IProgressTile progressTile) {
            progressTile.setProgress(progress);
        }
    }

    @Override
    public void actionServer(World world, EntityPlayerMP player) {

    }
}
