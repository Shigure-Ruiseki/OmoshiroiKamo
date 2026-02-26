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

    public TileEntity getTile();

    public int getMeta();

    public void updateTEState();

    public void updateTELight();

    public Block getBlock();

}
