package ruiseki.omoshiroikamo.core.tileentity;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.core.datastructure.BlockPos;
import ruiseki.omoshiroikamo.core.datastructure.DimPos;

public interface ITile {

    BlockPos getPos();

    DimPos getDimPos();

    public World getWorld();

    public int getX();

    public int getY();

    public int getZ();

    public void mark();

    public int getWorldID();

    default TileEntity getTile() {
        return getWorld().getTileEntity(getX(), getY(), getZ());
    }

    default int getMeta() {
        return getWorld().getBlockMetadata(getX(), getY(), getZ());
    }

    public void updateTEState();

    public void updateTELight();

    public Block getBlock();

}
