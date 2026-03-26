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
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tier.TierManager;
import ruiseki.omoshiroikamo.module.machinery.common.tile.essentia.output.TEEssentiaOutputPort;

/**
 * Essentia Output Port block with unified 16-tier system.
 * Uses a single TE class (TEEssentiaOutputPort) with tier field instead of per-tier TE classes.
 */
public class BlockEssentiaOutputPort extends AbstractPortBlock<TEEssentiaOutputPort> {

    private static final int TIER_COUNT = 16;

    protected BlockEssentiaOutputPort() {
        super(ModObject.blockModularEssentiaOutput.name, TEEssentiaOutputPort.class);
        setHardness(5.0F);
        setResistance(10.0F);
        setTextureName("modularmachineryOverlay/base_modularports");
    }

    @Override
    public boolean canRenderInPass(int pass) {
        super.canRenderInPass(pass); // keep render-pass thread local in sync
        return pass == 0; // suppress block render pass 1; TESR handles overlay
    }

    public static BlockEssentiaOutputPort create() {
        return new BlockEssentiaOutputPort();
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        // Create unified TE with tier set from metadata
        return new TEEssentiaOutputPort(meta);
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
        return "overlay_essentiaoutput_";
    }

    @Override
    protected Class<? extends AbstractPortItemBlock> getItemBlockClass() {
        return ItemBlockEssentiaOutputPort.class;
    }

    @Override
    public Type getPortType() {
        return Type.ESSENTIA;
    }

    @Override
    public Direction getPortDirection() {
        return Direction.OUTPUT;
    }

    @Override
    protected void addCapacityTooltip(List<String> list, int tier) {
        list.add(
            LangHelpers.localize(
                "tooltip.machinery.capacity",
                String.format("%,d", MachineryConfig.getEssentiaPortCapacity(tier)) + " Essentia / type"));
    }

    public static class ItemBlockEssentiaOutputPort extends AbstractPortItemBlock {

        public ItemBlockEssentiaOutputPort(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            super.addInformation(stack, player, list, flag);
        }
    }
}
