package ruiseki.omoshiroikamo.module.chickens.common.block;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.entity.chicken.DataChicken;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractBlock;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;

public class BlockRoost extends AbstractBlock<TERoost> {
    // TODO: Add specific or tiered food for chicken

    protected BlockRoost() {
        super(ModObject.blockRoost.unlocalisedName, TERoost.class, Material.wood);
    }

    public static BlockRoost create() {
        return new BlockRoost();
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new TERoost();
    }

    @Override
    public int getRenderType() {
        return JSON_ISBRH_ID;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
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
    public void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        TileEntity tileEntity = accessor.getTileEntity();
        if (tileEntity instanceof TERoost roost) {
            DataChicken chicken1 = roost.getChickenData(0);
            if (chicken1 != null) {
                tooltip.add(chicken1.getDisplaySummary());
                tooltip.add(WailaUtils.getProgress(roost));
            }
        }
    }
}
