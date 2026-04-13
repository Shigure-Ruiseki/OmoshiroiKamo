package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumExtractor;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.client.render.IJsonModelBlock;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.IMBBlock;
import ruiseki.omoshiroikamo.core.block.BlockOK;
import ruiseki.omoshiroikamo.core.item.ItemBlockOK;

public class BlockLens extends BlockOK implements IMBBlock, IJsonModelBlock {

    protected BlockLens() {
        super(ModObject.LENS.name, Material.glass);
        hasSubtypes = true;
        isFullSize = isOpaque = false;
    }

    @Override
    public String getTextureName() {
        return "multiblock/lens_side";
    }

    public static BlockLens create() {
        return new BlockLens();
    }

    @Override
    protected Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockLens.class;
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
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
    }

    public static class ItemBlockLens extends ItemBlockOK {

        public ItemBlockLens(Block block) {
            super(block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
        }
    }
}
