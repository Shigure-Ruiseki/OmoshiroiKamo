package ruiseki.omoshiroikamo.common.block;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.client.render.block.JsonModelISBRH;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class BlockOK extends Block {

    protected final @Nullable Class<? extends TileEntityOK> teClass;
    protected final String name;

    protected BlockOK(String name) {
        this(name, null, new Material(MapColor.ironColor));
    }

    public BlockOK(String name, Material material) {
        this(name, null, material);
    }

    protected BlockOK(String name, Class<? extends TileEntityOK> teClass) {
        this(name, teClass, new Material(MapColor.ironColor));
    }

    public BlockOK(ModObject modObject) {
        this(modObject, null, Material.iron);
    }

    public BlockOK(ModObject modObject, Material material) {
        this(modObject, null, material);
    }

    public BlockOK(ModObject modObject, Class<? extends TileEntityOK> teClass) {
        this(modObject, teClass, Material.iron);
    }

    public BlockOK(ModObject modObject, Class<? extends TileEntityOK> teClass, Material material) {
        this(modObject.unlocalisedName, teClass, material);
    }

    protected BlockOK(String name, Class<? extends TileEntityOK> teClass, Material mat) {
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
                TileEntityOK te = teClass.newInstance();
                te.init();
                return te;
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
        if (!(getRenderType() == JsonModelISBRH.JSON_ISBRH_ID)) {
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
        return getNBTDrop(world, x, y, z, teClass != null ? getTileEntityEio(world, x, y, z) : null);
    }

    public ItemStack getNBTDrop(World world, int x, int y, int z, @Nullable TileEntityOK te) {
        int meta = te != null ? damageDropped(te.getBlockMetadata()) : world.getBlockMetadata(x, y, z);
        ItemStack itemStack = new ItemStack(this, 1, meta);
        processDrop(world, x, y, z, te, itemStack);
        return itemStack;
    }

    protected void processDrop(World world, int x, int y, int z, @Nullable TileEntityOK te, ItemStack drop) {}

    @SuppressWarnings("null")
    protected @Nullable TileEntityOK getTileEntityEio(IBlockAccess world, int x, int y, int z) {
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
}
