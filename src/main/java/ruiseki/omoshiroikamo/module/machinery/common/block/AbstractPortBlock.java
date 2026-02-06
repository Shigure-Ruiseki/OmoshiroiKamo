package ruiseki.omoshiroikamo.module.machinery.common.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.client.model.color.BlockColor;

import ruiseki.omoshiroikamo.api.block.ISidedIO;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.IModularBlock;
import ruiseki.omoshiroikamo.api.modular.IModularBlockTint;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.common.block.TileEntityOK;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractTE;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractTieredBlock;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.StructureTintCache;

public abstract class AbstractPortBlock<T extends AbstractTE> extends AbstractTieredBlock<T>
    implements IModularBlock, IModularBlockTint {
    // TODO: Accept front I/O by default
    // TODO: Change (configurable) capacity of ports
    // TODO: Change (configurable) transfer rate of ports

    /** Render ID for ISBRH, set during client init */
    public static int portRendererId = -1;

    public static IIcon baseIcon;
    public static IIcon casingIcon;

    @SafeVarargs
    protected AbstractPortBlock(String name, Class<? extends TileEntity>... teClasses) {
        super(name, teClasses);
        this.useNeighborBrightness = true;
    }

    @Override
    public void init() {
        super.init();

        BlockColor.registerBlockColors(new IModularBlockTint() {

            @Override
            public int colorMultiplier(IBlockAccess world, int x, int y, int z, int tintIndex) {
                if (tintIndex == 0) {
                    // Get color from cache
                    Integer structureColor = StructureTintCache.get(world, x, y, z);
                    if (structureColor != null) {
                        return structureColor;
                    }
                    // Fall back to config color
                    return MachineryConfig.getDefaultTintColorInt();
                }
                return 0xFFFFFFFF; // White for non-tinted layers
            }

            @Override
            public int colorMultiplier(ItemStack stack, int tintIndex) {
                if (tintIndex == 0) {
                    // Items always use config color (no structure context)
                    return MachineryConfig.getDefaultTintColorInt();
                }
                return 0xFFFFFFFF; // White for non-tinted layers
            }
        }, this);
    }

    @Override
    public int getRenderType() {
        return portRendererId;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return true;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return baseIcon;
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof ISidedIO io) {
            ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[side];
            if (io.getSideIO(dir) == EnumIO.NONE) {
                if (casingIcon != null) {
                    return casingIcon;
                }
            }
        }
        return baseIcon;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        baseIcon = reg.registerIcon(LibResources.PREFIX_MOD + getTextureName());
        casingIcon = reg.registerIcon(LibResources.PREFIX_MOD + "modular_machine_casing");
        registerPortOverlays(reg);
    }

    public abstract void registerPortOverlays(IIconRegister reg);

    protected abstract Class<? extends AbstractPortItemBlock> getItemBlockClass();

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {}

    @Override
    protected void processDrop(World world, int x, int y, int z, TileEntityOK te, ItemStack stack) {}
}
