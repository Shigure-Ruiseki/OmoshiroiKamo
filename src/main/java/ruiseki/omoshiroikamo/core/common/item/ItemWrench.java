package ruiseki.omoshiroikamo.core.common.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.block.ISidedIO;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.common.network.PacketHandler;
import ruiseki.omoshiroikamo.module.machinery.common.network.PacketToggleSide;

/**
 * Item Wrench - config ISidedIO.
 * TODO List:
 * - Add Wrench texture
 * - Add tooltip
 * - Add announcement when change io
 */
public class ItemWrench extends ItemOK {

    public ItemWrench() {
        super(ModObject.itemWrench.unlocalisedName);
        setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        this.itemIcon = Items.stick.getIconFromDamage(0);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof ISidedIO io)) return false;

        ForgeDirection clicked = ForgeDirection.getOrientation(side);
        ForgeDirection target = getClickedSide(clicked, hitX, hitY, hitZ);

        PacketHandler.INSTANCE.sendToServer(new PacketToggleSide(io, target));

        return true;
    }

    public static ForgeDirection getClickedSide(ForgeDirection hitSide, float hitX, float hitY, float hitZ) {
        final float BORDER = 0.15f;

        switch (hitSide) {
            case UP:
            case DOWN:
                if (hitX < BORDER) return ForgeDirection.WEST;
                if (hitX > 1 - BORDER) return ForgeDirection.EAST;
                if (hitZ < BORDER) return ForgeDirection.NORTH;
                if (hitZ > 1 - BORDER) return ForgeDirection.SOUTH;
                return hitSide;

            case NORTH:
            case SOUTH:
                if (hitX < BORDER) return ForgeDirection.WEST;
                if (hitX > 1 - BORDER) return ForgeDirection.EAST;
                if (hitY < BORDER) return ForgeDirection.DOWN;
                if (hitY > 1 - BORDER) return ForgeDirection.UP;
                return hitSide;

            case WEST:
            case EAST:
                if (hitZ < BORDER) return ForgeDirection.NORTH;
                if (hitZ > 1 - BORDER) return ForgeDirection.SOUTH;
                if (hitY < BORDER) return ForgeDirection.DOWN;
                if (hitY > 1 - BORDER) return ForgeDirection.UP;
                return hitSide;
        }

        return hitSide;
    }

}
