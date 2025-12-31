package ruiseki.omoshiroikamo.module.machinery.common.block;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.modular.IModularBlock;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.core.common.block.TileEntityOK;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractTieredBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.essentia.input.TEEssentiaInputPort;

/**
 * Essentia Input Port block - absorbs Essentia from nearby Thaumcraft
 * containers.
 */
public class BlockEssentiaInputPort extends AbstractTieredBlock<TEEssentiaInputPort> implements IModularBlock {

    protected BlockEssentiaInputPort() {
        super(ModObject.blockModularEssentiaInput.unlocalisedName, TEEssentiaInputPort.class);
        setHardness(5.0F);
        setResistance(10.0F);
    }

    public static BlockEssentiaInputPort create() {
        return new BlockEssentiaInputPort();
    }

    @Override
    public String getTextureName() {
        return "modular_machine_casing";
    }

    @Override
    public int colorMultiplier(@Nullable IBlockAccess world, int x, int y, int z, int tintIndex) {
        return -1;
    }

    @Override
    protected Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockEssentiaInputPort.class;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(itemIn, 1, 0));
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {}

    @Override
    protected void processDrop(World world, int x, int y, int z, TileEntityOK te, ItemStack stack) {}

    @Override
    public int getRenderType() {
        return JSON_ISBRH_ID;
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.ESSENTIA;
    }

    @Override
    public IPortType.Direction getPortDirection() {
        return IPortType.Direction.INPUT;
    }

    public static class ItemBlockEssentiaInputPort extends ItemBlockOK {

        public ItemBlockEssentiaInputPort(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            return super.getUnlocalizedName();
        }
    }
}
