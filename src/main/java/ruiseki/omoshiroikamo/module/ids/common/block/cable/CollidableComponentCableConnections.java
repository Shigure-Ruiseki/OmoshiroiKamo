package ruiseki.omoshiroikamo.module.ids.common.block.cable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.core.block.ICollidable;
import ruiseki.omoshiroikamo.module.ids.common.init.IDsBlocks;

public class CollidableComponentCableConnections implements ICollidable.IComponent<ForgeDirection, BlockCable> {

    @Override
    public Collection<ForgeDirection> getPossiblePositions() {
        return Arrays.asList(ForgeDirection.VALID_DIRECTIONS);
    }

    @Override
    public int getBoundsCount(ForgeDirection position) {
        return 1;
    }

    @Override
    public boolean isActive(BlockCable block, World world, int x, int y, int z, ForgeDirection position) {

        TileEntity te = world.getTileEntity(x, y, z);
        return te instanceof TECable cable && cable.isConnected(position);
    }

    @Override
    public List<AxisAlignedBB> getBounds(BlockCable block, World world, int x, int y, int z, ForgeDirection dir) {

        float min = 6f / 16f;
        float max = 10f / 16f;

        AxisAlignedBB box = switch (dir) {
            case DOWN -> AxisAlignedBB.getBoundingBox(min, 0, min, max, min, max);
            case UP -> AxisAlignedBB.getBoundingBox(min, max, min, max, 1, max);
            case NORTH -> AxisAlignedBB.getBoundingBox(min, min, 0, max, max, min);
            case SOUTH -> AxisAlignedBB.getBoundingBox(min, min, max, max, max, 1);
            case WEST -> AxisAlignedBB.getBoundingBox(0, min, min, min, max, max);
            case EAST -> AxisAlignedBB.getBoundingBox(max, min, min, 1, max, max);
            default -> null;
        };

        return box == null ? Collections.emptyList() : Collections.singletonList(box);
    }

    @Override
    public ItemStack getPickBlock(World world, int x, int y, int z, ForgeDirection position) {
        return IDsBlocks.CABLE.newItemStack();
    }

    @Override
    public boolean destroy(World world, int x, int y, int z, ForgeDirection position, EntityPlayer player) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TECable cable)) return false;

        if (!player.capabilities.isCreativeMode) {

            // Drop all parts
            for (ICablePart part : cable.getParts()) {
                if (part != null) {
                    ItemStack stack = part.getItemStack();
                    if (stack != null) {
                        TECable.dropStack(world, x, y, z, stack);
                    }
                }
            }

            // Drop core
            ItemStack core = IDsBlocks.CABLE.newItemStack();
            if (core != null) {
                TECable.dropStack(world, x, y, z, core);
            }
        }

        // Remove block AFTER drop
        world.setBlockToAir(x, y, z);

        return true;
    }
}
