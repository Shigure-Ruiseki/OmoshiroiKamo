package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.IFluidHandler;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.input.TEFluidInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.input.TEFluidInputPortT1;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.input.TEFluidInputPortT2;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.input.TEFluidInputPortT3;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.input.TEFluidInputPortT4;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.input.TEFluidInputPortT5;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.input.TEFluidInputPortT6;

public class BlockFluidInputPort extends AbstractPortBlock<TEFluidInputPort> {

    protected BlockFluidInputPort() {
        super(
            ModObject.blockModularFluidInput.unlocalisedName,
            TEFluidInputPortT1.class,
            TEFluidInputPortT2.class,
            TEFluidInputPortT3.class,
            TEFluidInputPortT4.class,
            TEFluidInputPortT5.class,
            TEFluidInputPortT6.class);
        setHardness(5.0F);
        setResistance(10.0F);
        setTextureName("modularmachineryOverlay/base_modularports");
    }

    public static BlockFluidInputPort create() {
        return new BlockFluidInputPort();
    }

    @Override
    public String getOverlayPrefix() {
        return "overlay_fluidinput_";
    }

    @Override
    protected Class<? extends AbstractPortItemBlock> getItemBlockClass() {
        return ItemBlockFluidInputPort.class;
    }

    @Override
    public void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        super.getWailaInfo(tooltip, itemStack, accessor, config);
        TileEntity te = accessor.getTileEntity();
        if (te instanceof IFluidHandler) {
            tooltip.addAll(WailaUtils.getFluidTooltip((IFluidHandler) te));
        }
    }

    @Override
    protected void addCapacityTooltip(List<String> list, int tier) {
        list.add(
            LibMisc.LANG.localize(
                "tooltip.machinery.capacity",
                String.format("%,d", MachineryConfig.getFluidPortCapacity(tier)) + " mB"));
    }

    public static class ItemBlockFluidInputPort extends AbstractPortItemBlock {

        public ItemBlockFluidInputPort(Block block) {
            super(block, block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            super.addInformation(stack, player, list, flag);
        }
    }

    @Override
    public Type getPortType() {
        return Type.FLUID;
    }

    @Override
    public Direction getPortDirection() {
        return Direction.INPUT;
    }
}
