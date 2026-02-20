package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.gas.IGasHandler;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.output.TEGasOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.output.TEGasOutputPortT1;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.output.TEGasOutputPortT2;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.output.TEGasOutputPortT3;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.output.TEGasOutputPortT4;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.output.TEGasOutputPortT5;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.output.TEGasOutputPortT6;

public class BlockGasOutputPort extends AbstractPortBlock<TEGasOutputPort> {

    protected BlockGasOutputPort() {
        super(
            ModObject.blockModularGasOutput.unlocalisedName,
            TEGasOutputPortT1.class,
            TEGasOutputPortT2.class,
            TEGasOutputPortT3.class,
            TEGasOutputPortT4.class,
            TEGasOutputPortT5.class,
            TEGasOutputPortT6.class);
        setHardness(5.0F);
        setResistance(10.0F);
        setTextureName("modularmachineryOverlay/base_modularports");
    }

    public static BlockGasOutputPort create() {
        return new BlockGasOutputPort();
    }

    @Override
    public String getOverlayPrefix() {
        return "overlay_gasoutput_";
    }

    @Override
    protected Class<? extends AbstractPortItemBlock> getItemBlockClass() {
        return ItemBlockGasOutputPort.class;
    }

    @Override
    public void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaInfo(tooltip, itemStack, accessor, config);
        TileEntity tileEntity = accessor.getTileEntity();
        if (tileEntity instanceof IGasHandler handler) {
            tooltip.addAll(WailaUtils.getGasTooltip(handler));
        }
    }

    @Override
    public Type getPortType() {
        return Type.GAS;
    }

    @Override
    public Direction getPortDirection() {
        return Direction.OUTPUT;
    }

    @Override
    protected void addCapacityTooltip(List<String> list, int tier) {
        list.add(
            LibMisc.LANG.localize(
                "tooltip.machinery.capacity",
                String.format("%,d", MachineryConfig.getGasPortCapacity(tier)) + " mB"));
    }

    public static class ItemBlockGasOutputPort extends AbstractPortItemBlock {

        public ItemBlockGasOutputPort(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            super.addInformation(stack, player, list, flag);
        }
    }
}
