package ruiseki.omoshiroikamo.common.block.deepMobLearning;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.TileEntityOK;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractBlock;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractStorageTE;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractTE;

public class BlockLootFabricator extends AbstractBlock<TELootFabricator> {

    protected BlockLootFabricator() {
        super(ModObject.blockLootFabricator.unlocalisedName, TELootFabricator.class);
    }

    public static BlockLootFabricator create() {
        return new BlockLootFabricator();
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
        return new TELootFabricator();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, player, stack);
        AbstractTE te = (AbstractTE) world.getTileEntity(x, y, z);
        world.setBlockMetadataWithNotify(x, y, z, te.getFacing(), 2);
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
    protected void processDrop(World world, int x, int y, int z, TileEntityOK te, ItemStack stack) {

    }
}
