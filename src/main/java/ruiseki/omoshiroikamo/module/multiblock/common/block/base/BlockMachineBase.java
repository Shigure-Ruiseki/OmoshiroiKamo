package ruiseki.omoshiroikamo.module.multiblock.common.block.base;

import java.util.List;

import net.minecraft.block.Block;
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
import ruiseki.omoshiroikamo.core.common.block.BlockOK;
import ruiseki.omoshiroikamo.core.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.core.lib.LibResources;

public class BlockMachineBase extends BlockOK implements IMBBlock {

    public static String[] blocks = new String[] { "basalt_machine_base", "hardened_stone_machine_base",
        "alabaster_machine_base" };

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    public static BlockMachineBase create() {
        return new BlockMachineBase();
    }

    protected BlockMachineBase() {
        super(ModObject.blockMachineBase.unlocalisedName);
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, ItemBlockMachineBase.class, name);
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

    public static class ItemBlockMachineBase extends ItemBlockOK {

        public ItemBlockMachineBase(Block block) {
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
