package ruiseki.omoshiroikamo.module.dml.common.block.lootFabricator;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import com.gtnewhorizon.gtnhlib.blockstate.properties.IntegerBlockProperty;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.block.AbstractBlock;
import ruiseki.omoshiroikamo.core.block.property.BlockPropertyReg;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;

public class BlockLootFabricator extends AbstractBlock<TELootFabricator> {

    @BlockPropertyReg
    public static final IntegerBlockProperty CRAFTING_STATE = IntegerBlockProperty.meta("craftingState", 0b1100, 2);

    protected BlockLootFabricator() {
        super(ModObject.blockLootFabricator.unlocalisedName, TELootFabricator.class);
        isOpaque = false;
    }

    public static BlockLootFabricator create() {
        return new BlockLootFabricator();
    }

    @Override
    public int damageDropped(int meta) {
        return 0;
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
