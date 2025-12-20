package ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore;

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
import ruiseki.omoshiroikamo.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractTieredMBBlock;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.TEQuantumExtractor;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.plugin.waila.IWailaBlockInfoProvider;

public class BlockQuantumOreExtractor extends AbstractTieredMBBlock<TEQuantumExtractor>
    implements IWailaBlockInfoProvider {

    protected BlockQuantumOreExtractor() {
        super(
            ModObject.blockQuantumOreExtractor.unlocalisedName,
            TEQuantumOreExtractorT1.class,
            TEQuantumOreExtractorT2.class,
            TEQuantumOreExtractorT3.class,
            TEQuantumOreExtractorT4.class,
            TEQuantumOreExtractorT5.class,
            TEQuantumOreExtractorT6.class);
        this.setLightLevel(0.5F);
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

    public static class ItemBlockQuantumOreExtractor extends ItemBlockOK {

        public ItemBlockQuantumOreExtractor(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
        }

    }

}
