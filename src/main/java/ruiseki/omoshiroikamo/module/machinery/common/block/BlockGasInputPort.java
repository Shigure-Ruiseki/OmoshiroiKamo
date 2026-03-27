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

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.gas.IGasHandler;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tier.TierManager;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.input.TEGasInputPort;

/**
 * Gas Input Port block with unified 16-tier system.
 * Uses a single TE class (TEGasInputPort) with tier field instead of per-tier TE classes.
 *
 * Legacy TE classes (TEGasInputPortT1-T6) are automatically remapped to TEGasInputPort.
 */
public class BlockGasInputPort extends AbstractPortBlock<TEGasInputPort> {

    private static final int TIER_COUNT = 16;

    protected BlockGasInputPort() {
        // Pass single TE class - we override createTileEntity and registerTileEntity
        super(ModObject.blockModularGasInput.name, TEGasInputPort.class);
        setHardness(5.0F);
        setResistance(10.0F);
        setTextureName("modularmachineryOverlay/base_modularports");
    }

    public static BlockGasInputPort create() {
        return new BlockGasInputPort();
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        // Create unified TE with tier set from metadata
        return new TEGasInputPort(meta);
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
        return "overlay_gasinput_";
    }

    @Override
    protected Class<? extends AbstractPortItemBlock> getItemBlockClass() {
        return ItemBlockGasInputPort.class;
    }

    @Override
    protected void registerTileEntity() {
        // Register with remapping for legacy TE classes
        GameRegistry.registerTileEntityWithAlternatives(
            TEGasInputPort.class,
            TEGasInputPort.class.getSimpleName() + "TileEntity",
            // Legacy TE class names - automatically remap to new class
            "TEGasInputPortT1TileEntity",
            "TEGasInputPortT2TileEntity",
            "TEGasInputPortT3TileEntity",
            "TEGasInputPortT4TileEntity",
            "TEGasInputPortT5TileEntity",
            "TEGasInputPortT6TileEntity");
    }

    @Override
    public void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaInfo(tooltip, itemStack, accessor, config);
        TileEntity tileEntity = accessor.getTileEntity();
        if (tileEntity instanceof IGasHandler handler) {
            tooltip.addAll(WailaUtils.getGasTooltip(handler));
        }
    }

    @Override
    public Type getPortType() {
        return Type.GAS;
    }

    @Override
    public Direction getPortDirection() {
        return Direction.INPUT;
    }

    @Override
    protected void addCapacityTooltip(List<String> list, int tier) {
        list.add(
            LangHelpers.localize(
                "tooltip.machinery.capacity",
                String.format("%,d", MachineryConfig.getGasPortCapacity(tier)) + " mB"));
    }

    public static class ItemBlockGasInputPort extends AbstractPortItemBlock {

        public ItemBlockGasInputPort(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            super.addInformation(stack, player, list, flag);
        }
    }
}
