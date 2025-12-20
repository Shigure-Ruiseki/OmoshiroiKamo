package ruiseki.omoshiroikamo.api.block;

import net.minecraft.tileentity.TileEntity;

public interface IOKTile {

    TileEntity getTileEntity();

    BlockPos getPos();
}
