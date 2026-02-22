package ruiseki.omoshiroikamo.module.multiblock.common.block.base;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.block.BlockOK;
import ruiseki.omoshiroikamo.core.item.ItemBlockOK;

public class BlockCrystal extends BlockOK {

    public static final int VARIATIONS = 8;

    protected BlockCrystal() {
        super(ModObject.blockBlockCrystal.unlocalisedName);
        setTextureName("multiblock/crystal_block");
    }

    public static BlockCrystal create() {
        return new BlockCrystal();
    }

    @Override
    protected Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockCrystal.class;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < VARIATIONS; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public int getRenderColor(int meta) {
        return switch (meta) {
            case 0 -> EnumDye.rgbToHex(138, 255, 250);
            case 1 -> EnumDye.rgbToHex(255, 179, 71);
            case 2 -> EnumDye.rgbToHex(11, 0, 51);
            case 3 -> EnumDye.rgbToHex(27, 255, 212);
            case 4 -> EnumDye.rgbToHex(28, 28, 28);
            case 5 -> EnumDye.rgbToHex(177, 156, 217);
            case 6 -> EnumDye.rgbToHex(128, 0, 128);
            case 7 -> EnumDye.rgbToHex(255, 215, 0);
            default -> EnumDye.WHITE.getColor();
        };
    }

    @Override
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        return getRenderColor(worldIn.getBlockMetadata(x, y, z));
    }

    public static class ItemBlockCrystal extends ItemBlockOK {

        public ItemBlockCrystal(Block block) {
            super(block);
        }

        @Override
        public int getColorFromItemStack(ItemStack stack, int pass) {
            Block block = Block.getBlockFromItem(stack.getItem());
            return block.getRenderColor(stack.getItemDamage());
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
        }
    }
}
