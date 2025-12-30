package ruiseki.omoshiroikamo.module.dml.common.block.simulationCharmber;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.common.block.TileEntityOK;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractBlock;
import ruiseki.omoshiroikamo.core.common.block.state.BlockStateUtils;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;

public class BlockSimulationChamber extends AbstractBlock<TESimulationChamber> {

    protected BlockSimulationChamber() {
        super(ModObject.blockSimulationChamber.unlocalisedName, TESimulationChamber.class);
    }

    public static BlockSimulationChamber create() {
        return new BlockSimulationChamber();
    }

    @Override
    public void init() {
        BlockStateUtils.registerCraftingStateProp(this.getClass());
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
        return new TESimulationChamber();
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
        if (tile instanceof TESimulationChamber te) {
            tooltip.add(WailaUtils.getCraftingState(te));
            tooltip.add(WailaUtils.getProgress(te));
        }
    }
}
