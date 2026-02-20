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
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.input.TEGasInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.input.TEGasInputPortT1;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.input.TEGasInputPortT2;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.input.TEGasInputPortT3;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.input.TEGasInputPortT4;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.input.TEGasInputPortT5;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.input.TEGasInputPortT6;

public class BlockGasInputPort extends AbstractPortBlock<TEGasInputPort> {

    protected BlockGasInputPort() {
        super(
            ModObject.blockModularGasInput.unlocalisedName,
            TEGasInputPortT1.class,
            TEGasInputPortT2.class,
            TEGasInputPortT3.class,
            TEGasInputPortT4.class,
            TEGasInputPortT5.class,
            TEGasInputPortT6.class);
        setHardness(5.0F);
        setResistance(10.0F);
        setTextureName("modularmachineryOverlay/base_modularports");
    }

    public static BlockGasInputPort create() {
        return new BlockGasInputPort();
    }

    @Override
    public String getOverlayPrefix() {
        return "overlay_gasinput_";
    }

    @Override
    protected Class<? extends AbstractPortItemBlock> getItemBlockClass() {
        return ItemBlockGasInputPort.class;
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
        return Direction.INPUT;
    }

    @Override
    protected void addCapacityTooltip(List<String> list, int tier) {
        list.add(
            LibMisc.LANG.localize(
                "tooltip.machinery.capacity",
                String.format("%,d", MachineryConfig.getGasPortCapacity(tier)) + " mB"));
    }

    public static class ItemBlockGasInputPort extends AbstractPortItemBlock {

        public ItemBlockGasInputPort(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            super.addInformation(stack, player, list, flag);
        }
    }
}
