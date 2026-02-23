package ruiseki.omoshiroikamo.module.chickens.common.block;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.entity.chicken.DataChicken;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.block.AbstractBlock;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;

public class BlockBreeder extends AbstractBlock<TEBreeder> {
    // TODO: Add specific conditions for breeding

    protected BlockBreeder() {
        super(ModObject.blockBreeder.unlocalisedName, TEBreeder.class, Material.wood);
        this.setTickRandomly(true);
    }

    public static BlockBreeder create() {
        return new BlockBreeder();
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
    public TileEntity createTileEntity(World world, int meta) {
        return new TEBreeder();
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        int currentMeta = world.getBlockMetadata(x, y, z);
        int targetMeta = isActive(world, x, y, z) ? 1 : 0;

        if (currentMeta != targetMeta) {
            world.setBlockMetadataWithNotify(x, y, z, targetMeta, 2);
            world.scheduleBlockUpdate(x, y, z, this, 2);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {}

    @Override
    public void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        TileEntity tileEntity = accessor.getTileEntity();
        if (tileEntity instanceof TEBreeder roost) {
            DataChicken chicken1 = roost.getChickenData(0);
            DataChicken chicken2 = roost.getChickenData(1);
            if (chicken1 != null && chicken2 != null) {
                tooltip.add(chicken1.getDisplaySummary());
                tooltip.add(chicken2.getDisplaySummary());
                tooltip.add(WailaUtils.getProgress(roost));
            }
        }
    }
}
