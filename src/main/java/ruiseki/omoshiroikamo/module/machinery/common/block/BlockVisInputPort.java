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
import ruiseki.omoshiroikamo.module.machinery.common.tile.vis.input.TEVisInputPort;

/**
 * Vis Input Port block - receives Vis from Thaumcraft Vis Relay network.
 */
public class BlockVisInputPort extends AbstractTieredBlock<TEVisInputPort> implements IModularBlock {

    protected BlockVisInputPort() {
        super(ModObject.blockModularVisInput.unlocalisedName, TEVisInputPort.class);
        setHardness(5.0F);
        setResistance(10.0F);
    }

    public static BlockVisInputPort create() {
        return new BlockVisInputPort();
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
        return ItemBlockVisInputPort.class;
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
        return IPortType.Type.VIS;
    }

    @Override
    public IPortType.Direction getPortDirection() {
        return IPortType.Direction.INPUT;
    }

    public static class ItemBlockVisInputPort extends ItemBlockOK {

        public ItemBlockVisInputPort(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            return super.getUnlocalizedName();
        }
    }
}
