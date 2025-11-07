package ruiseki.omoshiroikamo.common.block.multiblock.solarArray;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractMultiBlockBlock;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.plugin.waila.IWailaInfoProvider;

public class BlockSolarArray extends AbstractMultiBlockBlock<TESolarArray> implements IWailaInfoProvider {

    protected BlockSolarArray() {
        super(ModObject.blockSolarArray, TESolarArray.class);
        setTextureName("cont_tier");
    }

    public static BlockSolarArray create() {
        return new BlockSolarArray();
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, ItemBlockSolarArray.class, name);
        GameRegistry.registerTileEntity(TESolarArrayT1.class, name + "TESolarArrayT1TileEntity");
        GameRegistry.registerTileEntity(TESolarArrayT2.class, name + "TESolarArrayT2TileEntity");
        GameRegistry.registerTileEntity(TESolarArrayT3.class, name + "TESolarArrayT3TileEntity");
        GameRegistry.registerTileEntity(TESolarArrayT4.class, name + "TESolarArrayT4TileEntity");
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
        list.add(new ItemStack(this, 1, 2));
        list.add(new ItemStack(this, 1, 3));
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
                return new TESolarArrayT4();
            case 2:
                return new TESolarArrayT3();
            case 1:
                return new TESolarArrayT2();
            default:
                return new TESolarArrayT1();
        }
    }

    @Override
    public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TESolarArray solar) {
            float efficiency = solar.calculateLightRatio();
            if (!solar.canSeeSun()) {
                tooltip.add(EnumChatFormatting.RED + LibMisc.LANG.localize("tooltip.sunlightBlocked"));
            } else {
                tooltip.add(
                    String.format(
                        "%s : %s%.0f%%",
                        EnumChatFormatting.GRAY + LibMisc.LANG.localize("tooltip.efficiency")
                            + EnumChatFormatting.RESET,
                        EnumChatFormatting.GRAY,
                        efficiency * 100));
            }
        }
    }

    @Override
    public int getDefaultDisplayMask(World world, int x, int y, int z) {
        return 0;
    }

    public static class ItemBlockSolarArray extends ItemBlockOK {

        public ItemBlockSolarArray(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
        }
    }

}
