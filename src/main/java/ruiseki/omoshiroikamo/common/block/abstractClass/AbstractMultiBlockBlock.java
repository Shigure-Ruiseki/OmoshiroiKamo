package ruiseki.omoshiroikamo.common.block.abstractClass;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.multiblock.IMBBlock;
import ruiseki.omoshiroikamo.common.block.TileEntityEnder;

public class AbstractMultiBlockBlock<T extends AbstractMultiBlockModifierTE> extends AbstractBlock<T>
    implements IMBBlock {

    protected AbstractMultiBlockBlock(ModObject mo, Class<T> teClass, Material material) {
        super(mo, teClass, material);
    }

    protected AbstractMultiBlockBlock(ModObject mo, Class<T> teClass) {
        super(mo, teClass);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, player, stack);
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof AbstractMultiBlockModifierTE blockModifierTE) {
            if (player instanceof EntityPlayer) {
                blockModifierTE.setPlayer((EntityPlayer) player);
            }
        }
    }

    @Override
    protected void processDrop(World world, int x, int y, int z, TileEntityEnder te, ItemStack stack) {}
}
