package ruiseki.omoshiroikamo.module.ids.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.ids.ICable;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.api.ids.ICablePartItem;
import ruiseki.omoshiroikamo.core.common.item.ItemOK;

public abstract class AbstractPartItem extends ItemOK implements ICablePartItem {

    public AbstractPartItem(String name) {
        super(name);
        setMaxStackSize(64);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {

        ForgeDirection dir = ForgeDirection.getOrientation(side);

        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof ICable cable) {
            if (tryPlacePart(cable, dir, player, stack, world)) {
                return true;
            }
        }

        int ox = x + dir.offsetX;
        int oy = y + dir.offsetY;
        int oz = z + dir.offsetZ;

        TileEntity te2 = world.getTileEntity(ox, oy, oz);
        if (te2 instanceof ICable cable2) {
            return tryPlacePart(cable2, dir.getOpposite(), player, stack, world);
        }

        return false;
    }

    private boolean tryPlacePart(ICable cable, ForgeDirection side, EntityPlayer player, ItemStack stack, World world) {

        if (cable.hasPart(side)) return false;

        ICablePart part = createPart();
        cable.setPart(side, part);

        world.playSoundEffect(
            cable.getPos().x + 0.5F,
            cable.getPos().y + 0.5F,
            cable.getPos().z + 0.5F,
            "dig.stone",
            1F,
            1F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);

        if (!player.capabilities.isCreativeMode) {
            stack.stackSize--;
        }

        return true;
    }
}
