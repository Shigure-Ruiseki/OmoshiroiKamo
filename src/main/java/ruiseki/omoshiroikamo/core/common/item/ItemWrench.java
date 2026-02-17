package ruiseki.omoshiroikamo.core.common.item;

import static ruiseki.omoshiroikamo.CommonProxy.NETWORK;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.item.IToolHammer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.block.ISidedIO;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.module.machinery.common.network.PacketToggleSide;

/**
 * Item Wrench - config ISidedIO.
 * TODO: Add Wrench texture
 * TODO: Add tooltip
 * TODO: Add announcement when change io
 */
public class ItemWrench extends ItemOK implements IToolHammer {

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
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (world.isRemote) return false;

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof ISidedIO io)) return false;

        ForgeDirection clicked = ForgeDirection.getOrientation(side);
        ForgeDirection target = getClickedSide(clicked, hitX, hitY, hitZ);

        NETWORK.sendToServer(new PacketToggleSide(io, target));

        return true;
    }

    public static ForgeDirection getClickedSide(ForgeDirection hitSide, float hitX, float hitY, float hitZ) {
        final float BORDER = 0.20f;

        // Determine horizontal/vertical ranges based on the face
        boolean hLeft = false, hRight = false, vTop = false, vBottom = false;

        switch (hitSide) {
            case UP:
            case DOWN:
                hLeft = hitX < BORDER;
                hRight = hitX > 1 - BORDER;
                vTop = hitZ < BORDER;
                vBottom = hitZ > 1 - BORDER;

                if ((hLeft || hRight) && (vTop || vBottom)) return hitSide.getOpposite();
                if (hLeft) return ForgeDirection.WEST;
                if (hRight) return ForgeDirection.EAST;
                if (vTop) return ForgeDirection.NORTH;
                if (vBottom) return ForgeDirection.SOUTH;
                return hitSide;

            case NORTH:
            case SOUTH:
                hLeft = hitX < BORDER;
                hRight = hitX > 1 - BORDER;
                vTop = hitY > 1 - BORDER;
                vBottom = hitY < BORDER;

                if ((hLeft || hRight) && (vTop || vBottom)) return hitSide.getOpposite();
                if (hLeft) return ForgeDirection.WEST;
                if (hRight) return ForgeDirection.EAST;
                if (vTop) return ForgeDirection.UP;
                if (vBottom) return ForgeDirection.DOWN;
                return hitSide;

            case WEST:
            case EAST:
                hLeft = hitZ > 1 - BORDER;
                hRight = hitZ < BORDER;
                vTop = hitY > 1 - BORDER;
                vBottom = hitY < BORDER;

                if ((hLeft || hRight) && (vTop || vBottom)) return hitSide.getOpposite();
                if (hLeft) return ForgeDirection.SOUTH;
                if (hRight) return ForgeDirection.NORTH;
                if (vTop) return ForgeDirection.UP;
                if (vBottom) return ForgeDirection.DOWN;
                return hitSide;
            default:
                break;
        }

        return hitSide;
    }

    @Override
    public boolean isUsable(ItemStack item, EntityLivingBase user, int x, int y, int z) {
        return true;
    }

    @Override
    public void toolUsed(ItemStack item, EntityLivingBase user, int x, int y, int z) {

    }
}
