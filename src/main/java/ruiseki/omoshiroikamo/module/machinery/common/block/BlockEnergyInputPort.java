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
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractEnergyTE;
import ruiseki.omoshiroikamo.core.common.item.ItemWrench;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPortT1;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPortT2;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPortT3;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPortT4;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPortT5;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPortT6;

/**
 * Energy Input Port - accepts energy (RF) for machine processing.
 * Can be placed at IO slot positions in machine structures.
 * Uses JSON model with base + overlay textures via GTNHLib.
 *
 * TODO List:
 * - Add visual indicator for energy level (texture animation or overlay)
 * - Implement BlockColor tinting for machine color customization
 * - Add Tesla coil-style wireless energy input
 */
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
    public void registerPortOverlays(IIconRegister reg) {
        IconRegistry.addIcon(
            "overlay_energyinput_1",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_energyinput_1"));
        IconRegistry.addIcon(
            "overlay_energyinput_2",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_energyinput_2"));
        IconRegistry.addIcon(
            "overlay_energyinput_3",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_energyinput_3"));
        IconRegistry.addIcon(
            "overlay_energyinput_4",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_energyinput_4"));
        IconRegistry.addIcon(
            "overlay_energyinput_5",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_energyinput_5"));
        IconRegistry.addIcon(
            "overlay_energyinput_6",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_energyinput_6"));
        IconRegistry.addIcon(
            "overlay_energyinput_disabled",
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
            super(block, block);
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
            // TODO: Add tooltips
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
}
