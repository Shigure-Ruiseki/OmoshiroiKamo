package ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.ore;

import static ruiseki.omoshiroikamo.client.render.block.JsonModelISBRH.JSON_ISBRH_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractMBBlock;
import ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor.TEQuantumExtractor;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.plugin.waila.IWailaBlockInfoProvider;

public class BlockQuantumOreExtractor extends AbstractMBBlock<TEQuantumExtractor> implements IWailaBlockInfoProvider {

    @SideOnly(Side.CLIENT)
    IIcon cont_tier;

    protected BlockQuantumOreExtractor() {
        super(ModObject.blockQuantumOreExtractor, TEQuantumExtractor.class);
        this.setLightLevel(0.5F);
    }

    public static BlockQuantumOreExtractor create() {
        return new BlockQuantumOreExtractor();
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, ItemBlockQuantumOreExtractor.class, name);
        GameRegistry.registerTileEntity(TEQuantumOreExtractorT1.class, "TEQuantumOreExtractorT1TileEntity");
        GameRegistry.registerTileEntity(TEQuantumOreExtractorT2.class, "TEQuantumOreExtractorT2TileEntity");
        GameRegistry.registerTileEntity(TEQuantumOreExtractorT3.class, "TEQuantumOreExtractorT3TileEntity");
        GameRegistry.registerTileEntity(TEQuantumOreExtractorT4.class, "TEQuantumOreExtractorT4TileEntity");
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
    public void registerBlockIcons(IIconRegister reg) {
        cont_tier = reg.registerIcon(LibResources.PREFIX_MOD + "cont_tier");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return this.cont_tier;
    }

    @Override
    public int getRenderColor(int meta) {
        int rgb;
        switch (meta) {
            case 0:
                rgb = EnumDye.YELLOW.getColor();
                break;
            case 1:
                rgb = EnumDye.LIGHT_BLUE.getColor();
                break;
            case 2:
                rgb = EnumDye.CYAN.getColor();
                break;
            default:
                rgb = EnumDye.WHITE.getColor();
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
                return new TEQuantumOreExtractorT4();
            case 2:
                return new TEQuantumOreExtractorT3();
            case 1:
                return new TEQuantumOreExtractorT2();
            default:
                return new TEQuantumOreExtractorT1();
        }
    }

    @Override
    public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TEQuantumExtractor extractor) {
            int progress = extractor.getCurrentProgress();
            int duration = extractor.getCurrentProcessDuration();

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
