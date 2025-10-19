package ruiseki.omoshiroikamo.common.block.multiblock.nanoBotBeacon;

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
import ruiseki.omoshiroikamo.common.OKCreativeTab;
import ruiseki.omoshiroikamo.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractMultiBlockBlock;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class BlockNanoBotBeacon extends AbstractMultiBlockBlock<TENanoBotBeacon> {

    protected BlockNanoBotBeacon() {
        super(ModObject.blockNanoBotBeacon, TENanoBotBeacon.class);
        this.setLightLevel(0.2F);
    }

    public static BlockNanoBotBeacon create() {
        return new BlockNanoBotBeacon();
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, ItemBlockNanoBotBeacon.class, name);
        GameRegistry.registerTileEntity(TENanoBotBeaconT1.class, "TENanoBotBeaconT1TileEntity");
        GameRegistry.registerTileEntity(TENanoBotBeaconT2.class, "TENanoBotBeaconT2TileEntity");
        GameRegistry.registerTileEntity(TENanoBotBeaconT3.class, "TENanoBotBeaconT3TileEntity");
        GameRegistry.registerTileEntity(TENanoBotBeaconT4.class, "TENanoBotBeaconT4TileEntity");
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
                return new TENanoBotBeaconT4();
            case 2:
                return new TENanoBotBeaconT3();
            case 1:
                return new TENanoBotBeaconT2();
            default:
                return new TENanoBotBeaconT1();
        }
    }

    public static class ItemBlockNanoBotBeacon extends ItemBlockOK {

        public ItemBlockNanoBotBeacon(Block block) {
            super(block, block);
            setCreativeTab(OKCreativeTab.tabBlock);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
        }

    }

}
