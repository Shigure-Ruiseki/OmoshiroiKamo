package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tier.TierManager;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.output.TEFluidOutputPort;

/**
 * Fluid Output Port block with unified 16-tier system.
 * Uses a single TE class (TEFluidOutputPort) with tier field instead of per-tier TE classes.
 *
 * Legacy TE classes (TEFluidOutputPortT1-T6) are automatically remapped to TEFluidOutputPort.
 */
public class BlockFluidOutputPort extends AbstractPortBlock<TEFluidOutputPort> {

    private static final int TIER_COUNT = 16;

    protected BlockFluidOutputPort() {
        // Pass single TE class - we override createTileEntity and registerTileEntity
        super(ModObject.blockModularFluidOutput.unlocalisedName, TEFluidOutputPort.class);
        setHardness(5.0F);
        setResistance(10.0F);
        setTextureName("modularmachineryOverlay/base_modularports");
    }

    public static BlockFluidOutputPort create() {
        return new BlockFluidOutputPort();
    }

    @Override
    protected void registerTileEntity() {
        // Register with remapping for legacy TE classes
        GameRegistry.registerTileEntityWithAlternatives(
            TEFluidOutputPort.class,
            TEFluidOutputPort.class.getSimpleName() + "TileEntity",
            // Legacy TE class names - automatically remap to new class
            "TEFluidOutputPortT1TileEntity",
            "TEFluidOutputPortT2TileEntity",
            "TEFluidOutputPortT3TileEntity",
            "TEFluidOutputPortT4TileEntity",
            "TEFluidOutputPortT5TileEntity",
            "TEFluidOutputPortT6TileEntity");
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        // Create unified TE with tier set from metadata
        return new TEFluidOutputPort(meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        // Show all 16 tiers in creative tab (limited by TierManager)
        int enabledTiers = TierManager.getEnabledTierCount();
        for (int i = 0; i < enabledTiers && i < TIER_COUNT; i++) {
            list.add(new ItemStack(itemIn, 1, i));
        }
    }

    @Override
    public void registerPortOverlays(IIconRegister reg) {
        // Register overlays for all 16 tiers (1-based indexing to match texture files)
        String prefix = getOverlayPrefix();
        for (int i = 1; i <= TIER_COUNT; i++) {
            IconRegistry.addIcon(
                prefix + i,
                reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/" + prefix + i));
        }
        IconRegistry
            .addIcon("overlay_port_disabled", reg.registerIcon(LibResources.PREFIX_MOD + "modular_machine_casing"));
    }

    @Override
    public String getOverlayPrefix() {
        return "overlay_fluidoutput_";
    }

    @Override
    protected Class<? extends AbstractPortItemBlock> getItemBlockClass() {
        return ItemBlockFluidOutputPort.class;
    }

    @Override
    public void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaInfo(tooltip, itemStack, accessor, config);
        TileEntity te = accessor.getTileEntity();
        if (te instanceof IFluidHandler) {
            tooltip.addAll(WailaUtils.getFluidTooltip((IFluidHandler) te));
        }
    }

    @Override
    protected void addCapacityTooltip(List<String> list, int tier) {
        list.add(
            LibMisc.LANG.localize(
                "tooltip.machinery.capacity",
                String.format("%,d", MachineryConfig.getFluidPortCapacity(tier)) + " mB"));
    }

    public static class ItemBlockFluidOutputPort extends AbstractPortItemBlock {

        public ItemBlockFluidOutputPort(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            super.addInformation(stack, player, list, flag);
        }
    }

    @Override
    public Type getPortType() {
        return Type.FLUID;
    }

    @Override
    public Direction getPortDirection() {
        return Direction.OUTPUT;
    }
}
