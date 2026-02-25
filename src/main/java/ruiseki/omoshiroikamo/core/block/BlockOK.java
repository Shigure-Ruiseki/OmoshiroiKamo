package ruiseki.omoshiroikamo.core.block;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;
import com.gtnewhorizon.gtnhlib.client.model.ModelISBRH;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.experimental.Delegate;
import ruiseki.omoshiroikamo.core.block.property.BlockPropertyProviderComponent;
import ruiseki.omoshiroikamo.core.block.property.IBlockPropertyProvider;
import ruiseki.omoshiroikamo.core.client.render.BaseBlockRender;
import ruiseki.omoshiroikamo.core.client.render.BlockRenderInfo;
import ruiseki.omoshiroikamo.core.client.render.block.WorldRender;
import ruiseki.omoshiroikamo.core.client.texture.FlippableIcon;
import ruiseki.omoshiroikamo.core.client.texture.MissingIcon;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.datastructure.DimPos;
import ruiseki.omoshiroikamo.core.helper.TileHelpers;
import ruiseki.omoshiroikamo.core.item.ItemBlockOK;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.core.tileentity.IOrientable;
import ruiseki.omoshiroikamo.core.tileentity.TileEntityOK;

public class BlockOK extends Block implements IBlockPropertyProvider {

    protected final Class<? extends TileEntityOK> teClass;
    protected final String name;

    @Delegate(types = IBlockPropertyProvider.class)
    private final IBlockPropertyProvider propertyComponent = new BlockPropertyProviderComponent(this);

    @SideOnly(Side.CLIENT)
    private BlockRenderInfo renderInfo;

    protected boolean isOpaque = true;
    protected boolean isFullSize = true;
    public boolean hasSubtypes = false;

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
        setHarvestLevel("pickaxe", 0);
        this.setStepSound(getSoundForMaterial(mat));
    }

    public void init() {
        registerBlock();
        registerTileEntity();
        registerBlockColor();
        registerComponent();
    }

    protected void registerBlock() {
        GameRegistry.registerBlock(this, getItemBlockClass(), name);
    }

    protected Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockOK.class;
    }

    protected void registerTileEntity() {
        if (teClass != null) {
            GameRegistry.registerTileEntity(teClass, name + "TileEntity");
        }
    }

    protected void registerBlockColor() {}

    protected void registerComponent() {
        registerProperties();
    }

    public void registerNoIcons() {
        final BlockRenderInfo info = this.getRendererInstance();
        final FlippableIcon i = new FlippableIcon(new MissingIcon(this));
        info.updateIcons(i, i, i, i, i, i);
    }

    @SideOnly(Side.CLIENT)
    public BlockRenderInfo getRendererInstance() {
        if (this.renderInfo != null) {
            return this.renderInfo;
        }

        final BaseBlockRender<? extends BlockOK, ? extends TileEntityOK> renderer = this.getRenderer();
        this.renderInfo = new BlockRenderInfo(renderer);

        return this.renderInfo;
    }

    @SideOnly(Side.CLIENT)
    protected BaseBlockRender<? extends BlockOK, ? extends TileEntityOK> getRenderer() {
        return new BaseBlockRender<>();
    }

    public boolean hasSubtypes() {
        return this.hasSubtypes;
    }

    @SideOnly(Side.CLIENT)
    public void setRenderStateByMeta(final int itemDamage) {}

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType() {
        return WorldRender.INSTANCE.getRenderId();
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

    @Override
    public final boolean isOpaqueCube() {
        return this.isOpaque;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return this.isFullSize && this.isOpaque;
    }

    @Override
    public final boolean isNormalCube(final IBlockAccess world, final int x, final int y, final int z) {
        return this.isFullSize;
    }

    public SoundType getSoundForMaterial(Material mat) {
        if (mat == Material.glass) return Block.soundTypeGlass;
        if (mat == Material.rock) return Block.soundTypeStone;
        if (mat == Material.wood) return Block.soundTypeWood;
        return Block.soundTypeMetal;
    }

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
        return Lists
            .newArrayList(getNBTDrop(world, x, y, z, TileHelpers.getSafeTile(DimPos.of(world, x, y, z), teClass)));
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z,
        @Nullable EntityPlayer player) {
        return getNBTDrop(
            world,
            x,
            y,
            z,
            teClass != null ? TileHelpers.getSafeTile(DimPos.of(world, x, y, z), teClass) : null);
    }

    public ItemStack getNBTDrop(World world, int x, int y, int z, @Nullable TileEntityOK te) {
        int meta = te != null ? damageDropped(te.getBlockMetadata()) : world.getBlockMetadata(x, y, z);
        ItemStack itemStack = new ItemStack(this, 1, meta);
        processDrop(world, x, y, z, te, itemStack);
        return itemStack;
    }

    protected void processDrop(World world, int x, int y, int z, @Nullable TileEntityOK te, ItemStack drop) {}

    // Because the vanilla method takes floats...
    public void setBlockBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    // Orientable
    public IOrientable getOrientable(final IBlockAccess world, final int x, final int y, final int z) {
        if (this instanceof IOrientableBlock) {
            return ((IOrientableBlock) this).getOrientable(world, x, y, z);
        }
        return TileHelpers.getSafeTile(world, x, y, z, IOrientable.class);
    }
}
