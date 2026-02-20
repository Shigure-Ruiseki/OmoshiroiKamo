package ruiseki.omoshiroikamo.api.block;

import net.minecraft.tileentity.TileEntity;

import ruiseki.omoshiroikamo.core.datastructure.BlockPos;

public interface IOKTile {

    TileEntity getTileEntity();

    BlockPos getPos();
}
