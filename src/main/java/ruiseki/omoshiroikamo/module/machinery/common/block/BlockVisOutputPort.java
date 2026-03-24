package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tier.TierManager;
import ruiseki.omoshiroikamo.module.machinery.common.tile.vis.output.TEVisOutputPort;

/**
 * Vis Output Port block with unified 16-tier system.
 * Uses a single TE class (TEVisOutputPort) with tier field instead of per-tier TE classes.
 */
public class BlockVisOutputPort extends AbstractPortBlock<TEVisOutputPort> {

    private static final int TIER_COUNT = 16;

    protected BlockVisOutputPort() {
        super(ModObject.blockModularVisOutput.unlocalisedName, TEVisOutputPort.class);
        setHardness(5.0F);
        setResistance(10.0F);
        setTextureName("modularmachineryOverlay/base_modularports");
    }

    public static BlockVisOutputPort create() {
        return new BlockVisOutputPort();
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        // Create unified TE with tier set from metadata
        return new TEVisOutputPort(meta);
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
    public String getOverlayPrefix() {
        return "overlay_visoutput_";
    }

    @Override
    protected Class<? extends AbstractPortItemBlock> getItemBlockClass() {
        return ItemBlockVisOutputPort.class;
    }

    @Override
    public Type getPortType() {
        return Type.VIS;
    }

    @Override
    public Direction getPortDirection() {
        return Direction.OUTPUT;
    }

    @Override
    protected void addCapacityTooltip(List<String> list, int tier) {
        int capacity = MachineryConfig.getVisPortCapacity(tier);
        list.add(
            LibMisc.LANG.localize(
                "tooltip.machinery.capacity",
                String.format("%,d", capacity) + " Vis / " + String.format("%,d", capacity * 10) + " cV"));
    }

    public static class ItemBlockVisOutputPort extends AbstractPortItemBlock {

        public ItemBlockVisOutputPort(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            super.addInformation(stack, player, list, flag);
        }
    }
}
