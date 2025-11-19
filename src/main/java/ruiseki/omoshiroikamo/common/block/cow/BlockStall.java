package ruiseki.omoshiroikamo.common.block.cow;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.client.render.block.JsonModelISBRH;
import ruiseki.omoshiroikamo.common.block.TileEntityOK;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractBlock;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractTE;
import ruiseki.omoshiroikamo.common.entity.cow.EntityCowsCow;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

public class BlockStall extends AbstractBlock<TEStall> {

    protected BlockStall() {
        super(ModObject.blockStall, TEStall.class, Material.rock);
    }

    public static BlockStall create() {
        return new BlockStall();
    }

    @Override
    public int getRenderType() {
        return JsonModelISBRH.JSON_ISBRH_ID;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return super.getIcon(side, meta);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, player, stack);
        AbstractTE te = (AbstractTE) world.getTileEntity(x, y, z);
        if (te != null) {
            world.setBlockMetadataWithNotify(x, y, z, te.getFacing(), 2);
        }
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TEStall();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TEStall te) {
            spawnCow(world, x, y, z);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    private void spawnCow(World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (!(tileEntity instanceof TEStall TEStall)) {
            return;
        }

        if (!TEStall.hasCow()) {
            return;
        }

        EntityCowsCow cow = TEStall.getCow(world);
        cow.setPosition(x + 0.5, y, z + 0.5);
        cow.setType(TEStall.getCowType());
        world.spawnEntityInWorld(cow);
    }

    @Override
    protected void processDrop(World world, int x, int y, int z, TileEntityOK te, ItemStack stack) {

    }

    @Override
    public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TEStall TEStall) {

            float progress = TEStall.getProgress();

            if (progress > 0) {
                float percent = Math.max(0f, progress);
                tooltip.add(
                    String.format(
                        "%s: %s%.1f%%%s",
                        EnumChatFormatting.GRAY + LibMisc.LANG.localize("tooltip.progress"),
                        EnumChatFormatting.GRAY,
                        percent,
                        EnumChatFormatting.RESET));
            } else {
                tooltip.add(
                    EnumChatFormatting.GRAY + LibMisc.LANG.localize(
                        "tooltip.progress") + ": " + EnumChatFormatting.GRAY + "N/A" + EnumChatFormatting.RESET);
            }
        }
    }
}
