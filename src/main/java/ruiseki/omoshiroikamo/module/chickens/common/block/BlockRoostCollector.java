package ruiseki.omoshiroikamo.module.chickens.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.block.AbstractBlock;

public class BlockRoostCollector extends AbstractBlock<TERoostCollector> {

    protected BlockRoostCollector() {
        super(ModObject.blockRoostCollector.unlocalisedName, TERoostCollector.class, Material.wood);
    }

    public static BlockRoostCollector create() {
        return new BlockRoostCollector();
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new TERoostCollector();
    }
}
