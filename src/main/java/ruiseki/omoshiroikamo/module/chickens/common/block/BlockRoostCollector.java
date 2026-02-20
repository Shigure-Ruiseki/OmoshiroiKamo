package ruiseki.omoshiroikamo.module.chickens.common.block;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import net.minecraft.block.Block;
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
    public int getRenderType() {
        return JSON_ISBRH_ID;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new TERoostCollector();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        dropStacks(world, x, y, z);
        super.breakBlock(world, x, y, z, block, meta);
    }
}
