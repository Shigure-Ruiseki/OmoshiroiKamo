package ruiseki.omoshiroikamo.common.block.furnace;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.enderio.core.common.TileEntityEnder;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractBlock;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractStorageTE;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractTE;

public class BlockFurnace extends AbstractBlock<TEFurnace> {

    @SideOnly(Side.CLIENT)
    private IIcon icon, iconFrontOff, iconFrontOn, iconTop;

    public static BlockFurnace create() {
        return new BlockFurnace();
    }

    protected BlockFurnace() {
        super(ModObject.blockFurnace, TEFurnace.class);
        setStepSound(soundTypeStone);
        setLightOpacity(0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new TEFurnace();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.icon = reg.registerIcon("furnace_side");
        this.iconTop = reg.registerIcon("furnace_top");
        this.iconFrontOff = reg.registerIcon("furnace_front_off");
        this.iconFrontOn = reg.registerIcon("furnace_front_on");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (side == 0 || side == 1) {
            return iconTop;
        }
        if (side == 3) {
            return iconFrontOff;
        }
        return icon;
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        if (side == 1 || side == 0) {
            return iconTop;
        }
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        AbstractTE te = (AbstractTE) tileEntity;
        if (side == te.facing) {
            if (te.isActive()) {
                return iconFrontOn;
            }
            return iconFrontOff;
        }
        return icon;
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

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof AbstractTE te && te.isActive()) {
            return 13;
        }
        return super.getLightValue(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (!(tileEntity instanceof AbstractTE te) || !te.isActive()) {
            return;
        }

        int facing = te.facing;

        double d0 = x + 0.5;
        double d1 = y + rand.nextDouble() * 6.0 / 16.0;
        double d2 = z + 0.5;

        double offset = 0.52;
        double sideOffset = rand.nextDouble() * 0.6 - 0.3;

        switch (facing) {
            case 0: // Down (hiếm khi dùng)
            case 1: // Up
            case 2: // North (-Z)
                world.spawnParticle("smoke", d0 + sideOffset, d1, d2 - offset, 0.0D, 0.0D, 0.0D);
                world.spawnParticle("flame", d0 + sideOffset, d1, d2 - offset, 0.0D, 0.0D, 0.0D);
                break;
            case 3: // South (+Z)
                world.spawnParticle("smoke", d0 + sideOffset, d1, d2 + offset, 0.0D, 0.0D, 0.0D);
                world.spawnParticle("flame", d0 + sideOffset, d1, d2 + offset, 0.0D, 0.0D, 0.0D);
                break;
            case 4: // West (-X)
                world.spawnParticle("smoke", d0 - offset, d1, d2 + sideOffset, 0.0D, 0.0D, 0.0D);
                world.spawnParticle("flame", d0 - offset, d1, d2 + sideOffset, 0.0D, 0.0D, 0.0D);
                break;
            case 5: // East (+X)
                world.spawnParticle("smoke", d0 + offset, d1, d2 + sideOffset, 0.0D, 0.0D, 0.0D);
                world.spawnParticle("flame", d0 + offset, d1, d2 + sideOffset, 0.0D, 0.0D, 0.0D);
                break;
        }
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

}
