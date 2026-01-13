package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.ore;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.multiblock.QuantumExtractorConfig;
import ruiseki.omoshiroikamo.core.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractTieredMBBlock;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor.TEQuantumExtractor;

public class BlockQuantumOreExtractor extends AbstractTieredMBBlock<TEQuantumExtractor> {

    protected BlockQuantumOreExtractor() {
        super(
            ModObject.blockQuantumOreExtractor.unlocalisedName,
            TEQuantumOreExtractorT1.class,
            TEQuantumOreExtractorT2.class,
            TEQuantumOreExtractorT3.class,
            TEQuantumOreExtractorT4.class,
            TEQuantumOreExtractorT5.class,
            TEQuantumOreExtractorT6.class);
        this.setLightLevel(0.8F);
    }

    public static BlockQuantumOreExtractor create() {
        return new BlockQuantumOreExtractor();
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
        return ItemBlockQuantumOreExtractor.class;
    }

    @Override
    public void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        TileEntity tile = accessor.getTileEntity();
        if (tile instanceof TEQuantumExtractor te) {
            tooltip.add(WailaUtils.getCraftingState(te));
            tooltip.add(WailaUtils.getProgress(te));
        }
    }

    public static class ItemBlockQuantumOreExtractor extends ItemBlockOK {

        public ItemBlockQuantumOreExtractor(Block block) {
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
            tooltip.add(LibMisc.LANG.localize("tooltip.miner.speed", seconds));
            tooltip.add(LibMisc.LANG.localize("tooltip.miner.energy", energyCost));
        }

        private int getTickDuration(int tier) {
            switch (tier) {
                case 1:
                    return QuantumExtractorConfig.oreMiner.getTick(1);
                case 2:
                    return QuantumExtractorConfig.oreMiner.getTick(2);
                case 3:
                    return QuantumExtractorConfig.oreMiner.getTick(3);
                case 4:
                    return QuantumExtractorConfig.oreMiner.getTick(4);
                case 5:
                    return QuantumExtractorConfig.oreMiner.getTick(5);
                case 6:
                    return QuantumExtractorConfig.oreMiner.getTick(6);
                default:
                    return 0;
            }
        }

        private int getEnergyCost(int tier) {
            switch (tier) {
                case 1:
                    return QuantumExtractorConfig.oreMiner.getEnergyCost(1);
                case 2:
                    return QuantumExtractorConfig.oreMiner.getEnergyCost(2);
                case 3:
                    return QuantumExtractorConfig.oreMiner.getEnergyCost(3);
                case 4:
                    return QuantumExtractorConfig.oreMiner.getEnergyCost(4);
                case 5:
                    return QuantumExtractorConfig.oreMiner.getEnergyCost(5);
                case 6:
                    return QuantumExtractorConfig.oreMiner.getEnergyCost(6);
                default:
                    return 0;
            }
        }

    }

}
