package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

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
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.output.TEFluidOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.output.TEFluidOutputPortT1;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.output.TEFluidOutputPortT2;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.output.TEFluidOutputPortT3;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.output.TEFluidOutputPortT4;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.output.TEFluidOutputPortT5;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.output.TEFluidOutputPortT6;

/**
 * Mana Output Port - accepts mana for machine processing.
 * Can be placed at IO slot positions in machine structures.
 * Uses JSON model with base + overlay textures via GTNHLib.
 *
 * TODO List:
 * - Add visual indicator for mana level (texture animation or overlay)
 * - Implement BlockColor tinting for machine color customization
 * - Add animation/particle effects when receiving mana
 */
public class BlockFluidOutputPort extends AbstractPortBlock<TEFluidOutputPort> implements IModularBlock {

    protected BlockFluidOutputPort() {
        super(
            ModObject.blockModularFluidOutput.unlocalisedName,
            TEFluidOutputPortT1.class,
            TEFluidOutputPortT2.class,
            TEFluidOutputPortT3.class,
            TEFluidOutputPortT4.class,
            TEFluidOutputPortT5.class,
            TEFluidOutputPortT6.class);
        setHardness(5.0F);
        setResistance(10.0F);
    }

    public static BlockFluidOutputPort create() {
        return new BlockFluidOutputPort();
    }

    @Override
    public String getTextureName() {
        return "modular_machine_casing";
    }

    @Override
    public void registerPortOverlays(IIconRegister reg) {
        IconRegistry.addIcon(
            "overlay_fluidoutput_1",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_fluidoutput_1"));
        IconRegistry.addIcon(
            "overlay_fluidoutput_2",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_fluidoutput_2"));
        IconRegistry.addIcon(
            "overlay_fluidoutput_3",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_fluidoutput_3"));
        IconRegistry.addIcon(
            "overlay_fluidoutput_4",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_fluidoutput_4"));
        IconRegistry.addIcon(
            "overlay_fluidoutput_5",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_fluidoutput_5"));
        IconRegistry.addIcon(
            "overlay_fluidoutput_6",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_fluidoutput_6"));
    }

    @Override
    protected Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockFluidOutputPort.class;
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
        TileEntity tileEntity = accessor.getTileEntity();
        if (!config.getConfig("IFluidHandler") && tileEntity instanceof IFluidHandler handler) {
            tooltip.addAll(WailaUtils.getFluidTooltip(handler));
        }
        if (tileEntity instanceof ISidedIO io) {
            Vec3 hit = WailaUtils.getLocalHit(accessor);
            if (hit == null) return;
            ForgeDirection side = ItemWrench
                .getClickedSide(accessor.getSide(), (float) hit.xCoord, (float) hit.yCoord, (float) hit.zCoord);
            tooltip.add(WailaUtils.getSideIOTooltip(io, side));
        }
    }

    public static class ItemBlockFluidOutputPort extends AbstractPortItemBlock {

        public ItemBlockFluidOutputPort(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
        }

        @Override
        public IIcon getOverlayIcon(int tier) {
            return IconRegistry.getIcon("overlay_fluidoutput_" + tier);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            // TODO: Add tooltips
        }
    }

    @Override
    public Type getPortType() {
        return Type.FLUID;
    }

    @Override
    public Direction getPortDirection() {
        return Direction.OUTPUT;
    }
}
