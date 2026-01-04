package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.modular.IModularBlock;
import ruiseki.omoshiroikamo.core.client.util.IconRegistry;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.machinery.common.item.AbstractPortItemBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.essentia.output.TEEssentiaOutputPort;

/**
 * Essentia Output Port block - provides Essentia to Infusion Altar and Tubes.
 */
public class BlockEssentiaOutputPort extends AbstractPortBlock<TEEssentiaOutputPort> implements IModularBlock {

    protected BlockEssentiaOutputPort() {
        super(ModObject.blockModularEssentiaOutput.unlocalisedName, TEEssentiaOutputPort.class);
        setHardness(5.0F);
        setResistance(10.0F);
        setTextureName("modularmachineryOverlay/base_modularports");
    }

    public static BlockEssentiaOutputPort create() {
        return new BlockEssentiaOutputPort();
    }

    @Override
    public void registerPortOverlays(IIconRegister reg) {
        IconRegistry.addIcon(
            "overlay_essentiaoutput_1",
            reg.registerIcon(LibResources.PREFIX_MOD + "modularmachineryOverlay/overlay_essentiaoutput_1"));
    }

    @Override
    protected Class<? extends AbstractPortItemBlock> getItemBlockClass() {
        return ItemBlockEssentiaOutputPort.class;
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
        return Direction.OUTPUT;
    }

    public static class ItemBlockEssentiaOutputPort extends AbstractPortItemBlock {

        public ItemBlockEssentiaOutputPort(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
        }

        @Override
        public IIcon getOverlayIcon(int tier) {
            return IconRegistry.getIcon("overlay_essentiaoutput_" + tier);
        }
    }
}
