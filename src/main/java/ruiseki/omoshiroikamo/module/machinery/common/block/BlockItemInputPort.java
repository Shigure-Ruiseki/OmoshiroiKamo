package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
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
    public String getOverlayPrefix() {
        return "overlay_iteminput_";
    }

    @Override
    public Class<? extends AbstractPortItemBlock> getItemBlockClass() {
        return ItemBlockItemInputPort.class;
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
        super.getWailaInfo(tooltip, itemStack, accessor, config);
        TileEntity tileEntity = accessor.getTileEntity();
        if (tileEntity instanceof IInventory handler) {
            tooltip.add(WailaUtils.getInventoryTooltip(handler));
        }
    }

    @Override
    protected void addCapacityTooltip(List<String> list, int tier) {
        list.add(LibMisc.LANG.localize("tooltip.machinery.slots", MachineryConfig.getItemPortSlots(tier)));
    }

    public static class ItemBlockItemInputPort extends AbstractPortItemBlock {

        public ItemBlockItemInputPort(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            super.addInformation(stack, player, list, flag);
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
