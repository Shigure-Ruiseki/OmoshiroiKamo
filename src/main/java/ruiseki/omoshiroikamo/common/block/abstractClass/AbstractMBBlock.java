package ruiseki.omoshiroikamo.common.block.abstractClass;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.IMBBlock;
import ruiseki.omoshiroikamo.common.block.TileEntityOK;

public class AbstractMBBlock<T extends AbstractMBModifierTE> extends AbstractBlock<T> implements IMBBlock {

    protected AbstractMBBlock(ModObject mo, Class<T> teClass, Material material) {
        super(mo, teClass, material);
    }

    protected AbstractMBBlock(ModObject mo, Class<T> teClass) {
        super(mo, teClass);
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
