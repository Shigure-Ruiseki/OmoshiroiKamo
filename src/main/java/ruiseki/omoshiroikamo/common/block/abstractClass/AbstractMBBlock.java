package ruiseki.omoshiroikamo.common.block.abstractClass;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.gtnhlib.client.model.color.BlockColor;
import com.gtnewhorizon.gtnhlib.client.model.color.IBlockColor;

import ruiseki.omoshiroikamo.api.enums.EnumDye;
import ruiseki.omoshiroikamo.api.multiblock.IMBBlock;
import ruiseki.omoshiroikamo.common.block.TileEntityOK;

public class AbstractMBBlock<T extends AbstractMBModifierTE> extends AbstractBlock<T> implements IMBBlock {

    protected AbstractMBBlock(String name, Class<T> teClass, Material material) {
        super(name, teClass, material);
    }

    protected AbstractMBBlock(String name, Class<T> teClass) {
        super(name, teClass);
    }

    public void registerBlockColor() {
        BlockColor.registerBlockColors(new IBlockColor() {

            @Override
            public int colorMultiplier(IBlockAccess world, int x, int y, int z, int tintIndex) {
                if (world != null) {
                    int meta = world.getBlockMetadata(x, y, z);
                    if (tintIndex == 0) {
                        switch (meta) {
                            case 0:
                                return EnumDye.YELLOW.dyeToAbgr();
                            case 1:
                                return EnumDye.LIGHT_BLUE.dyeToAbgr();
                            case 2:
                                return EnumDye.CYAN.dyeToAbgr();
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
                            return EnumDye.YELLOW.dyeToAbgr();
                        case 1:
                            return EnumDye.LIGHT_BLUE.dyeToAbgr();
                        case 2:
                            return EnumDye.CYAN.dyeToAbgr();
                        default:
                            return EnumDye.WHITE.dyeToAbgr();
                    }
                }
                return EnumDye.WHITE.dyeToAbgr();
            }
        }, this);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, player, stack);
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof AbstractMBModifierTE blockModifierTE) {
            if (player instanceof EntityPlayer) {
                blockModifierTE.setPlayer((EntityPlayer) player);
            }
        }
    }

    @Override
    protected void processDrop(World world, int x, int y, int z, TileEntityOK te, ItemStack stack) {}

}
