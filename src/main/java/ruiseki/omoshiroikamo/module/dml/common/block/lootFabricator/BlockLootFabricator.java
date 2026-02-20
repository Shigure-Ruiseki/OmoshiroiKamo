package ruiseki.omoshiroikamo.module.dml.common.block.lootFabricator;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.block.AbstractBlock;
import ruiseki.omoshiroikamo.core.helper.BlockStateHelpers;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.core.tileentity.TileEntityOK;

public class BlockLootFabricator extends AbstractBlock<TELootFabricator> {

    protected BlockLootFabricator() {
        super(ModObject.blockLootFabricator.unlocalisedName, TELootFabricator.class);
    }

    public static BlockLootFabricator create() {
        return new BlockLootFabricator();
    }

    @Override
    public void init() {
        BlockStateHelpers.registerCraftingStateProp(this.getClass());
        super.init();
    }

    @Override
    public int getRenderType() {
        return ModelISBRH.JSON_ISBRH_ID;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TELootFabricator();
    }

    @Override
    public int damageDropped(int meta) {
        return 0;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        dropStacks(world, x, y, z);
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    protected void processDrop(World world, int x, int y, int z, TileEntityOK te, ItemStack stack) {

    }

    @Override
    public void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        TileEntity tile = accessor.getTileEntity();
        if (tile instanceof TELootFabricator te) {
            tooltip.add(WailaUtils.getCraftingState(te));
            tooltip.add(WailaUtils.getProgress(te));
        }
    }
}
