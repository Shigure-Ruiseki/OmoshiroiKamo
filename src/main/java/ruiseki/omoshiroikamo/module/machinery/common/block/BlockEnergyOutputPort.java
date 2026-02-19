package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.block.ISidedIO;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractEnergyTE;
import ruiseki.omoshiroikamo.core.common.item.ItemWrench;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.output.TEEnergyOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.output.TEEnergyOutputPortT1;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.output.TEEnergyOutputPortT2;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.output.TEEnergyOutputPortT3;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.output.TEEnergyOutputPortT4;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.output.TEEnergyOutputPortT5;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.output.TEEnergyOutputPortT6;

// TODO: Add wireless energy output
public class BlockEnergyOutputPort extends AbstractPortBlock<TEEnergyOutputPort> {

    protected BlockEnergyOutputPort() {
        super(
            ModObject.blockModularEnergyOutput.unlocalisedName,
            TEEnergyOutputPortT1.class,
            TEEnergyOutputPortT2.class,
            TEEnergyOutputPortT3.class,
            TEEnergyOutputPortT4.class,
            TEEnergyOutputPortT5.class,
            TEEnergyOutputPortT6.class);
        setHardness(5.0F);
        setResistance(10.0F);
        setTextureName("modularmachineryOverlay/base_modularports");
    }

    public static BlockEnergyOutputPort create() {
        return new BlockEnergyOutputPort();
    }

    @Override
    public void registerPortOverlays(IIconRegister reg) {
        IconRegistry.addIcon(
            "overlay_energyoutput_1",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_energyoutput_1"));
        IconRegistry.addIcon(
            "overlay_energyoutput_2",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_energyoutput_2"));
        IconRegistry.addIcon(
            "overlay_energyoutput_3",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_energyoutput_3"));
        IconRegistry.addIcon(
            "overlay_energyoutput_4",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_energyoutput_4"));
        IconRegistry.addIcon(
            "overlay_energyoutput_5",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_energyoutput_5"));
        IconRegistry.addIcon(
            "overlay_energyoutput_6",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_energyoutput_6"));
        IconRegistry.addIcon(
            "overlay_energyoutput_disabled",
            reg.registerIcon(LibResources.PREFIX_MOD + "modular_machine_casing"));
    }

    @Override
    protected Class<? extends AbstractPortItemBlock> getItemBlockClass() {
        return ItemBlockEnergyOutputPort.class;
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
    public void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        TileEntity te = accessor.getTileEntity();
        if (te instanceof AbstractEnergyTE energyTE) {
            tooltip.add(WailaUtils.getEnergyTransfer(energyTE));
        }
        if (te instanceof ISidedIO io) {
            Vec3 hit = WailaUtils.getLocalHit(accessor);
            if (hit == null) return;
            ForgeDirection side = ItemWrench
                .getClickedSide(accessor.getSide(), (float) hit.xCoord, (float) hit.yCoord, (float) hit.zCoord);
            tooltip.add(WailaUtils.getSideIOTooltip(io, side));
        }
    }

    @Override
    public String getOverlayPrefix() {
        return "overlay_energyoutput_";
    }

    @Override
    protected void addCapacityTooltip(List<String> list, int tier) {
        list.add(
            LibMisc.LANG.localize(
                "tooltip.machinery.capacity",
                String.format("%,d", MachineryConfig.getEnergyPortCapacity(tier)) + " RF"));
    }

    @Override
    protected void addTransferTooltip(List<String> list, int tier) {
        list.add(
            LibMisc.LANG
                .localize("gui.energy_transfer", String.format("%,d", MachineryConfig.getEnergyPortTransfer(tier))));
    }

    public static class ItemBlockEnergyOutputPort extends AbstractPortItemBlock {

        public ItemBlockEnergyOutputPort(Block block) {
            super(block, block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            super.addInformation(stack, player, list, flag);
        }
    }

    @Override
    public Type getPortType() {
        return Type.ENERGY;
    }

    @Override
    public Direction getPortDirection() {
        return Direction.OUTPUT;
    }
}
