package ruiseki.omoshiroikamo.common.block.chicken;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.enderio.core.common.TileEntityEnder;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractBlock;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractStorageTE;
import ruiseki.omoshiroikamo.common.item.chicken.DataChicken;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.plugin.waila.IWailaInfoProvider;

public class BlockBreeder extends AbstractBlock<TEBreeder> implements IWailaInfoProvider {

    @SideOnly(Side.CLIENT)
    IIcon side, up, down;

    protected BlockBreeder() {
        super(ModObject.blockBreeder, TEBreeder.class, Material.wood);
    }

    public static BlockBreeder create() {
        return new BlockBreeder();
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        up = reg.registerIcon(LibResources.PREFIX_MOD + "hay_floor");
        down = reg.registerIcon(LibResources.PREFIX_MOD + "plain_face");
        side = reg.registerIcon(LibResources.PREFIX_MOD + "curtain_side");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        switch (side) {
            case 0:
                return this.up;
            case 1:
                return this.down;
            default:
                return this.side;
        }
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new TEBreeder();
    }

    @Override
    protected void processDrop(World world, int x, int y, int z, TileEntityEnder te, ItemStack stack) {}

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof AbstractStorageTE te) {
            for (int i = 0; i < te.getSizeInventory(); i++) {
                ItemStack stack = te.getStackInSlot(i);
                if (stack != null) {
                    dropStack(world, x, y, z, stack);
                }
            }
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    public static void dropStack(World world, int x, int y, int z, ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) {
            return;
        }

        float dx = world.rand.nextFloat() * 0.8F + 0.1F;
        float dy = world.rand.nextFloat() * 0.8F + 0.1F;
        float dz = world.rand.nextFloat() * 0.8F + 0.1F;

        EntityItem entityItem = new EntityItem(world, x + dx, y + dy, z + dz, stack.copy());

        float motion = 0.05F;
        entityItem.motionX = world.rand.nextGaussian() * motion;
        entityItem.motionY = world.rand.nextGaussian() * motion + 0.2F;
        entityItem.motionZ = world.rand.nextGaussian() * motion;

        world.spawnEntityInWorld(entityItem);
    }

    @Override
    public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TEBreeder roost) {
            DataChicken chicken1 = roost.getChickenData(0);
            DataChicken chicken2 = roost.getChickenData(1);
            if (chicken1 != null && chicken2 != null) {
                tooltip.add(chicken1.getDisplaySummary());
                tooltip.add(chicken2.getDisplaySummary());
                tooltip.add("Progress: " + roost.getFormattedProgress());
            }
        }
    }

    @Override
    public int getDefaultDisplayMask(World world, int x, int y, int z) {
        return 0;
    }
}
