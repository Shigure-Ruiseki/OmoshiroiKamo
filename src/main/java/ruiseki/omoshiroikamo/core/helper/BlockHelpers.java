package ruiseki.omoshiroikamo.core.helper;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.blockstate.core.BlockProperty;
import com.gtnewhorizon.gtnhlib.blockstate.core.BlockState;
import com.gtnewhorizon.gtnhlib.blockstate.registry.BlockPropertyRegistry;

import ruiseki.omoshiroikamo.core.datastructure.BlockPos;

/**
 * Contains helper methods for various block specific things.
 *
 * @author rubensworks
 */
public final class BlockHelpers {

    private BlockHelpers() {}

    /**
     * Safely get a block state property for a nullable state and value that may not have been set yet.
     *
     * @param state    The block state.
     * @param property The property to get the value for.
     * @param fallback The fallback value when something has failed.
     * @param <T>      The type of value to fetch.
     * @return The value.
     */
    public static <T> T getSafeBlockStateProperty(@Nullable BlockState state, BlockProperty<T> property, T fallback) {

        if (state == null) return fallback;

        T value;
        try {
            value = state.getPropertyValue(property);
        } catch (IllegalArgumentException e) {
            return fallback;
        }

        return value != null ? value : fallback;
    }

    /**
     * Get the blockstate from the given itemstack
     *
     * @param itemStack The itemstack
     * @return The blockstate
     */
    public static BlockState getBlockStateFromItemStack(ItemStack itemStack) {
        return BlockPropertyRegistry.getBlockState(itemStack);
    }

    /**
     * Get the itemstack from the given blockstate
     *
     * @param blockState The blockstate
     * @return The itemstack
     */
    public static ItemStack getItemStackFromBlockState(BlockState blockState) {
        if (blockState == null) return null;

        try {
            return blockState.getItemStack();
        } catch (Exception ignored) {
            Block block = blockState.getBlock();
            Item item = Item.getItemFromBlock(block);
            if (item == null) return null;
            return new ItemStack(item, 1, 0);
        }
    }

    /**
     * Trigger a block update.
     *
     * @param world The world.
     * @param pos   The pos.
     */
    public static void markForUpdate(World world, BlockPos pos) {
        if (world == null || pos == null) return;
        world.markBlockForUpdate(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Add a collision box to the given list if it intersects with a box.
     *
     * @param pos          The block position the collision is happening in.
     * @param collidingBox The box that is colliding with the block, absolute coordinates.
     * @param collisions   The list fo add the box to.
     * @param addingBox    The box to add to the lost, relative coordinates.
     */
    public static void addCollisionBoxToList(BlockPos pos, AxisAlignedBB collidingBox, List<AxisAlignedBB> collisions,
        AxisAlignedBB addingBox) {
        if (addingBox != null) {
            AxisAlignedBB axisalignedbb = addingBox.offset(pos.getX(), pos.getY(), pos.getZ());
            if (collidingBox.intersectsWith(axisalignedbb)) {
                collisions.add(axisalignedbb);
            }
        }
    }

    /**
     * If the given block has a solid top surface.
     *
     * @param world    The world.
     * @param blockPos The block to check the top of.
     * @return If it has a solid top surface.
     */
    public static boolean doesBlockHaveSolidTopSurface(IBlockAccess world, BlockPos blockPos) {
        return BlockPropertyRegistry.getBlockState(world, blockPos.getX(), blockPos.getY() - 1, blockPos.getZ())
            .getBlock()
            .isOpaqueCube();
    }

    /**
     * If the given block can be displayed in the given creative tab.
     *
     * @param block       The block.
     * @param creativeTab The creative tab.
     * @return If it can be displayed.
     */
    public static boolean isValidCreativeTab(Block block, @Nullable CreativeTabs creativeTab) {
        return creativeTab == null || creativeTab == CreativeTabs.tabAllSearch
            || block.getCreativeTabToDisplayOn() == creativeTab;
    }
}
