package ruiseki.omoshiroikamo.common.block.multiblock.quantumExtractor;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.client.model.color.BlockColor;
import com.gtnewhorizon.gtnhlib.client.model.color.IBlockColor;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.IMBBlock;
import ruiseki.omoshiroikamo.common.block.BlockOK;
import ruiseki.omoshiroikamo.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class BlockColoredLens extends BlockOK implements IMBBlock {

    @SideOnly(Side.CLIENT)
    IIcon lens_colored_top, lens_colored_top_2, lens_colored_side, lens_colored_side_2, glass_colored;

    public static String[] blocks = new String[] { "black", "red", "green", "brown", "blue", "purple", "cyan",
        "light_gray", "gray", "pink", "lime", "yellow", "light_blue", "magenta", "orange", "white" };

    protected BlockColoredLens() {
        super(ModObject.blockColoredLens.unlocalisedName, Material.glass);
    }

    public static BlockColoredLens create() {
        return new BlockColoredLens();
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, ItemBlockColoredLens.class, name);
        BlockColor.registerBlockColors(new IBlockColor() {

            @Override
            public int colorMultiplier(@Nullable IBlockAccess world, int x, int y, int z, int tintIndex) {
                if (tintIndex == 0 && world != null) {
                    int meta = world.getBlockMetadata(x, y, z);
                    return EnumDye.values()[meta].dyeToAbgr();
                }
                return -1;
            }

            @Override
            public int colorMultiplier(@Nullable ItemStack stack, int tintIndex) {
                if (tintIndex == 0 && stack != null) {
                    int meta = stack.getItemDamage();
                    return EnumDye.values()[meta].dyeToAbgr();
                }
                return -1;
            }
        }, this);
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
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        lens_colored_top = reg.registerIcon(LibResources.PREFIX_MOD + "lens_colored_top");
        lens_colored_top_2 = reg.registerIcon(LibResources.PREFIX_MOD + "lens_colored_top_2");
        lens_colored_side = reg.registerIcon(LibResources.PREFIX_MOD + "lens_colored_side");
        lens_colored_side_2 = reg.registerIcon(LibResources.PREFIX_MOD + "lens_colored_side_2");
        glass_colored = reg.registerIcon(LibResources.PREFIX_MOD + "glass_colored");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return this.lens_colored_top;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < BlockColoredLens.blocks.length; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    public EnumDye getFocusColor(int meta) {
        meta = Math.min(meta, EnumDye.values().length);
        return EnumDye.values()[meta];
    }

    public static class ItemBlockColoredLens extends ItemBlockOK {

        public ItemBlockColoredLens(Block block) {
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
