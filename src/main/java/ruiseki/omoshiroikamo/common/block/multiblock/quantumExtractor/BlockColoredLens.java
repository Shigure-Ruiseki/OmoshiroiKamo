package ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor;

import static ruiseki.omoshiroikamo.client.render.block.JsonModelISBRH.JSON_ISBRH_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import com.enderio.core.common.util.DyeColor;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.BlockOK;
import ruiseki.omoshiroikamo.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class BlockColoredLens extends BlockOK {

    public static String[] blocks = new String[]{"black", "red", "green", "brown", "blue", "purple", "cyan",
        "light_gray", "gray", "pink", "lime", "yellow", "light_blue", "magenta", "orange", "white"};

    protected BlockColoredLens() {
        super(ModObject.blockColoredLens, Material.glass);
    }

    public static BlockColoredLens create() {
        return new BlockColoredLens();
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, ItemBlockLaserLens.class, name);
    }

    @Override
    public int getRenderType() {
        return JSON_ISBRH_ID;
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
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
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < BlockColoredLens.blocks.length; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    public DyeColor getFocusColor(int meta) {
        meta = Math.min(meta, DyeColor.values().length);
        return DyeColor.values()[meta];
    }

    @Override
    public int getRenderColor(int meta) {
        int rgb = DyeColor.values()[meta].getColor();
        return (0xFF << 24) | ((rgb & 0xFF) << 16) | (rgb & 0xFF00) | ((rgb >> 16) & 0xFF);
    }

    @Override
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        int meta = worldIn.getBlockMetadata(x, y, z);
        return getRenderColor(meta);
    }

    public static class ItemBlockLaserLens extends ItemBlockOK {

        public ItemBlockLaserLens(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int meta = stack.getItemDamage();
            String base = super.getUnlocalizedName(stack);

            if (meta >= 0 && meta < BlockColoredLens.blocks.length) {
                return base + "." + BlockColoredLens.blocks[meta];
            } else {
                return base;
            }
        }
    }

}
