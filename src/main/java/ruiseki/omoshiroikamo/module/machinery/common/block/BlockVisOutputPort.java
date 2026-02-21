package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.vis.output.TEVisOutputPort;

// TODO: Add texture
public class BlockVisOutputPort extends AbstractPortBlock<TEVisOutputPort> {

    protected BlockVisOutputPort() {
        super(ModObject.blockModularVisOutput.unlocalisedName, TEVisOutputPort.class);
        setHardness(5.0F);
        setResistance(10.0F);
        setTextureName("modularmachineryOverlay/base_modularports");
    }

    public static BlockVisOutputPort create() {
        return new BlockVisOutputPort();
    }

    @Override
    public String getOverlayPrefix() {
        return "overlay_visoutput_";
    }

    @Override
    protected Class<? extends AbstractPortItemBlock> getItemBlockClass() {
        return ItemBlockVisOutputPort.class;
    }

    @Override
    public Type getPortType() {
        return Type.VIS;
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
                String.format("%,d", MachineryConfig.visPortCapacity) + " Vis / "
                    + String.format("%,d", MachineryConfig.visPortCapacity * 10)
                    + " cV"));
    }

    public static class ItemBlockVisOutputPort extends AbstractPortItemBlock {

        public ItemBlockVisOutputPort(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            super.addInformation(stack, player, list, flag);
        }
    }
}
