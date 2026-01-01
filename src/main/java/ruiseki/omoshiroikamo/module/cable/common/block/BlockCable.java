package ruiseki.omoshiroikamo.module.cable.common.block;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.Util;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.conduit.RaytraceResult;
import crazypants.enderio.conduit.geom.CollidableComponent;
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

import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraftforge.common.util.ForgeDirection;
import ruiseki.omoshiroikamo.OmoshiroiKamo;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.common.block.BlockOK;
import ruiseki.omoshiroikamo.core.common.util.PlayerUtils;
import ruiseki.omoshiroikamo.core.lib.LibResources;

import java.util.ArrayList;
import java.util.List;

public class BlockCable extends BlockOK {

    public static int rendererId = -1;

    public BlockCable() {
        super(ModObject.blockCable.unlocalisedName, TECable.class);
        float min = 6f / 16f;
        float max = 10f / 16f;
        setBlockBounds(min, max, min, max, min, max);
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
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collider) {

        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TECable cable)) return;

        float min = 6f / 16f;
        float max = 10f / 16f;

        // Center
        this.setBlockBounds(min, min, min, max, max, max);
        super.addCollisionBoxesToList(world, x, y, z, mask, list, collider);

        // WEST
        if (cable.isConnected(ForgeDirection.WEST)) {
            this.setBlockBounds(0f, min, min, min, max, max);
            super.addCollisionBoxesToList(world, x, y, z, mask, list, collider);
        }

        // EAST
        if (cable.isConnected(ForgeDirection.EAST)) {
            this.setBlockBounds(max, min, min, 1f, max, max);
            super.addCollisionBoxesToList(world, x, y, z, mask, list, collider);
        }

        // DOWN
        if (cable.isConnected(ForgeDirection.DOWN)) {
            this.setBlockBounds(min, 0f, min, max, min, max);
            super.addCollisionBoxesToList(world, x, y, z, mask, list, collider);
        }

        // UP
        if (cable.isConnected(ForgeDirection.UP)) {
            this.setBlockBounds(min, max, min, max, 1f, max);
            super.addCollisionBoxesToList(world, x, y, z, mask, list, collider);
        }

        // NORTH
        if (cable.isConnected(ForgeDirection.NORTH)) {
            this.setBlockBounds(min, min, 0f, max, max, min);
            super.addCollisionBoxesToList(world, x, y, z, mask, list, collider);
        }

        // SOUTH
        if (cable.isConnected(ForgeDirection.SOUTH)) {
            this.setBlockBounds(min, min, max, max, max, 1f);
            super.addCollisionBoxesToList(world, x, y, z, mask, list, collider);
        }

        // reset
        setBlockBounds(0, 0, 0, 1, 1, 1);
    }

}
