package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.vis.input.TEVisInputPort;

/**
 * Vis Input Port block - receives Vis from Thaumcraft Vis Relay network.
 */
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
    public String getTextureName() {
        return "modular_machine_casing";
    }

    @Override
    public void registerPortOverlays(IIconRegister reg) {
        IconRegistry.addIcon(
            "overlay_visoutput_1",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_visoutput_1"));
        IconRegistry
            .addIcon("overlay_port_disabled", reg.registerIcon(LibResources.PREFIX_MOD + "modular_machine_casing"));
    }

    @Override
    protected Class<? extends AbstractPortItemBlock> getItemBlockClass() {
        return ItemBlockVisInputPort.class;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(itemIn, 1, 0));
    }

    @Override
    public Type getPortType() {
        return Type.VIS;
    }

    @Override
    public Direction getPortDirection() {
        return Direction.INPUT;
    }

    public static class ItemBlockVisInputPort extends AbstractPortItemBlock {

        public ItemBlockVisInputPort(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
        }

        @Override
        public IIcon getOverlayIcon(int tier) {
            return IconRegistry.getIcon("overlay_visinput_" + tier);
        }
    }
}
