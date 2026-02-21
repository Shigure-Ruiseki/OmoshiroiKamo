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
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.output.TEItemOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.output.TEItemOutputPortT1;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.output.TEItemOutputPortT2;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.output.TEItemOutputPortT3;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.output.TEItemOutputPortT4;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.output.TEItemOutputPortT5;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.output.TEItemOutputPortT6;

public class BlockItemOutputPort extends AbstractPortBlock<TEItemOutputPort> {

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
        setTextureName("modularmachineryOverlay/base_modularports");
    }

    public static BlockItemOutputPort create() {
        return new BlockItemOutputPort();
    }

    @Override
    public String getOverlayPrefix() {
        return "overlay_itemoutput_";
    }

    @Override
    protected Class<? extends AbstractPortItemBlock> getItemBlockClass() {
        return ItemBlockItemOutputPort.class;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        dropStacks(world, x, y, z);
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
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

    public static class ItemBlockItemOutputPort extends AbstractPortItemBlock {

        public ItemBlockItemOutputPort(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            super.addInformation(stack, player, list, flag);
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
