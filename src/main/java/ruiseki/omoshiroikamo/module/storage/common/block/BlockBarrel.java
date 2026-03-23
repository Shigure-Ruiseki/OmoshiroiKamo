package ruiseki.omoshiroikamo.module.storage.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;
import com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry;

import net.minecraftforge.common.util.ForgeDirection;
import ruiseki.omoshiroikamo.core.block.BlockOK;
import ruiseki.omoshiroikamo.core.block.property.BlockPropertyReg;
import ruiseki.omoshiroikamo.core.block.property.BlockStateProperties;
import ruiseki.omoshiroikamo.core.block.property.BooleanProperty;
import ruiseki.omoshiroikamo.core.block.property.DirectionProperty;
import ruiseki.omoshiroikamo.core.helper.DirectionHelpers;
import ruiseki.omoshiroikamo.core.item.ItemBlockOK;
import ruiseki.omoshiroikamo.module.storage.common.handler.StorageWrapper;
import ruiseki.omoshiroikamo.module.storage.common.tileentity.TEBarrel;

public class BlockBarrel extends BlockOK {

    @BlockPropertyReg
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    @BlockPropertyReg
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    private final int slots;
    private final int upgradeSlots;

    public BlockBarrel(String name, int slots, int upgradeSlots) {
        super(name, TEBarrel.class, Material.wood);
        setStepSound(soundTypeWood);
        setHardness(1f);
        this.slots = slots;
        this.upgradeSlots = upgradeSlots;
        this.rotatable = true;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, entity, stack);
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TEBarrel barrel) {
            barrel.setOrientation(DirectionHelpers.yawToDirection6(entity).getOpposite(), ForgeDirection.UP);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float subX,
        float subY, float subZ) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TEBarrel barrel) {
            barrel.setOpen(true);
            return barrel.onBlockActivated(world, x, y, z, player, side, subX, subY, subZ);
        }
        return true;
    }

    @Override
    protected Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBarrel.class;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        TEBarrel tile = (TEBarrel) super.createTileEntity(world, metadata);
        StorageWrapper wrapper = new StorageWrapper(slots, upgradeSlots);
        tile.setWrapper(wrapper);
        return tile;
    }

    public static class ItemBarrel extends ItemBlockOK {

        public int slots = 27;
        public int upgradeSlots = 1;

        public ItemBarrel(Block block) {
            super(block);
            if (block instanceof BlockBarrel backpack) {
                this.slots = backpack.slots;
                this.upgradeSlots = backpack.upgradeSlots;
            }
        }

        @Override
        public String getItemStackDisplayName(ItemStack stack) {
            if (stack.hasTagCompound() && stack.getTagCompound()
                .hasKey("display", 10)) {
                return stack.getTagCompound()
                    .getCompoundTag("display")
                    .getString("Name");
            }
            return super.getItemStackDisplayName(stack);
        }
    }
}
