package ruiseki.omoshiroikamo.common.block.multiblock.voidMiner.oreMiner;

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

public class BlockVoidOreMiner extends AbstractMultiBlockBlock<TEVoidMiner> {

    protected BlockVoidOreMiner() {
        super(ModObject.blockVoidOreMiner, TEVoidMiner.class);
        this.setLightLevel(0.5F);
    }

    public static BlockVoidOreMiner create() {
        return new BlockVoidOreMiner();
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, ItemBlockVoidOreMiner.class, name);
        GameRegistry.registerTileEntity(TEVoidOreMinerT1.class, "TEVoidOreMinerT1TileEntity");
        GameRegistry.registerTileEntity(TEVoidOreMinerT2.class, "TEVoidOreMinerT2TileEntity");
        GameRegistry.registerTileEntity(TEVoidOreMinerT3.class, "TEVoidOreMinerT3TileEntity");
        GameRegistry.registerTileEntity(TEVoidOreMinerT4.class, "TEVoidOreMinerT4TileEntity");
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
                return new TEVoidOreMinerT4();
            case 2:
                return new TEVoidOreMinerT3();
            case 1:
                return new TEVoidOreMinerT2();
            default:
                return new TEVoidOreMinerT1();
        }
    }

    public static class ItemBlockVoidOreMiner extends ItemBlockOK {

        public ItemBlockVoidOreMiner(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
        }

    }

}
