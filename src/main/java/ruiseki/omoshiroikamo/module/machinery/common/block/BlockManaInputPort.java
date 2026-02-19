package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
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
import ruiseki.omoshiroikamo.module.machinery.common.tile.mana.AbstractManaPortTE;
import ruiseki.omoshiroikamo.module.machinery.common.tile.mana.input.TEManaInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.tile.mana.input.TEManaInputPortT1;
import vazkii.botania.api.mana.IManaBlock;
import vazkii.botania.api.wand.IWandHUD;

public class BlockManaInputPort extends AbstractPortBlock<TEManaInputPort> implements IWandHUD {

    protected BlockManaInputPort() {
        super(ModObject.blockModularManaInput.unlocalisedName, TEManaInputPortT1.class);
        setHardness(5.0F);
        setResistance(10.0F);
        setTextureName("modularmachineryOverlay/base_modularports");
    }

    public static BlockManaInputPort create() {
        return new BlockManaInputPort();
    }

    @Override
    public String getOverlayPrefix() {
        return "overlay_manainput_";
    }

    @Override
    protected Class<? extends AbstractPortItemBlock> getItemBlockClass() {
        return ItemBlockManaInputPort.class;
    }

    @Override
    public void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        TileEntity tileEntity = accessor.getTileEntity();
        if (tileEntity instanceof IManaBlock handler) {
            tooltip.add(WailaUtils.getManaToolTip(handler));
        }
    }

    @Override
    public void renderHUD(Minecraft mc, ScaledResolution res, World world, int x, int y, int z) {
        ((AbstractManaPortTE) world.getTileEntity(x, y, z)).renderHUD(mc, res);
    }

    @Override
    protected void addCapacityTooltip(List<String> list, int tier) {
        list.add(
            LibMisc.LANG.localize(
                "tooltip.machinery.capacity",
                String.format("%,d", MachineryConfig.manaPortCapacity) + " Mana"));
    }

    @Override
    protected void addTransferTooltip(List<String> list, int tier) {
        list.add(
            LibMisc.LANG
                .localize("tooltip.machinery.mana_transfer", String.format("%,d", MachineryConfig.manaPortTransfer)));
    }

    public static class ItemBlockManaInputPort extends AbstractPortItemBlock {

        public ItemBlockManaInputPort(Block block) {
            super(block, block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            super.addInformation(stack, player, list, flag);
        }
    }

    @Override
    public Type getPortType() {
        return Type.MANA;
    }

    @Override
    public Direction getPortDirection() {
        return Direction.INPUT;
    }
}
