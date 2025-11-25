package ruiseki.omoshiroikamo.common.block.chicken;

import static ruiseki.omoshiroikamo.api.client.JsonModelISBRH.JSON_ISBRH_ID;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.entity.chicken.DataChicken;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractBlock;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractStorageTE;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class BlockBreeder extends AbstractBlock<TEBreeder> {

    @SideOnly(Side.CLIENT)
    IIcon side, floor, top;

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
    public void registerBlockIcons(IIconRegister reg) {
        floor = reg.registerIcon(LibResources.PREFIX_MOD + "hay_floor");
        top = reg.registerIcon(LibResources.PREFIX_MOD + "plain_face");
        side = reg.registerIcon(LibResources.PREFIX_MOD + "curtain_side");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return this.side;
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
}
