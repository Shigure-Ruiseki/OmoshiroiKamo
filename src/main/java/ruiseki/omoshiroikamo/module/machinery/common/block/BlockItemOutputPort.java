package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.io.ISidedIO;
import ruiseki.omoshiroikamo.api.modular.IModularBlock;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.common.item.ItemWrench;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.output.TEItemOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.output.TEItemOutputPortT1;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.output.TEItemOutputPortT2;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.output.TEItemOutputPortT3;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.output.TEItemOutputPortT4;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.output.TEItemOutputPortT5;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.output.TEItemOutputPortT6;

/**
 * Item Output Port - outputs items from machine processing.
 * Can be placed at IO slot positions in machine structures.
 * Uses JSON model with base + overlay textures via GTNHLib.
 * TODO List:
 * - Implement BlockColor tinting for machine color customization
 * - Add animation/particle effects when outputting items
 * - Support comparator output for automation
 */
public class BlockItemOutputPort extends AbstractPortBlock<TEItemOutputPort> implements IModularBlock {

    protected BlockItemOutputPort() {
        super(
            ModObject.blockModularItemOutput.unlocalisedName,
            TEItemOutputPortT1.class,
            TEItemOutputPortT2.class,
            TEItemOutputPortT3.class,
            TEItemOutputPortT4.class,
            TEItemOutputPortT5.class,
            TEItemOutputPortT6.class);
        setHardness(5.0F);
        setResistance(10.0F);
    }

    public static BlockItemOutputPort create() {
        return new BlockItemOutputPort();
    }

    @Override
    public String getTextureName() {
        return "modularmachineryOverlay/base_modularports";
    }

    @Override
    public void registerPortOverlays(IIconRegister reg) {
        IconRegistry.addIcon(
            "overlay_itemoutput_1",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_itemoutput_1"));
        IconRegistry.addIcon(
            "overlay_itemoutput_2",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_itemoutput_2"));
        IconRegistry.addIcon(
            "overlay_itemoutput_3",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_itemoutput_3"));
        IconRegistry.addIcon(
            "overlay_itemoutput_4",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_itemoutput_4"));
        IconRegistry.addIcon(
            "overlay_itemoutput_5",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_itemoutput_5"));
        IconRegistry.addIcon(
            "overlay_itemoutput_6",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_itemoutput_6"));
    }

    @Override
    protected Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockItemOutputPort.class;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(itemIn, 1, 0));
        list.add(new ItemStack(itemIn, 1, 1));
        list.add(new ItemStack(itemIn, 1, 2));
        list.add(new ItemStack(itemIn, 1, 3));
        list.add(new ItemStack(itemIn, 1, 4));
        list.add(new ItemStack(itemIn, 1, 5));
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        dropStacks(world, x, y, z);
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        TileEntity tileEntity = accessor.getTileEntity();
        if (tileEntity instanceof IInventory handler) {
            tooltip.add(WailaUtils.getInventoryTooltip(handler));
        }
        if (tileEntity instanceof ISidedIO io) {
            Vec3 hit = WailaUtils.getLocalHit(accessor);
            if (hit == null) return;
            ForgeDirection side = ItemWrench
                .getClickedSide(accessor.getSide(), (float) hit.xCoord, (float) hit.yCoord, (float) hit.zCoord);
            tooltip.add(WailaUtils.getSideIOTooltip(io, side));
        }
    }

    public static class ItemBlockItemOutputPort extends AbstractPortItemBlock {

        public ItemBlockItemOutputPort(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
        }

        @Override
        public IIcon getOverlayIcon(int tier) {
            return IconRegistry.getIcon("overlay_itemoutput_" + tier);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            // TODO: Add tooltips
        }
    }

    @Override
    public Type getPortType() {
        return Type.ITEM;
    }

    @Override
    public Direction getPortDirection() {
        return Direction.OUTPUT;
    }
}
