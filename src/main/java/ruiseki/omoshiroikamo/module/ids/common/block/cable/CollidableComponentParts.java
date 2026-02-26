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
import ruiseki.omoshiroikamo.core.block.collidable.ICollidable;

public class CollidableComponentParts implements ICollidable.IComponent<ForgeDirection, BlockCable> {

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
        return te instanceof TECable cable && cable.getPart(position) != null;
    }

    @Override
    public List<AxisAlignedBB> getBounds(BlockCable block, World world, int x, int y, int z, ForgeDirection position) {

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TECable cable)) return Collections.emptyList();

        ICablePart part = cable.getPart(position);
        if (part == null) return Collections.emptyList();

        AxisAlignedBB box = part.getCollisionBox();
        return box == null ? Collections.emptyList() : Collections.singletonList(box);
    }

    @Override
    public ItemStack getPickBlock(World world, int x, int y, int z, ForgeDirection position) {

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TECable cable)) return null;

        ICablePart part = cable.getPart(position);
        return part != null ? part.getItemStack() : null;
    }

    @Override
    public boolean destroy(World world, int x, int y, int z, ForgeDirection side, EntityPlayer player) {

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TECable cable)) return false;

        ICablePart part = cable.getPart(side);
        if (part == null) return false;

        if (!player.capabilities.isCreativeMode) {
            ItemStack stack = part.getItemStack();
            if (stack != null) {
                TECable.dropStack(world, x, y, z, stack);
            }
        }

        cable.removePart(side);
        cable.updateConnections();

        // Nếu không còn gì → xoá block
        if (!cable.hasCore() && cable.getParts()
            .isEmpty()) {
            world.setBlockToAir(x, y, z);
        }

        return true;
    }
}
