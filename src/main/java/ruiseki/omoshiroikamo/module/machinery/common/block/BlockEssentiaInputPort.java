package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.essentia.input.TEEssentiaInputPort;

// TODO: Add texture

public class BlockEssentiaInputPort extends AbstractPortBlock<TEEssentiaInputPort> {

    protected BlockEssentiaInputPort() {
        super(ModObject.blockModularEssentiaInput.unlocalisedName, TEEssentiaInputPort.class);
        setHardness(5.0F);
        setResistance(10.0F);
        setTextureName("modularmachineryOverlay/base_modularports");
    }

    @Override
    public boolean canRenderInPass(int pass) {
        super.canRenderInPass(pass); // keep render-pass thread local in sync
        return pass == 0; // suppress block render pass 1; TESR handles overlay
    }

    public static BlockEssentiaInputPort create() {
        return new BlockEssentiaInputPort();
    }

    @Override
    public String getOverlayPrefix() {
        return "overlay_essentiainput_";
    }

    @Override
    protected Class<? extends AbstractPortItemBlock> getItemBlockClass() {
        return ItemBlockEssentiaInputPort.class;
    }

    @Override
    public Type getPortType() {
        return Type.ESSENTIA;
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
                String.format("%,d", MachineryConfig.essentiaPortCapacity) + " Essentia / type"));
    }

    public static class ItemBlockEssentiaInputPort extends AbstractPortItemBlock {

        public ItemBlockEssentiaInputPort(Block block) {
            super(block, block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            super.addInformation(stack, player, list, flag);
        }
    }
}
