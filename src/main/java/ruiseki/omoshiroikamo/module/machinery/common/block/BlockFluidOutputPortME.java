package ruiseki.omoshiroikamo.module.machinery.common.block;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.modular.IModularBlock;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.core.common.block.TileEntityOK;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractTieredBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.fluid.output.TEFluidOutputPortME;

/**
 * ME Fluid Output Port - outputs fluids directly to AE2 ME Network.
 * Requires Applied Energistics 2 to be loaded.
 * Uses JSON model with base + overlay textures via GTNHLib.
 */
public class BlockFluidOutputPortME extends AbstractTieredBlock<TEFluidOutputPortME> implements IModularBlock {

    protected BlockFluidOutputPortME() {
        super(ModObject.blockModularFluidOutputME.unlocalisedName, TEFluidOutputPortME.class);
        setHardness(5.0F);
        setResistance(10.0F);
    }

    public static BlockFluidOutputPortME create() {
        return new BlockFluidOutputPortME();
    }

    @Override
    public String getTextureName() {
        return "modular_machine_casing";
    }

    @Override
    public int colorMultiplier(@Nullable IBlockAccess world, int x, int y, int z, int tintIndex) {
        return -1; // No tint
    }

    @Override
    protected Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockFluidOutputPortME.class;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        // Only one tier for ME version
        list.add(new ItemStack(itemIn, 1, 0));
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, player, stack);
    }

    @Override
    protected void processDrop(World world, int x, int y, int z, TileEntityOK te, ItemStack stack) {
        // No special drop handling
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        // TODO: Flush remaining cached fluids before breaking
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public int getRenderType() {
        return JSON_ISBRH_ID;
    }

    @Override
    public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TEFluidOutputPortME meTile) {
            if (meTile.isActive()) {
                tooltip.add("§a" + StatCollector.translateToLocal("waila.me.online"));
            } else if (meTile.isPowered()) {
                tooltip.add("§c" + StatCollector.translateToLocal("waila.me.no_channel"));
            } else {
                tooltip.add("§c" + StatCollector.translateToLocal("waila.me.offline"));
            }
        }
    }

    public static class ItemBlockFluidOutputPortME extends ItemBlockOK {

        public ItemBlockFluidOutputPortME(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            return super.getUnlocalizedName() + ".me";
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            list.add("§7" + StatCollector.translateToLocal("tooltip.me_fluid_output.desc"));
        }
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.FLUID;
    }

    @Override
    public IPortType.Direction getPortDirection() {
        return IPortType.Direction.OUTPUT;
    }
}
