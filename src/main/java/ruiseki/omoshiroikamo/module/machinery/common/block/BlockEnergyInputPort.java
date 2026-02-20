package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.block.ISidedIO;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.core.item.ItemWrench;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.core.tileentity.AbstractEnergyTE;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPortT1;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPortT2;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPortT3;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPortT4;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPortT5;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPortT6;

// TODO: Add wireless energy input
public class BlockEnergyInputPort extends AbstractPortBlock<TEEnergyInputPort> {

    protected BlockEnergyInputPort() {
        super(
            ModObject.blockModularEnergyInput.unlocalisedName,
            TEEnergyInputPortT1.class,
            TEEnergyInputPortT2.class,
            TEEnergyInputPortT3.class,
            TEEnergyInputPortT4.class,
            TEEnergyInputPortT5.class,
            TEEnergyInputPortT6.class);
        setHardness(5.0F);
        setResistance(10.0F);
        setTextureName("modularmachineryOverlay/base_modularports");
    }

    public static BlockEnergyInputPort create() {
        return new BlockEnergyInputPort();
    }

    @Override
    public String getOverlayPrefix() {
        return "overlay_energyinput_";
    }

    @Override
    public void registerPortOverlays(IIconRegister reg) {
        for (int i = 1; i <= 6; i++) {
            IconRegistry.addIcon(
                getOverlayPrefix() + i,
                reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/" + getOverlayPrefix() + i));
        }
        IconRegistry.addIcon(
            getOverlayPrefix() + "disabled",
            reg.registerIcon(LibResources.PREFIX_MOD + "modular_machine_casing"));
    }

    @Override
    protected Class<? extends AbstractPortItemBlock> getItemBlockClass() {
        return ItemBlockEnergyInputPort.class;
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

    public static class ItemBlockEnergyInputPort extends AbstractPortItemBlock {

        public ItemBlockEnergyInputPort(Block block) {
            super(block);
        }

        @Override
        public IIcon getOverlayIcon(int tier) {
            return IconRegistry.getIcon("overlay_energyinput_" + tier);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
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
        return Direction.INPUT;
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
}
