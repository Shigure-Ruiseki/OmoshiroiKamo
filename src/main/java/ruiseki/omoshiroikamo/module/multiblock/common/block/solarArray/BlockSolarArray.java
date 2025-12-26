package ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray;

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
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.multiblock.SolarArrayConfig;
import ruiseki.omoshiroikamo.core.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractTieredMBBlock;
import ruiseki.omoshiroikamo.core.integration.waila.IWailaBlockInfoProvider;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

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
        this.setLightLevel(0.5F);
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
            tooltip.add(WailaUtils.getCraftingState(solar));
            float efficiency = solar.calculateLightRatio();
            if (!solar.canSeeSun()) {
                tooltip.add(EnumChatFormatting.RED + LibMisc.LANG.localize("gui.sunlightBlocked"));
            } else {
                tooltip.add(LibMisc.LANG.localize("gui.efficiency", efficiency * 100));
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
            tooltip.add(LibMisc.LANG.localize("tooltip.solar.array.peak", peakEnergy));
        }

        private int getPeakEnergy(int tier) {
            switch (tier) {
                case 1:
                    return SolarArrayConfig.tierEnergy.tier1;
                case 2:
                    return SolarArrayConfig.tierEnergy.tier2;
                case 3:
                    return SolarArrayConfig.tierEnergy.tier3;
                case 4:
                    return SolarArrayConfig.tierEnergy.tier4;
                case 5:
                    return SolarArrayConfig.tierEnergy.tier5;
                case 6:
                    return SolarArrayConfig.tierEnergy.tier6;
                default:
                    return 0;
            }
        }
    }

}
