package ruiseki.omoshiroikamo.core.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.block.ICustomCollision;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.common.util.PlayerUtils;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.core.tileentity.TileEntityOK;

public class BlockOK extends Block {

    protected final Class<? extends TileEntityOK> teClass;
    protected final String name;

    protected float baseMinX = 0f;
    protected float baseMinY = 0f;
    protected float baseMinZ = 0f;
    protected float baseMaxX = 1f;
    protected float baseMaxY = 1f;
    protected float baseMaxZ = 1f;

    protected BlockOK(String name) {
        this(name, null, new Material(MapColor.ironColor));
    }

    public BlockOK(String name, Material material) {
        this(name, null, material);
    }

    protected BlockOK(String name, Class<? extends TileEntityOK> teClass) {
        this(name, teClass, new Material(MapColor.ironColor));
    }

    protected BlockOK(String name, @Nullable Class<? extends TileEntityOK> teClass, Material mat) {
        super(mat);
        this.teClass = teClass;
        this.name = name;
        setHardness(0.5F);
        setBlockName(name);
        setStepSound(Block.soundTypeMetal);
        setHarvestLevel("pickaxe", 0);
    }

    public void init() {
        GameRegistry.registerBlock(this, name);
        if (teClass != null) {
            GameRegistry.registerTileEntity(teClass, name + "TileEntity");
        }
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return teClass != null;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        if (teClass != null) {
            try {
                return teClass.newInstance();
            } catch (Exception e) {
                Logger.error("Could not create tile entity for block " + name + " for class " + teClass);
            }
        }
        return null;
    }

    public BlockOK setTextureName(String texture) {
        this.textureName = texture;
        return this;
    }

    @Override
    public String getTextureName() {
        return textureName == null ? name : textureName;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        if (getRenderType() != ModelISBRH.JSON_ISBRH_ID) {
            blockIcon = reg.registerIcon(LibResources.PREFIX_MOD + getTextureName());
        }
    }

    /* Subclass Helpers */
    public boolean doNormalDrops(World world, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        if (willHarvest) {
            return true;
        }
        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
        super.harvestBlock(world, player, x, y, z, meta);
        world.setBlockToAir(x, y, z);
    }

