package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.gtnewhorizon.gtnhlib.client.model.color.BlockColor;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.IModularBlock;
import ruiseki.omoshiroikamo.api.modular.IModularBlockTint;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.ModularTier;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.block.AbstractTieredBlock;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.core.item.ItemWrench;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.core.tileentity.AbstractTE;
import ruiseki.omoshiroikamo.core.tileentity.ISidedIO;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.StructureTintCache;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.AbstractEnergyIOPortTE;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.AbstractFluidPortTE;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.AbstractGasPortTE;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.AbstractItemIOPortTE;

public abstract class AbstractPortBlock<T extends AbstractTE> extends AbstractTieredBlock<T>
    implements IModularBlock, IModularBlockTint {

    // Render ID for ISBRH, set during client init
    public static int portRendererId = -1;

    public IIcon[] baseIcons; // Tier-based base textures
    public IIcon[] casingIcons; // Tier-based casing textures for disabled sides

    protected final int tierCount;

    @SafeVarargs
    protected AbstractPortBlock(String name, Class<? extends TileEntity>... teClasses) {
        super(name, teClasses);
        // Use actual tier count from ModularTier (16 tiers: 0-15)
        this.tierCount = ModularTier.getTierCount();
        this.baseIcons = new IIcon[tierCount];
        this.casingIcons = new IIcon[tierCount];
        this.useNeighborBrightness = true;
        isFullSize = isOpaque = false;
    }

    @Override
    protected void registerBlockColor() {
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
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return false;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        // Return tier-based base texture
        if (baseIcons != null && meta >= 0 && meta < baseIcons.length && baseIcons[meta] != null) {
            return baseIcons[meta];
        }
        // Fallback to tier 0
        if (baseIcons != null && baseIcons.length > 0 && baseIcons[0] != null) {
            return baseIcons[0];
        }
        return null;
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        int tier = 0;

        // Get tier from TileEntity
        if (te instanceof IModularPort port) {
            tier = port.getTier();
        }

        // Check if side should show casing (IO disabled)
        if (te instanceof ISidedIO io) {
            ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[side];
            if (io.getSideIO(dir) == EnumIO.NONE) {
                // Use tier-based casing texture
                if (tier >= 0 && tier < casingIcons.length && casingIcons[tier] != null) {
                    return casingIcons[tier];
                }
                // Fallback to tier 0 casing
                if (casingIcons.length > 0 && casingIcons[0] != null) {
                    return casingIcons[0];
                }
            }
        }

        // Return tier-based base texture
        if (baseIcons != null && tier >= 0 && tier < baseIcons.length && baseIcons[tier] != null) {
            return baseIcons[tier];
        }
        // Fallback to tier 0
        if (baseIcons != null && baseIcons.length > 0 && baseIcons[0] != null) {
            return baseIcons[0];
        }
        return null;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        // Use the same tier textures as Casing blocks
        for (int i = 0; i < tierCount; i++) {
            // Format: tier_0_base, tier_1_base, etc. (same as BlockMachineCasing)
            baseIcons[i] = reg.registerIcon(LibResources.PREFIX_MOD + "modular/tier_" + i + "_base");
            // Casing icons also tier-based
            casingIcons[i] = reg.registerIcon(LibResources.PREFIX_MOD + "modular/tier_" + i + "_base");
        }
        registerPortOverlays(reg);
    }

    public void registerPortOverlays(IIconRegister reg) {
        String prefix = getOverlayPrefix();
        for (int i = 0; i < tierCount; i++) {
            int tier = i + 1;
            IconRegistry.addIcon(
                prefix + tier,
                reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/" + prefix + tier));
        }
        IconRegistry
            .addIcon("overlay_port_disabled", reg.registerIcon(LibResources.PREFIX_MOD + "modular_machine_casing"));
    }

    public abstract String getOverlayPrefix();

    protected abstract Class<? extends AbstractPortItemBlock> getItemBlockClass();

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        ForgeDirection facing = ForgeDirection.NORTH;

        if (Math.abs(player.rotationPitch) > 50) {
            facing = player.rotationPitch > 0 ? ForgeDirection.DOWN : ForgeDirection.UP;
        } else {
            int rotation = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
            switch (rotation) {
                case 0 -> facing = ForgeDirection.SOUTH;
                case 1 -> facing = ForgeDirection.WEST;
                case 2 -> facing = ForgeDirection.NORTH;
                case 3 -> facing = ForgeDirection.EAST;
            }
        }

        // Set block metadata to match tier from ItemStack
        int tier = stack.getItemDamage();
        world.setBlockMetadataWithNotify(x, y, z, tier, 2);

        TileEntity te = world.getTileEntity(x, y, z);

        if (te instanceof IModularPort port) {
            // Set tier on TileEntity
            port.setTier(tier);

            // Determine IO limit based on type
            EnumIO ioLimit = EnumIO.NONE;
            if (te instanceof AbstractFluidPortTE portTE) ioLimit = portTE.getIOLimit();
            else if (te instanceof AbstractEnergyIOPortTE portTE) ioLimit = portTE.getIOLimit();
            else if (te instanceof AbstractItemIOPortTE portTE) ioLimit = portTE.getIOLimit();
            else if (te instanceof AbstractGasPortTE portTE) ioLimit = portTE.getIOLimit();

            if (ioLimit != EnumIO.NONE) {
                // Reset all to NONE first
                for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
                    port.setSideIO(dir, EnumIO.NONE);
                }
                // Set facing side
                port.setSideIO(facing.getOpposite(), ioLimit);
            }
        }
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < tierCount; i++) {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    @Override
    public void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        TileEntity te = accessor.getTileEntity();
        if (te instanceof ISidedIO io) {
            Vec3 hit = WailaUtils.getLocalHit(accessor);
            if (hit == null) return;
            ForgeDirection side = ItemWrench
                .getClickedSide(accessor.getSide(), (float) hit.xCoord, (float) hit.yCoord, (float) hit.zCoord);
            tooltip.add(WailaUtils.getSideIOTooltip(io, side));
        }
    }

    public void addTooltip(List<String> list, int tier) {
        addCapacityTooltip(list, tier);
        addTransferTooltip(list, tier);
    }

    protected void addCapacityTooltip(List<String> list, int tier) {}

    protected void addTransferTooltip(List<String> list, int tier) {}

    /**
     * Get base icon for the specified tier.
     * Provides safe access to baseIcons array with fallback.
     */
    public IIcon getBaseIcon(int tier) {
        if (baseIcons != null && tier >= 0 && tier < baseIcons.length && baseIcons[tier] != null) {
            return baseIcons[tier];
        }
        // Fallback to tier 0 if available
        if (baseIcons != null && baseIcons.length > 0 && baseIcons[0] != null) {
            return baseIcons[0];
        }
        return null;
    }
}
