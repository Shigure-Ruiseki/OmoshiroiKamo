package ruiseki.omoshiroikamo.common.block.multiblock.voidMiner.resMiner;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractMultiBlockBlock;
import ruiseki.omoshiroikamo.common.block.multiblock.voidMiner.TEVoidMiner;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class BlockVoidResMiner extends AbstractMultiBlockBlock<TEVoidMiner> {

    protected BlockVoidResMiner() {
        super(ModObject.blockVoidResMiner, TEVoidMiner.class);
        this.setLightLevel(0.5F);
    }

    public static BlockVoidResMiner create() {
        return new BlockVoidResMiner();
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, ItemBlockVoidResMiner.class, name);
        GameRegistry.registerTileEntity(TEVoidResMinerT1.class, "TEVoidResMinerT1TileEntity");
        GameRegistry.registerTileEntity(TEVoidResMinerT2.class, "TEVoidResMinerT2TileEntity");
        GameRegistry.registerTileEntity(TEVoidResMinerT3.class, "TEVoidResMinerT3TileEntity");
        GameRegistry.registerTileEntity(TEVoidResMinerT4.class, "TEVoidResMinerT4TileEntity");
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
        list.add(new ItemStack(this, 1, 2));
        list.add(new ItemStack(this, 1, 3));
    }

    @Override
    public void registerBlockIcons(IIconRegister iIconRegister) {
        blockIcon = iIconRegister.registerIcon(LibResources.PREFIX_MOD + "cont_tier");
    }

    @Override
    public int getRenderType() {
        return -1;
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
    public TileEntity createTileEntity(World world, int meta) {
        switch (meta) {
            case 3:
                return new TEVoidResMinerT4();
            case 2:
                return new TEVoidResMinerT3();
            case 1:
                return new TEVoidResMinerT2();
            default:
                return new TEVoidResMinerT1();
        }
    }

    public static class ItemBlockVoidResMiner extends ItemBlockOK {

        public ItemBlockVoidResMiner(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
        }

    }

}
