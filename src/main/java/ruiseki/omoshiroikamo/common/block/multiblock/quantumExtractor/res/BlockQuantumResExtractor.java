package ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.res;

import static ruiseki.omoshiroikamo.client.render.block.JsonModelISBRH.JSON_ISBRH_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.enderio.core.common.util.DyeColor;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractMultiBlockBlock;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.TEQuantumExtractor;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.plugin.waila.IWailaInfoProvider;

public class BlockQuantumResExtractor extends AbstractMultiBlockBlock<TEQuantumExtractor>
    implements IWailaInfoProvider {

    protected BlockQuantumResExtractor() {
        super(ModObject.blockQuantumResExtractor, TEQuantumExtractor.class);
        this.setLightLevel(0.5F);
    }

    public static BlockQuantumResExtractor create() {
        return new BlockQuantumResExtractor();
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, ItemBlockQuantumResExtractor.class, name);
        GameRegistry.registerTileEntity(TEQuantumResExtractorT1.class, "TEQuantumResExtractorT1TileEntity");
        GameRegistry.registerTileEntity(TEQuantumResExtractorT2.class, "TEQuantumResExtractorT2TileEntity");
        GameRegistry.registerTileEntity(TEQuantumResExtractorT3.class, "TEQuantumResExtractorT3TileEntity");
        GameRegistry.registerTileEntity(TEQuantumResExtractorT4.class, "TEQuantumResExtractorT4TileEntity");
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
        list.add(new ItemStack(this, 1, 2));
        list.add(new ItemStack(this, 1, 3));
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
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderColor(int meta) {
        int rgb;
        switch (meta) {
            case 0:
                rgb = DyeColor.YELLOW.getColor();
                break;
            case 1:
                rgb = DyeColor.LIGHT_BLUE.getColor();
                break;
            case 2:
                rgb = DyeColor.CYAN.getColor();
                break;
            case 3:
                rgb = DyeColor.WHITE.getColor();
                break;
            default:
                rgb = DyeColor.WHITE.getColor();
                break;
        }

        return (0xFF << 24) | ((rgb & 0xFF) << 16) | (rgb & 0xFF00) | ((rgb >> 16) & 0xFF);
    }

    @Override
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        return this.getRenderColor(worldIn.getBlockMetadata(x, y, z));
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        switch (meta) {
            case 3:
                return new TEQuantumResExtractorT4();
            case 2:
                return new TEQuantumResExtractorT3();
            case 1:
                return new TEQuantumResExtractorT2();
            default:
                return new TEQuantumResExtractorT1();
        }
    }

    @Override
    public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TEQuantumExtractor extractor) {
            int progress = extractor.getCurrentProgress();
            int duration = extractor.getCurrentDuration();

            if (duration > 0) {
                float percent = (progress / (float) duration) * 100f;
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

    }

}
