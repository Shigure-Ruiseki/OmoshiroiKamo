package ruiseki.omoshiroikamo.common.block.multiblock.base;

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

public class BlockStructureFrame extends BlockOK implements IMBBlock {

    public static String[] blocks = new String[] { "basalt_structure_1", "basalt_structure_2", "basalt_structure_3",
        "basalt_structure_4", "hardened_stone_structure_1", "hardened_stone_structure_2", "hardened_stone_structure_3",
        "hardened_stone_structure_4", "alabaster_structure_1", "alabaster_structure_2", "alabaster_structure_3",
        "alabaster_structure_4" };

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public static BlockStructureFrame create() {
        return new BlockStructureFrame();
    }

    private BlockStructureFrame() {
        super(ModObject.blockStructureFrame, null, Material.rock);
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, ItemBlockStructureFrame.class, ModObject.blockStructureFrame.unlocalisedName);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        for (int i = 0; i < blocks.length; i++) {
            list.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        icons = new IIcon[blocks.length];

        for (int i = 0; i < blocks.length; i++) {
            String iconName = LibResources.PREFIX_MOD + blocks[i];
            icons[i] = reg.registerIcon(iconName);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (icons == null || meta < 0 || meta >= icons.length) {
            return blockIcon;
        }
        return icons[meta];
    }

    public static class ItemBlockStructureFrame extends ItemBlockOK {

        public ItemBlockStructureFrame(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int meta = stack.getItemDamage();
            String base = super.getUnlocalizedName(stack);

            if (meta >= 0 && meta < blocks.length) {
                return base + "." + blocks[meta];
            } else {
                return base;
            }
        }

    }

}
