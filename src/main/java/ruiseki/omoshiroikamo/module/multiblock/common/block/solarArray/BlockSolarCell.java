package ruiseki.omoshiroikamo.module.multiblock.common.block.solarArray;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.client.model.color.BlockColor;
import com.gtnewhorizon.gtnhlib.client.model.color.IBlockColor;

import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.IMBBlock;
import ruiseki.omoshiroikamo.config.backport.multiblock.SolarArrayConfig;
import ruiseki.omoshiroikamo.core.block.BlockOK;
import ruiseki.omoshiroikamo.core.item.ItemBlockOK;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class BlockSolarCell extends BlockOK implements IMBBlock {

    protected BlockSolarCell() {
        super(ModObject.blockSolarCell.unlocalisedName);
        hasSubtypes = true;
    }

    public static BlockSolarCell create() {
        return new BlockSolarCell();
    }

    @Override
    protected Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockSolar.class;
    }

    @Override
    protected void registerBlockColor() {
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
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        setBlockBounds(0f, 0f, 0f, 1f, 13f / 16f, 1f);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    public static class ItemBlockSolar extends ItemBlockOK {

        public ItemBlockSolar(Block block) {
            super(block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
            super.addInformation(stack, player, tooltip, advanced);
            int tier = stack.getItemDamage();
            int cellEnergy = getCellEnergy(tier);
            tooltip.add(LibMisc.LANG.localize("tooltip.solar.cell.power", cellEnergy));
        }

        private int getCellEnergy(int cellTier) {
            int base = SolarArrayConfig.cellSettings.baseGeneration;
            float multiplier = SolarArrayConfig.cellSettings.tierMultiplier;
            return (int) Math.round(base * Math.pow(multiplier, cellTier));
        }
    }

}
