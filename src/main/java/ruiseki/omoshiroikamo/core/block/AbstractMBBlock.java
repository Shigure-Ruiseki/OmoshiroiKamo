package ruiseki.omoshiroikamo.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.gtnewhorizon.gtnhlib.client.model.color.BlockColor;
import com.gtnewhorizon.gtnhlib.client.model.color.IBlockColor;

import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.multiblock.IMBBlock;
import ruiseki.omoshiroikamo.core.tileentity.AbstractMBModifierTE;

public class AbstractMBBlock<T extends AbstractMBModifierTE> extends AbstractBlock<T> implements IMBBlock {

    protected AbstractMBBlock(String name, Class<T> teClass, Material material) {
        super(name, teClass, material);
    }

    protected AbstractMBBlock(String name, Class<T> teClass) {
        super(name, teClass);
    }

    @Override
    protected void registerBlockColor() {
        BlockColor.registerBlockColors(new IBlockColor() {

            @Override
            public int colorMultiplier(IBlockAccess world, int x, int y, int z, int tintIndex) {
                if (world != null && tintIndex == 0) {
                    return getColor(world.getBlockMetadata(x, y, z));
                }
                return EnumDye.WHITE.dyeToAbgr();
            }

            @Override
            public int colorMultiplier(ItemStack stack, int tintIndex) {
                if (stack != null && tintIndex == 0) {
                    return getColor(stack.getItemDamage());
                }
                return EnumDye.WHITE.dyeToAbgr();
            }
        }, this);
    }

    private int getColor(int meta) {
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

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof AbstractMBModifierTE blockModifierTE) {
            if (player instanceof EntityPlayer) {
                blockModifierTE.setPlayer((EntityPlayer) player);
            }
        }
    }

}
