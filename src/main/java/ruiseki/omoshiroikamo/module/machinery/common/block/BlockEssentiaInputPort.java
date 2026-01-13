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
import ruiseki.omoshiroikamo.module.machinery.common.tile.essentia.input.TEEssentiaInputPort;

/**
 * Essentia Input Port block - absorbs Essentia from nearby Thaumcraft
 * containers.
 */
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
    public void registerPortOverlays(IIconRegister reg) {
        IconRegistry.addIcon(
            "overlay_essentiainput_1",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_essentiainput_1"));
        IconRegistry
            .addIcon("overlay_port_disabled", reg.registerIcon(LibResources.PREFIX_MOD + "modular_machine_casing"));
    }

    @Override
    protected Class<? extends AbstractPortItemBlock> getItemBlockClass() {
        return ItemBlockEssentiaInputPort.class;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(itemIn, 1, 0));
    }

    @Override
    public Type getPortType() {
        return Type.ESSENTIA;
    }

    @Override
    public Direction getPortDirection() {
        return Direction.INPUT;
    }

    public static class ItemBlockEssentiaInputPort extends AbstractPortItemBlock {

        public ItemBlockEssentiaInputPort(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
        }

        @Override
        public IIcon getOverlayIcon(int tier) {
            return IconRegistry.getIcon("overlay_essentiainput_" + tier);
        }
    }
}
