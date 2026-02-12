package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.block.ISidedIO;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.common.item.ItemWrench;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.input.TEItemInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.input.TEItemInputPortT1;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.input.TEItemInputPortT2;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.input.TEItemInputPortT3;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.input.TEItemInputPortT4;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.input.TEItemInputPortT5;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.input.TEItemInputPortT6;

public class BlockItemInputPort extends AbstractPortBlock<TEItemInputPort> {

    protected BlockItemInputPort() {
        super(
            ModObject.blockModularItemInput.unlocalisedName,
            TEItemInputPortT1.class,
            TEItemInputPortT2.class,
            TEItemInputPortT3.class,
            TEItemInputPortT4.class,
            TEItemInputPortT5.class,
            TEItemInputPortT6.class);
        setHardness(5.0F);
        setResistance(10.0F);
        setTextureName("modularmachineryOverlay/base_modularports");
    }

    public static BlockItemInputPort create() {
        return new BlockItemInputPort();
    }

    @Override
    public void registerPortOverlays(IIconRegister reg) {
        IconRegistry.addIcon(
            "overlay_iteminput_1",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_iteminput_1"));
        IconRegistry.addIcon(
            "overlay_iteminput_2",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_iteminput_2"));
        IconRegistry.addIcon(
            "overlay_iteminput_3",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_iteminput_3"));
        IconRegistry.addIcon(
            "overlay_iteminput_4",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_iteminput_4"));
        IconRegistry.addIcon(
            "overlay_iteminput_5",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_iteminput_5"));
        IconRegistry.addIcon(
            "overlay_iteminput_6",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_iteminput_6"));
        // Fallback/disabled overlay for faces where IO is blocked
        IconRegistry.addIcon(
            "overlay_iteminput_disabled",
            reg.registerIcon(LibResources.PREFIX_MOD + "modular_machine_casing"));
    }

    @Override
    public Class<? extends AbstractPortItemBlock> getItemBlockClass() {
        return ItemBlockItemInputPort.class;
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
        // TODO: Show filter status if enabled
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

    public static class ItemBlockItemInputPort extends AbstractPortItemBlock {

        public ItemBlockItemInputPort(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
        }

        @Override
        public IIcon getOverlayIcon(int tier) {
            return IconRegistry.getIcon("overlay_iteminput_" + tier);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            // TODO: Add tooltips
        }
    }

    @Override
    public Type getPortType() {
        return IPortType.Type.ITEM;
    }

    @Override
    public IPortType.Direction getPortDirection() {
        return Direction.INPUT;
    }
}
