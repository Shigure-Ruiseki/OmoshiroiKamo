package ruiseki.omoshiroikamo.module.dml.common.block.lootFabricator;

import static com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry.registerProperty;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.block.AbstractBlock;
import ruiseki.omoshiroikamo.core.util.WailaUtils;

public class BlockLootFabricator extends AbstractBlock<TELootFabricator> {

    protected BlockLootFabricator() {
        super(ModObject.LOOT_FABRICATOR.name, TELootFabricator.class);
        isOpaque = false;
    }

    public static BlockLootFabricator create() {
        return new BlockLootFabricator();
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
        if (tile instanceof TELootFabricator te) {
            tooltip.add(WailaUtils.getCraftingState(te));
            tooltip.add(WailaUtils.getProgress(te));
        }
    }
}
