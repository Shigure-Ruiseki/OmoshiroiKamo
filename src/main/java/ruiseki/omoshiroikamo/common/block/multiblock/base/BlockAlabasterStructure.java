package ruiseki.omoshiroikamo.common.block.multiblock.base;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import com.gtnewhorizon.gtnhlib.client.model.color.BlockColor;
import com.gtnewhorizon.gtnhlib.client.model.color.IBlockColor;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.IMBBlock;
import ruiseki.omoshiroikamo.common.block.BlockOK;
import ruiseki.omoshiroikamo.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class BlockAlabasterStructure extends BlockOK implements IMBBlock {

    IIcon base;

    protected BlockAlabasterStructure() {
        super(ModObject.blockAlabasterStructure.unlocalisedName);
    }

    public static BlockAlabasterStructure create() {
        return new BlockAlabasterStructure();
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, ItemBlockAlabasterStructure.class, name);
        BlockColor.registerBlockColors(new IBlockColor() {

            @Override
            public int colorMultiplier(IBlockAccess world, int x, int y, int z, int tintIndex) {
                if (world != null) {
                    int meta = world.getBlockMetadata(x, y, z);
                    if (tintIndex == 0) {
                        switch (meta) {
                            case 0:
                                return EnumDye.rgbToAbgr(138, 255, 250);
                            case 1:
                                return EnumDye.rgbToAbgr(255, 179, 71);
                            case 2:
                                return EnumDye.rgbToAbgr(11, 0, 51);
                            case 3:
                                return EnumDye.rgbToAbgr(27, 255, 212);
                            case 4:
                                return EnumDye.rgbToAbgr(28, 28, 28);
                            case 5:
                                return EnumDye.rgbToAbgr(177, 156, 217);
                            default:
                                return EnumDye.WHITE.dyeToAbgr();
                        }
                    }
                }
                return EnumDye.WHITE.dyeToAbgr();
            }

            @Override
            public int colorMultiplier(ItemStack stack, int tintIndex) {
                if (tintIndex == 0 && stack != null) {
                    switch (stack.getItemDamage()) {
                        case 0:
                            return EnumDye.rgbToAbgr(138, 255, 250);
                        case 1:
                            return EnumDye.rgbToAbgr(255, 179, 71);
                        case 2:
                            return EnumDye.rgbToAbgr(11, 0, 51);
                        case 3:
                            return EnumDye.rgbToAbgr(27, 255, 212);
                        case 4:
                            return EnumDye.rgbToAbgr(28, 28, 28);
                        case 5:
                            return EnumDye.rgbToAbgr(177, 156, 217);
                        default:
                            return EnumDye.WHITE.dyeToAbgr();
                    }
                }
                return EnumDye.WHITE.dyeToAbgr();
            }
        }, this);
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
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        base = reg.registerIcon(LibResources.PREFIX_MOD + "alabaster_structure");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return this.base;
    }

    public static class ItemBlockAlabasterStructure extends ItemBlockOK {

        public ItemBlockAlabasterStructure(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
        }
    }
}
