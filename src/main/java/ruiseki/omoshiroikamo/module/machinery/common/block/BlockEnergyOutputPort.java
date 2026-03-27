package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.core.item.ItemWrench;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.core.tileentity.AbstractEnergyTE;
import ruiseki.omoshiroikamo.core.tileentity.ISidedIO;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tier.TierManager;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.output.TEEnergyOutputPort;

/**
 * Energy Output Port block with unified 16-tier system.
 * Uses a single TE class (TEEnergyOutputPort) with tier field instead of per-tier TE classes.
 *
 * Legacy TE classes (TEEnergyOutputPortT1-T6) are automatically remapped to TEEnergyOutputPort.
 */
// TODO: Add wireless energy output
public class BlockEnergyOutputPort extends AbstractPortBlock<TEEnergyOutputPort> {

    private static final int TIER_COUNT = 16;

    protected BlockEnergyOutputPort() {
        // Pass single TE class - we override createTileEntity and registerTileEntity
        super(ModObject.blockModularEnergyOutput.name, TEEnergyOutputPort.class);
        setHardness(5.0F);
        setResistance(10.0F);
        setTextureName("modularmachineryOverlay/base_modularports");
    }

    public static BlockEnergyOutputPort create() {
        return new BlockEnergyOutputPort();
    }

    @Override
    protected void registerTileEntity() {
        // Register with remapping for legacy TE classes
        GameRegistry.registerTileEntityWithAlternatives(
            TEEnergyOutputPort.class,
            TEEnergyOutputPort.class.getSimpleName() + "TileEntity",
            // Legacy TE class names - automatically remap to new class
            "TEEnergyOutputPortT1TileEntity",
            "TEEnergyOutputPortT2TileEntity",
            "TEEnergyOutputPortT3TileEntity",
            "TEEnergyOutputPortT4TileEntity",
            "TEEnergyOutputPortT5TileEntity",
            "TEEnergyOutputPortT6TileEntity");
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        // Create unified TE with tier set from metadata
        return new TEEnergyOutputPort(meta);
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
    protected Class<? extends AbstractPortItemBlock> getItemBlockClass() {
        return ItemBlockEnergyOutputPort.class;
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
    public void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        TileEntity te = accessor.getTileEntity();
        if (te instanceof AbstractEnergyTE energyTE) {
            tooltip.add(WailaUtils.getEnergyTransfer(energyTE));
        }
        if (te instanceof ISidedIO io) {
            Vec3 hit = WailaUtils.getLocalHit(accessor);
            if (hit == null) return;
            ForgeDirection side = ItemWrench
                .getClickedSide(accessor.getSide(), (float) hit.xCoord, (float) hit.yCoord, (float) hit.zCoord);
            tooltip.add(WailaUtils.getSideIOTooltip(io, side));
        }
    }

    @Override
    public String getOverlayPrefix() {
        return "overlay_energyoutput_";
    }

    @Override
    protected void addCapacityTooltip(List<String> list, int tier) {
        list.add(
            LangHelpers.localize(
                "tooltip.machinery.capacity",
                String.format("%,d", MachineryConfig.getEnergyPortCapacity(tier)) + " RF"));
    }

    @Override
    protected void addTransferTooltip(List<String> list, int tier) {
        list.add(
            LangHelpers
                .localize("gui.energy_transfer", String.format("%,d", MachineryConfig.getEnergyPortTransfer(tier))));
    }

    public static class ItemBlockEnergyOutputPort extends AbstractPortItemBlock {

        public ItemBlockEnergyOutputPort(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            super.addInformation(stack, player, list, flag);
        }
    }

    @Override
    public Type getPortType() {
        return Type.ENERGY;
    }

    @Override
    public Direction getPortDirection() {
        return Direction.OUTPUT;
    }
}
