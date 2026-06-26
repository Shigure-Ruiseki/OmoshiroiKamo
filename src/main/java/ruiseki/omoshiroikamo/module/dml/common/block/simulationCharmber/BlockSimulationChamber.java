package ruiseki.omoshiroikamo.module.dml.common.block.simulationCharmber;

import static com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry.registerProperty;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.block.AbstractBlock;
import ruiseki.omoshiroikamo.core.util.WailaUtils;

public class BlockSimulationChamber extends AbstractBlock<TESimulationChamber> {

    protected BlockSimulationChamber() {
        super(ModObject.SIMULATION_CHAMBER.name, TESimulationChamber.class);
        isOpaque = false;
    }

    public static BlockSimulationChamber create() {
        return new BlockSimulationChamber();
    }

    @Override
    public void registerProperties() {
        super.registerProperties();
        registerProperty(this, CRAFTING_STATE);
    }

    @Override
    public int damageDropped(int meta) {
        return 0;
    }

    @Override
    public void getWailaBody(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        TileEntity tile = accessor.getTileEntity();
        if (tile instanceof TESimulationChamber te) {
            tooltip.add(WailaUtils.getCraftingState(te));
            tooltip.add(WailaUtils.getProgress(te));
        }
    }
}
