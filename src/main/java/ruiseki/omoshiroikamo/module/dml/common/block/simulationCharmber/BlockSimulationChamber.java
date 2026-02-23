package ruiseki.omoshiroikamo.module.dml.common.block.simulationCharmber;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.blockstate.properties.IntegerBlockProperty;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.block.AbstractBlock;
import ruiseki.omoshiroikamo.core.block.property.AutoBlockProperty;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.core.tileentity.TileEntityOK;

public class BlockSimulationChamber extends AbstractBlock<TESimulationChamber> {

    @AutoBlockProperty
    public static final IntegerBlockProperty CRAFTING_STATE = IntegerBlockProperty.meta("craftingState", 0b1100, 2);

    protected BlockSimulationChamber() {
        super(ModObject.blockSimulationChamber.unlocalisedName, TESimulationChamber.class);
    }

    public static BlockSimulationChamber create() {
        return new BlockSimulationChamber();
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
