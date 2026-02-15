package ruiseki.omoshiroikamo.module.ids.common.block.cable;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.block.ICustomCollision;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICable;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.common.block.BlockOK;
import ruiseki.omoshiroikamo.core.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.ids.common.item.part.logic.redstone.IRedstoneLogic;

public class BlockCable extends BlockOK {

    public static int rendererId = -1;

    public BlockCable() {
        super(ModObject.blockCable.unlocalisedName, TECable.class);
        this.setBaseBounds(6f / 16f, 6f / 16f, 6f / 16f, 10f / 16f, 10f / 16f, 10f / 16f);
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, ItemBlockCable.class, name);
        GameRegistry.registerTileEntity(teClass, name + "TileEntity");
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new TECable();
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        blockIcon = reg.registerIcon(LibResources.PREFIX_MOD + "ids/cable");
        IconRegistry.addIcon("energy_input_bus", reg.registerIcon(LibResources.PREFIX_MOD + "ids/energy_input_bus"));
    }

    @Override
    public boolean isNormalCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return false;
    }

    @Override
    public int getRenderType() {
        return rendererId;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof ICable cable) cable.updateConnections();
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof ICable cable) cable.onNeighborBlockChange(block);
    }

    @Override
    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof ICable cable) cable.onNeighborChange(world, x, y, z, tileX, tileY, tileZ);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof ICable cable) cable.onBlockRemoved();
        world.removeTileEntity(x, y, z);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof ICable cable)) return false;
        return cable.onBlockActivated(world, x, y, z, player, ForgeDirection.getOrientation(side), hitX, hitY, hitZ);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TECable cable)) {
            return super.getPickBlock(target, world, x, y, z, player);
        }

        TECable.CableHit hit = cable.rayTraceCable(player);
        if (hit != null && hit.type == TECable.CableHit.Type.PART && hit.part != null) {
            ItemStack stack = hit.part.getItemStack();
            if (stack != null) {
                return stack.copy();
            }
        }

        return new ItemStack(this);
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        if (world.isRemote) {
            return true;
        }

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TECable cable)) {
            return super.removedByPlayer(world, player, x, y, z, willHarvest);
        }

        TECable.CableHit hit = cable.rayTraceCable(player);
        if (hit == null) {
            return super.removedByPlayer(world, player, x, y, z, willHarvest);
        }

        if (hit.type == TECable.CableHit.Type.PART && hit.side != null) {
            ICablePart part = cable.getPart(hit.side);
            if (part != null) {

                ItemStack drop = part.getItemStack();
                if (!player.capabilities.isCreativeMode && drop != null) {
                    TECable.dropStack(world, x, y, z, drop);
                }

                cable.removePart(hit.side);

                boolean shouldBreakBlock = !cable.hasCore() && cable.getParts()
                    .isEmpty();

                if (shouldBreakBlock) {
                    return super.removedByPlayer(world, player, x, y, z, willHarvest);
                }

                return true;
            }
        }

        if (hit.type == TECable.CableHit.Type.CORE) {
            for (ICablePart part : cable.getParts()) {
                if (part != null) {
                    ItemStack drop = part.getItemStack();
                    if (!player.capabilities.isCreativeMode && drop != null) {
                        TECable.dropStack(world, x, y, z, drop);
                    }
                }
            }

            return super.removedByPlayer(world, player, x, y, z, willHarvest);
        }

        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side) {
        if (side < 0) return false;
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TECable cable)) return false;
        ForgeDirection dir = ForgeDirection.getOrientation(side)
            .getOpposite();
        return cable.getPart(dir) instanceof IRedstoneLogic;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TECable cable)) return 0;

        ForgeDirection dir = ForgeDirection.getOrientation(side)
            .getOpposite();
        return cable.getRedstonePower(dir);
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
        return isProvidingWeakPower(world, x, y, z, side);
    }

    @Override
    protected ICustomCollision getCustomCollision(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof ICustomCollision collision) {
            return collision;
        }
        return null;
    }

    public static class ItemBlockCable extends ItemBlockOK {

        public ItemBlockCable(Block block) {
            super(block, block);
        }

        @Override
        public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ) {

            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof ICable cable) {
                if (!cable.hasCore()) {
                    cable.setHasCore(true);
                    if (!player.capabilities.isCreativeMode) {
                        --stack.stackSize;
                    }
                    return true;
                }
            }

            return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
        }
    }

}