    @Override
    public final ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        if (doNormalDrops(world, x, y, z)) {
            return super.getDrops(world, x, y, z, metadata, fortune);
        }
        return Lists.newArrayList(getNBTDrop(world, x, y, z, (TileEntityOK) world.getTileEntity(x, y, z)));
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return getPickBlock(target, world, x, y, z, null);
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z,
        @Nullable EntityPlayer player) {
        return getNBTDrop(world, x, y, z, teClass != null ? getTileEntityOK(world, x, y, z) : null);
    }

    public ItemStack getNBTDrop(World world, int x, int y, int z, @Nullable TileEntityOK te) {
        int meta = te != null ? damageDropped(te.getBlockMetadata()) : world.getBlockMetadata(x, y, z);
        ItemStack itemStack = new ItemStack(this, 1, meta);
        processDrop(world, x, y, z, te, itemStack);
        return itemStack;
    }

    protected void processDrop(World world, int x, int y, int z, @Nullable TileEntityOK te, ItemStack drop) {}

    @SuppressWarnings("null")
    protected @Nullable TileEntityOK getTileEntityOK(IBlockAccess world, int x, int y, int z) {
        if (teClass != null) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (teClass.isInstance(te)) { // no need to null-check teClass here, it was checked 2 lines above and is
                // *final*
                return (TileEntityOK) te;
            }
        }
        return null;
    }

    // Because the vanilla method takes floats...
    public void setBlockBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public void setBlockBounds(AxisAlignedBB bb) {
        setBlockBounds(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
    }

    protected ICustomCollision getCustomCollision(World w, int x, int y, int z) {
        if (this instanceof ICustomCollision) {
            return (ICustomCollision) this;
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addCollisionBoxesToList(final World w, final int x, final int y, final int z, final AxisAlignedBB bb,
        final List<AxisAlignedBB> out, final Entity e) {
        ICustomCollision collisionHandler = this.getCustomCollision(w, x, y, z);
        if (collisionHandler != null && bb != null) {
            List<AxisAlignedBB> tmp = new ArrayList<>();
            collisionHandler.addCollidingBlockToList(w, x, y, z, bb, tmp, e);
            for (AxisAlignedBB b : tmp) {
                b.minX += x;
                b.minY += y;
                b.minZ += z;
                b.maxX += x;
                b.maxY += y;
                b.maxZ += z;
                if (bb.intersectsWith(b)) {
                    out.add(b);
                }
            }
        } else {
            super.addCollisionBoxesToList(w, x, y, z, bb, out, e);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World w, int x, int y, int z) {
        ICustomCollision collision = getCustomCollision(w, x, y, z);

        if (collision != null) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if (player != null) {
                Vec3 start = PlayerUtils.getEyePosition(player);
                Vec3 look = player.getLookVec();
                Vec3 end = start.addVector(look.xCoord * 5.0, look.yCoord * 5.0, look.zCoord * 5.0);

                AxisAlignedBB closest = null;
                double minDist = Double.MAX_VALUE;

                for (AxisAlignedBB bb : collision.getSelectedBoundingBoxesFromPool(w, x, y, z, player, true)) {

                    // fake block bounds = part
                    this.setBlockBounds(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);

                    MovingObjectPosition mop = super.collisionRayTrace(w, x, y, z, start, end);

                    this.setBlockBounds(baseMinX, baseMinY, baseMinZ, baseMaxX, baseMaxY, baseMaxZ);

                    if (mop != null) {
                        double dist = mop.hitVec.distanceTo(start);
                        if (closest == null || dist < minDist) {
                            minDist = dist;
                            closest = bb;
                        }
                    }
                }

                if (closest != null) {
                    return AxisAlignedBB.getBoundingBox(
                        closest.minX + x,
                        closest.minY + y,
                        closest.minZ + z,
                        closest.maxX + x,
                        closest.maxY + y,
                        closest.maxZ + z);
                }
            }

            AxisAlignedBB merged = AxisAlignedBB.getBoundingBox(16, 16, 16, 0, 0, 0);
            for (AxisAlignedBB bb : collision.getSelectedBoundingBoxesFromPool(w, x, y, z, null, false)) {

                merged.setBounds(
                    Math.min(merged.minX, bb.minX),
                    Math.min(merged.minY, bb.minY),
                    Math.min(merged.minZ, bb.minZ),
                    Math.max(merged.maxX, bb.maxX),
                    Math.max(merged.maxY, bb.maxY),
                    Math.max(merged.maxZ, bb.maxZ));
            }

            return AxisAlignedBB.getBoundingBox(
                merged.minX + x,
                merged.minY + y,
                merged.minZ + z,
                merged.maxX + x,
                merged.maxY + y,
                merged.maxZ + z);
        }

        return super.getSelectedBoundingBoxFromPool(w, x, y, z);
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World w, int x, int y, int z, Vec3 a, Vec3 b) {
        ICustomCollision collisionHandler = this.getCustomCollision(w, x, y, z);

        if (collisionHandler != null) {
            Iterable<AxisAlignedBB> bbs = collisionHandler.getSelectedBoundingBoxesFromPool(w, x, y, z, null, false);
            MovingObjectPosition br = null;

            double lastDist = 0;

            for (AxisAlignedBB bb : bbs) {
                this.setBlockBounds(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);

                MovingObjectPosition r = super.collisionRayTrace(w, x, y, z, a, b);

                this.setBlockBounds(baseMinX, baseMinY, baseMinZ, baseMaxX, baseMaxY, baseMaxZ);

                if (r != null) {
                    double xLen = (a.xCoord - r.hitVec.xCoord);
                    double yLen = (a.yCoord - r.hitVec.yCoord);
                    double zLen = (a.zCoord - r.hitVec.zCoord);

                    double thisDist = xLen * xLen + yLen * yLen + zLen * zLen;
                    if (br == null || lastDist > thisDist) {
                        lastDist = thisDist;
                        br = r;
                    }
                }
            }

            return br;
        }

        this.setBlockBounds(baseMinX, baseMinY, baseMinZ, baseMaxX, baseMaxY, baseMaxZ);
        return super.collisionRayTrace(w, x, y, z, a, b);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return AxisAlignedBB
            .getBoundingBox(x + baseMinX, y + baseMinY, z + baseMinZ, x + baseMaxX, y + baseMaxY, z + baseMaxZ);
    }

    public void setBaseBounds(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.baseMinX = minX;
        this.baseMinY = minY;
        this.baseMinZ = minZ;

        this.baseMaxX = maxX;
        this.baseMaxY = maxY;
        this.baseMaxZ = maxZ;
        this.setBlockBounds(baseMinX, baseMinY, baseMinZ, baseMaxX, baseMaxY, baseMaxZ);
    }
}
