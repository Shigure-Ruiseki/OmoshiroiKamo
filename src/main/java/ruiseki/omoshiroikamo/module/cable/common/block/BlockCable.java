package ruiseki.omoshiroikamo.module.cable.common.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.common.block.BlockOK;
import ruiseki.omoshiroikamo.core.common.util.PlayerUtils;
import ruiseki.omoshiroikamo.core.lib.LibResources;

public class BlockCable extends BlockOK {

    public static int rendererId = -1;

    public BlockCable() {
        super(ModObject.blockCable.unlocalisedName, TECable.class);
        float min = 6f / 16f;
        float max = 10f / 16f;
        setBlockBounds(min, min, min, max, max, max);
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, name);
        GameRegistry.registerTileEntity(teClass, name + "TileEntity");
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new TECable();
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        blockIcon = reg.registerIcon(LibResources.PREFIX_MOD + "cable/cable");
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
    public int getRenderType() {
        return rendererId;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        super.onBlockPlacedBy(world, x, y, z, placer, itemIn);
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TECable cable) {
            cable.updateConnections();
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        super.onNeighborBlockChange(world, x, y, z, block);
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TECable cable) {
            cable.updateConnections();
        }
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TECable cable) {
            cable.destroy();
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List<AxisAlignedBB> list,
        Entity collider) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TECable cable)) return;

        for (AxisAlignedBB part : getCableParts(cable)) {
            AxisAlignedBB worldBox = part.getOffsetBoundingBox(x, y, z);
            if (mask.intersectsWith(worldBox)) {
                list.add(worldBox);
            }
        }
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 startVec, Vec3 endVec) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TECable cable)) return super.collisionRayTrace(world, x, y, z, startVec, endVec);

        MovingObjectPosition closestHit = null;
        double minDistance = Double.MAX_VALUE;

        for (AxisAlignedBB part : getCableParts(cable)) {
            AxisAlignedBB worldBox = part.getOffsetBoundingBox(x, y, z);
            MovingObjectPosition mop = worldBox.calculateIntercept(startVec, endVec);

            if (mop != null) {
                double dist = startVec.distanceTo(mop.hitVec);
                if (dist < minDistance) {
                    minDistance = dist;
                    closestHit = new MovingObjectPosition(x, y, z, mop.sideHit, mop.hitVec);
                }
            }
        }

        return closestHit != null ? closestHit : super.collisionRayTrace(world, x, y, z, startVec, endVec);
    }

    private List<AxisAlignedBB> getCableParts(TECable cable) {
        List<AxisAlignedBB> parts = new ArrayList<>();
        float min = 6f / 16f;
        float max = 10f / 16f;

        parts.add(AxisAlignedBB.getBoundingBox(min, min, min, max, max, max));

        if (cable.isConnected(ForgeDirection.WEST))
            parts.add(AxisAlignedBB.getBoundingBox(0f, min, min, min, max, max));
        if (cable.isConnected(ForgeDirection.EAST))
            parts.add(AxisAlignedBB.getBoundingBox(max, min, min, 1f, max, max));
        if (cable.isConnected(ForgeDirection.DOWN))
            parts.add(AxisAlignedBB.getBoundingBox(min, 0f, min, max, min, max));
        if (cable.isConnected(ForgeDirection.UP)) parts.add(AxisAlignedBB.getBoundingBox(min, max, min, max, 1f, max));
        if (cable.isConnected(ForgeDirection.NORTH))
            parts.add(AxisAlignedBB.getBoundingBox(min, min, 0f, max, max, min));
        if (cable.isConnected(ForgeDirection.SOUTH))
            parts.add(AxisAlignedBB.getBoundingBox(min, min, max, max, max, 1f));

        return parts;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TECable cable)) return super.getSelectedBoundingBoxFromPool(world, x, y, z);

        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (player == null) return super.getSelectedBoundingBoxFromPool(world, x, y, z);

        Vec3 start = PlayerUtils.getEyePosition(player);
        Vec3 look = player.getLookVec();
        Vec3 end = start.addVector(look.xCoord * 5, look.yCoord * 5, look.zCoord * 5);

        AxisAlignedBB closestBox = null;
        double minDistance = Double.MAX_VALUE;

        for (AxisAlignedBB part : getCableParts(cable)) {
            AxisAlignedBB worldBox = part.getOffsetBoundingBox(x, y, z);
            MovingObjectPosition mop = worldBox.calculateIntercept(start, end);

            if (mop != null) {
                double dist = start.distanceTo(mop.hitVec);
                if (dist < minDistance) {
                    minDistance = dist;
                    closestBox = worldBox;
                }
            }
        }

        return closestBox != null ? closestBox : super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }
}
