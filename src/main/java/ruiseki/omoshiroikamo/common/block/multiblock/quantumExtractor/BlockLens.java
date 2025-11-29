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

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.IMBBlock;
import ruiseki.omoshiroikamo.common.block.BlockOK;
import ruiseki.omoshiroikamo.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class BlockLens extends BlockOK implements IMBBlock {

    @SideOnly(Side.CLIENT)
    IIcon lens_top, lens_top_2, lens_side, lens_side_2;

    protected BlockLens() {
        super(ModObject.blockLens.unlocalisedName, Material.glass);
    }

    public static BlockLens create() {
        return new BlockLens();
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, ItemBlockLens.class, name);
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
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        lens_top = reg.registerIcon(LibResources.PREFIX_MOD + "lens_top");
        lens_top_2 = reg.registerIcon(LibResources.PREFIX_MOD + "lens_top_2");
        lens_side = reg.registerIcon(LibResources.PREFIX_MOD + "lens_side");
        lens_side_2 = reg.registerIcon(LibResources.PREFIX_MOD + "lens_side_2");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return this.lens_top;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
    }

    public static class ItemBlockLens extends ItemBlockOK {

        public ItemBlockLens(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
        }
    }
}
