package ruiseki.omoshiroikamo.common.block.multiblock.solarArray;

import static ruiseki.omoshiroikamo.api.client.JsonModelISBRH.JSON_ISBRH_ID;

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
import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractMBBlock;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.plugin.waila.IWailaBlockInfoProvider;

public class BlockSolarArray extends AbstractMBBlock<TESolarArray> implements IWailaBlockInfoProvider {

    IIcon solar_tex;

    protected BlockSolarArray() {
        super(ModObject.blockSolarArray.unlocalisedName, TESolarArray.class);
    }

    public static BlockSolarArray create() {
        return new BlockSolarArray();
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, ItemBlockSolarArray.class, name);
        GameRegistry.registerTileEntity(TESolarArrayT1.class, name + "TESolarArrayT1TileEntity");
        GameRegistry.registerTileEntity(TESolarArrayT2.class, name + "TESolarArrayT2TileEntity");
        GameRegistry.registerTileEntity(TESolarArrayT3.class, name + "TESolarArrayT3TileEntity");
        GameRegistry.registerTileEntity(TESolarArrayT4.class, name + "TESolarArrayT4TileEntity");
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
        list.add(new ItemStack(this, 1, 2));
        list.add(new ItemStack(this, 1, 3));
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        solar_tex = reg.registerIcon(LibResources.PREFIX_MOD + "solar_tex");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return this.solar_tex;
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
                return new TESolarArrayT4();
            case 2:
                return new TESolarArrayT3();
            case 1:
                return new TESolarArrayT2();
            default:
                return new TESolarArrayT1();
        }
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
    }

}
