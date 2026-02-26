package ruiseki.omoshiroikamo.module.cows.common.block;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.block.AbstractBlock;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.cows.common.entity.EntityCowsCow;

public class BlockStall extends AbstractBlock<TEStall> {

    protected BlockStall() {
        super(ModObject.blockStall.unlocalisedName, TEStall.class, Material.rock);
        isOpaque = false;
        rotatable = true;
    }

    public static BlockStall create() {
        return new BlockStall();
    }

    @Override
    public int getRenderType() {
        return JSON_ISBRH_ID;
    }

    @Override
    public int damageDropped(int meta) {
        return 0;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TEStall();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TEStall) {
            spawnCow(world, x, y, z);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    private void spawnCow(World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (!(tileEntity instanceof TEStall TEStall)) {
            return;
        }

        if (!TEStall.hasCow()) {
            return;
        }

        EntityCowsCow cow = TEStall.getCow(world);
        cow.setPosition(x + 0.5, y, z + 0.5);
        cow.setType(TEStall.getCowType());
        world.spawnEntityInWorld(cow);
    }

    @Override
    public void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        TileEntity tileEntity = accessor.getTileEntity();
        if (tileEntity instanceof TEStall stall) {
            tooltip.add(WailaUtils.getProgress(stall));

            FluidStack stored = stall.tank.getFluid();
            if (!(stored == null || stored.getFluid() == null)) {
                String fluidName = stored.getFluid()
                    .getLocalizedName(stored);
                int amount = stored.amount;
                tooltip.add(
                    String.format(
                        "%s%s : %s (%d %s)",
                        EnumChatFormatting.GRAY,
                        LibMisc.LANG.localize(LibResources.TOOLTIP + "mob.fluid"),
                        fluidName,
                        amount,
                        LibMisc.LANG.localize("fluid.millibucket")));
            }
        }
    }
}
