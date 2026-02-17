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
import ruiseki.omoshiroikamo.module.machinery.common.tile.essentia.input.TEEssentiaInputPortME;

/**
 * pulls Essentia from Thaumic Energistics ME network.
 * TODO: Add texture
 */
public class BlockEssentiaInputPortME extends AbstractPortBlock<TEEssentiaInputPortME> {

    protected BlockEssentiaInputPortME() {
        super(ModObject.blockModularEssentiaInputME.unlocalisedName, TEEssentiaInputPortME.class);
        setHardness(5.0F);
        setResistance(10.0F);
        setTextureName("modularmachineryOverlay/base_modularports");
    }

    public static BlockEssentiaInputPortME create() {
        return new BlockEssentiaInputPortME();
    }

    @Override
    public String getOverlayPrefix() {
        return "overlay_essentiainput_me";
    }

    @Override
    public void registerPortOverlays(IIconRegister reg) {
        IconRegistry.addIcon(
            "overlay_essentiainput_me",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_essentiainput_me"));
        IconRegistry
            .addIcon("overlay_port_disabled", reg.registerIcon(LibResources.PREFIX_MOD + "modular_machine_casing"));
    }

    @Override
    protected Class<? extends AbstractPortItemBlock> getItemBlockClass() {
        return ItemBlockEssentiaInputPortME.class;
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

    public static class ItemBlockEssentiaInputPortME extends AbstractPortItemBlock {

        public ItemBlockEssentiaInputPortME(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            return super.getUnlocalizedName() + ".me";
        }

        @Override
        public IIcon getOverlayIcon(int tier) {
            return IconRegistry.getIcon("overlay_essentiainput_me");
        }
    }
}
