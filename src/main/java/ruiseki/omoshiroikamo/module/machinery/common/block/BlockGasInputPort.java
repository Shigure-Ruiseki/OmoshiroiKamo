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

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.gas.IGasHandler;
import ruiseki.omoshiroikamo.api.io.ISidedIO;
import ruiseki.omoshiroikamo.api.modular.IModularBlock;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.common.item.ItemWrench;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.input.TEGasInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.input.TEGasInputPortT1;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.input.TEGasInputPortT2;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.input.TEGasInputPortT3;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.input.TEGasInputPortT4;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.input.TEGasInputPortT5;
import ruiseki.omoshiroikamo.module.machinery.common.tile.gas.input.TEGasInputPortT6;

/**
 * Mana Input Port - accepts mana for machine processing.
 * Can be placed at IO slot positions in machine structures.
 * Uses JSON model with base + overlay textures via GTNHLib.
 *
 * TODO List:
 * - Add visual indicator for mana level (texture animation or overlay)
 * - Implement BlockColor tinting for machine color customization
 * - Add animation/particle effects when receiving gas
 */
public class BlockGasInputPort extends AbstractPortBlock<TEGasInputPort> implements IModularBlock {

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
    }

    public static BlockGasInputPort create() {
        return new BlockGasInputPort();
    }

    @Override
    public String getTextureName() {
        return "modular_machine_casing";
    }

    @Override
    public void registerPortOverlays(IIconRegister reg) {
        IconRegistry.addIcon(
            "overlay_gasinput_1",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_gasinput_1"));
        IconRegistry.addIcon(
            "overlay_gasinput_2",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_gasinput_2"));
        IconRegistry.addIcon(
            "overlay_gasinput_3",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_gasinput_3"));
        IconRegistry.addIcon(
            "overlay_gasinput_4",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_gasinput_4"));
        IconRegistry.addIcon(
            "overlay_gasinput_5",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_gasinput_5"));
        IconRegistry.addIcon(
            "overlay_gasinput_6",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_gasinput_6"));
    }

    @Override
    protected Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockGasInputPort.class;
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
        if (tileEntity instanceof IGasHandler handler) {
            tooltip.addAll(WailaUtils.getGasTooltip(handler));
        }
        if (tileEntity instanceof ISidedIO io) {
            Vec3 hit = WailaUtils.getLocalHit(accessor);
            if (hit == null) return;
            ForgeDirection side = ItemWrench
                .getClickedSide(accessor.getSide(), (float) hit.xCoord, (float) hit.yCoord, (float) hit.zCoord);
            tooltip.add(WailaUtils.getSideIOTooltip(io, side));
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

    public static class ItemBlockGasInputPort extends AbstractPortItemBlock {

        public ItemBlockGasInputPort(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
        }

        @Override
        public IIcon getOverlayIcon(int tier) {
            return IconRegistry.getIcon("overlay_gasinput_" + tier);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            // TODO: Add tooltips
        }
    }
}
