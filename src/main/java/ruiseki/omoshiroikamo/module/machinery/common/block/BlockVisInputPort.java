package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.vis.input.TEVisInputPort;

// TODO: Add texture
public class BlockVisInputPort extends AbstractPortBlock<TEVisInputPort> {

    protected BlockVisInputPort() {
        super(ModObject.blockModularVisInput.unlocalisedName, TEVisInputPort.class);
        setHardness(5.0F);
        setResistance(10.0F);
        setTextureName("modularmachineryOverlay/base_modularports");
    }

    public static BlockVisInputPort create() {
        return new BlockVisInputPort();
    }

    @Override
    public String getOverlayPrefix() {
        return "overlay_visinput_";
    }

    @Override
    protected Class<? extends AbstractPortItemBlock> getItemBlockClass() {
        return ItemBlockVisInputPort.class;
    }

    @Override
    public Type getPortType() {
        return Type.VIS;
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
                String.format("%,d", MachineryConfig.visPortCapacity) + " Vis / "
                    + String.format("%,d", MachineryConfig.visPortCapacity * 10)
                    + " cV"));
    }

    public static class ItemBlockVisInputPort extends AbstractPortItemBlock {

        public ItemBlockVisInputPort(Block block) {
            super(block);
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            super.addInformation(stack, player, list, flag);
        }
    }
}
