package ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractTieredMBBlock;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.TEQuantumExtractor;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.config.backport.EnvironmentalConfig;
import ruiseki.omoshiroikamo.plugin.waila.IWailaBlockInfoProvider;

public class BlockQuantumResExtractor extends AbstractTieredMBBlock<TEQuantumExtractor>
    implements IWailaBlockInfoProvider {

    protected BlockQuantumResExtractor() {
        super(
            ModObject.blockQuantumResExtractor.unlocalisedName,
            TEQuantumResExtractorT1.class,
            TEQuantumResExtractorT2.class,
            TEQuantumResExtractorT3.class,
            TEQuantumResExtractorT4.class,
            TEQuantumResExtractorT5.class,
            TEQuantumResExtractorT6.class);
        this.setLightLevel(0.5F);
    }

    public static BlockQuantumResExtractor create() {
        return new BlockQuantumResExtractor();
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
        list.add(new ItemStack(this, 1, 2));
        list.add(new ItemStack(this, 1, 3));
        list.add(new ItemStack(this, 1, 4));
        list.add(new ItemStack(this, 1, 5));
    }

    @Override
    public int getRenderType() {
        return JSON_ISBRH_ID;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockQuantumResExtractor.class;
    }

    @Override
    public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TEQuantumExtractor extractor) {
            int progress = (int) extractor.getProgress();
            int duration = extractor.getCurrentProcessDuration();

            if (duration > 0) {
                float percent = Math.max(0f, (progress / (float) duration) * 100f);
                tooltip.add(
                    String.format(
                        "%s: %s%.1f%%%s",
                        EnumChatFormatting.GRAY + LibMisc.LANG.localize("tooltip.progress"),
                        EnumChatFormatting.GRAY,
                        percent,
                        EnumChatFormatting.RESET));
            } else {
                tooltip.add(
                    EnumChatFormatting.GRAY + LibMisc.LANG.localize(
                        "tooltip.progress") + ": " + EnumChatFormatting.GRAY + "N/A" + EnumChatFormatting.RESET);
            }
        }

    }

    @Override
    public int getDefaultDisplayMask(World world, int x, int y, int z) {
        return 0;
    }

    public static class ItemBlockQuantumResExtractor extends ItemBlockOK {

        public ItemBlockQuantumResExtractor(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
            super.addInformation(stack, player, tooltip, advanced);
            int tier = stack.getItemDamage() + 1;
            int tickDuration = getTickDuration(tier);
            int energyCost = getEnergyCost(tier);
            float seconds = tickDuration / 20.0f;
            tooltip.add(StatCollector.translateToLocalFormatted("tooltip.miner.speed", seconds));
            tooltip.add(StatCollector.translateToLocalFormatted("tooltip.miner.energy", energyCost));
        }

        private int getTickDuration(int tier) {
            EnvironmentalConfig.QuantumExtractorConfig cfg = EnvironmentalConfig.quantumExtractorConfig;
            switch (tier) {
                case 1:
                    return cfg.tickResTier1;
                case 2:
                    return cfg.tickResTier2;
                case 3:
                    return cfg.tickResTier3;
                case 4:
                    return cfg.tickResTier4;
                case 5:
                    return cfg.tickResTier5;
                case 6:
                    return cfg.tickResTier6;
                default:
                    return 0;
            }
        }

        private int getEnergyCost(int tier) {
            EnvironmentalConfig.QuantumExtractorConfig cfg = EnvironmentalConfig.quantumExtractorConfig;
            switch (tier) {
                case 1:
                    return cfg.energyCostResTier1;
                case 2:
                    return cfg.energyCostResTier2;
                case 3:
                    return cfg.energyCostResTier3;
                case 4:
                    return cfg.energyCostResTier4;
                case 5:
                    return cfg.energyCostResTier5;
                case 6:
                    return cfg.energyCostResTier6;
                default:
                    return 0;
            }
        }

    }

}
