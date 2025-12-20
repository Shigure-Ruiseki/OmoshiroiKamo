package ruiseki.omoshiroikamo.common.block.multiblock.solarArray;

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
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.config.backport.EnvironmentalConfig;
import ruiseki.omoshiroikamo.plugin.waila.IWailaBlockInfoProvider;

public class BlockSolarArray extends AbstractTieredMBBlock<TESolarArray> implements IWailaBlockInfoProvider {

    protected BlockSolarArray() {
        super(
            ModObject.blockSolarArray.unlocalisedName,
            TESolarArrayT1.class,
            TESolarArrayT2.class,
            TESolarArrayT3.class,
            TESolarArrayT4.class,
            TESolarArrayT5.class,
            TESolarArrayT6.class);
    }

    public static BlockSolarArray create() {
        return new BlockSolarArray();
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
        return ItemBlockSolarArray.class;
    }

    @Override
    public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TESolarArray solar) {
            float efficiency = solar.calculateLightRatio();
            if (!solar.canSeeSun()) {
                tooltip.add(EnumChatFormatting.RED + LibMisc.LANG.localize("tooltip.sunlightBlocked"));
            } else {
                tooltip.add(
                    String.format(
                        "%s : %s%.0f%%",
                        EnumChatFormatting.GRAY + LibMisc.LANG.localize("tooltip.efficiency")
                            + EnumChatFormatting.RESET,
                        EnumChatFormatting.GRAY,
                        efficiency * 100));
            }
        }
    }

    @Override
    public int getDefaultDisplayMask(World world, int x, int y, int z) {
        return 0;
    }

    public static class ItemBlockSolarArray extends ItemBlockOK {

        public ItemBlockSolarArray(Block block) {
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
            int peakEnergy = getPeakEnergy(tier);
            tooltip.add(StatCollector.translateToLocalFormatted("tooltip.solar.array.peak", peakEnergy));
        }

        private int getPeakEnergy(int tier) {
            switch (tier) {
                case 1:
                    return EnvironmentalConfig.solarArrayConfig.peakEnergyTier1;
                case 2:
                    return EnvironmentalConfig.solarArrayConfig.peakEnergyTier2;
                case 3:
                    return EnvironmentalConfig.solarArrayConfig.peakEnergyTier3;
                case 4:
                    return EnvironmentalConfig.solarArrayConfig.peakEnergyTier4;
                case 5:
                    return EnvironmentalConfig.solarArrayConfig.peakEnergyTier5;
                case 6:
                    return EnvironmentalConfig.solarArrayConfig.peakEnergyTier6;
                default:
                    return 0;
            }
        }
    }

}
