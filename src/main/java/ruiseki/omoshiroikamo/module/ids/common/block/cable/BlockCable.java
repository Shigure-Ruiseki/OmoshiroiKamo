package ruiseki.omoshiroikamo.module.ids.common.block.cable;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Lists;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.experimental.Delegate;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICable;
import ruiseki.omoshiroikamo.core.block.BlockOK;
import ruiseki.omoshiroikamo.core.block.CollidableComponent;
import ruiseki.omoshiroikamo.core.block.ICollidable;
import ruiseki.omoshiroikamo.core.block.ICollidableParent;
import ruiseki.omoshiroikamo.core.client.render.BaseBlockRender;
import ruiseki.omoshiroikamo.core.helper.BlockHelpers;
import ruiseki.omoshiroikamo.core.helper.MinecraftHelpers;
import ruiseki.omoshiroikamo.core.integration.waila.IWailaBlockInfoProvider;
import ruiseki.omoshiroikamo.core.item.ItemBlockOK;
import ruiseki.omoshiroikamo.core.tileentity.TileEntityOK;
import ruiseki.omoshiroikamo.module.ids.client.render.RenderCable;
import ruiseki.omoshiroikamo.module.ids.common.item.part.logic.redstone.IRedstoneLogic;

public class BlockCable extends BlockOK
    implements ICollidable<ForgeDirection>, ICollidableParent, IWailaBlockInfoProvider {

    protected static final float BLOCK_HARDNESS = 3.0F;
    protected static final Material BLOCK_MATERIAL = Material.glass;

    protected static final List<IComponent<ForgeDirection, BlockCable>> COLLIDABLE_COMPONENTS = Lists.newLinkedList();
    protected static final IComponent<ForgeDirection, BlockCable> CENTER_COMPONENT = new CollidableComponentCableCenter();
    protected static final IComponent<ForgeDirection, BlockCable> CONNECTIONS_COMPONENT = new CollidableComponentCableConnections();
    protected static final IComponent<ForgeDirection, BlockCable> PARTS_COMPONENT = new CollidableComponentParts();
    static {
        COLLIDABLE_COMPONENTS.add(PARTS_COMPONENT);
        COLLIDABLE_COMPONENTS.add(CONNECTIONS_COMPONENT);
        COLLIDABLE_COMPONENTS.add(CENTER_COMPONENT);
    }
    @Delegate
    protected final CollidableComponent<ForgeDirection, BlockCable> collision = new CollidableComponent<>(
        this,
        COLLIDABLE_COMPONENTS);

    public BlockCable() {
        super(ModObject.blockCable.unlocalisedName, TECable.class, BLOCK_MATERIAL);
        setHardness(BLOCK_HARDNESS);
        setStepSound(soundTypeStone);
        setBlockTextureName("ids/cable");
    }

    @Override
    protected void registerComponent() {
        super.registerComponent();
        if (MinecraftHelpers.isClientSide()) {
            BlockHelpers.bindTileEntitySpecialRenderer(TECable.class, this);
        }
    }

    @Override
    protected Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockCable.class;
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

    @SideOnly(Side.CLIENT)
    protected BaseBlockRender<? extends BlockOK, ? extends TileEntityOK> getRenderer() {
        return new RenderCable();
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
        RayTraceResult<ForgeDirection> rayTraceResult = doRayTrace(world, x, y, z, player);
        if (rayTraceResult != null && rayTraceResult.getCollisionType() != null) {
            return rayTraceResult.getCollisionType()
                .getPickBlock(world, x, y, z, rayTraceResult.getPositionHit());
        }
        return new ItemStack(getItem(world, x, y, z), 1, getDamageValue(world, x, y, z));
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        if (world.isRemote) return false;
        RayTraceResult<ForgeDirection> result = doRayTrace(world, x, y, z, player);
        if (result != null && result.getCollisionType() != null) {
            return result.getCollisionType()
                .destroy(world, x, y, z, result.getPositionHit(), player);
        }
        return false;
    }

    @Override
    public boolean doNormalDrops(World world, int x, int y, int z) {
        return false;
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
        // DO NOTHING
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
    public void addCollisionBoxesToListParent(World world, int x, int y, int z, AxisAlignedBB mask,
        List<AxisAlignedBB> list, Entity collidingEntity) {
        super.addCollisionBoxesToList(world, x, y, z, mask, list, collidingEntity);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPoolParent(World world, int x, int y, int z) {
        return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public MovingObjectPosition collisionRayTraceParent(World world, int x, int y, int z, Vec3 origin, Vec3 direction) {
        return super.collisionRayTrace(world, x, y, z, origin, direction);
    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        World world = accessor.getWorld();
        EntityPlayer player = accessor.getPlayer();
        int x = accessor.getPosition().blockX;
        int y = accessor.getPosition().blockY;
        int z = accessor.getPosition().blockZ;

        RayTraceResult<ForgeDirection> result = doRayTrace(world, x, y, z, player);
        if (result != null && result.getCollisionType() != null) {
            return result.getCollisionType()
                .getPickBlock(world, x, y, z, result.getPositionHit());
        }
        return null;
    }

    public static class ItemBlockCable extends ItemBlockOK {

        public ItemBlockCable(Block block) {
            super(block);
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
