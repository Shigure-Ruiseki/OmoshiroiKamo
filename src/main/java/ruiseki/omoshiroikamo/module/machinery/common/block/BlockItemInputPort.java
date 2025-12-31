package ruiseki.omoshiroikamo.module.machinery.common.block;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.modular.IModularBlock;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.core.common.block.TileEntityOK;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractTieredBlock;
import ruiseki.omoshiroikamo.core.integration.waila.WailaUtils;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.input.TEItemInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.input.TEItemInputPortT1;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.input.TEItemInputPortT2;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.input.TEItemInputPortT3;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.input.TEItemInputPortT4;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.input.TEItemInputPortT5;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.input.TEItemInputPortT6;

/**
 * Item Input Port - accepts items for machine processing.
 * Can be placed at IO slot positions in machine structures.
 * Uses JSON model with base + overlay textures via GTNHLib.
 * TODO List:
 * - Add filter support for specific item types (should filter with pipe)
 * - Implement BlockColor tinting for machine color customization
 * - Add animation/particle effects when receiving items
 */
public class BlockItemInputPort extends AbstractTieredBlock<TEItemInputPort> implements IModularBlock {

    protected BlockItemInputPort() {
        super(
            ModObject.blockModularItemInput.unlocalisedName,
            TEItemInputPortT1.class,
            TEItemInputPortT2.class,
            TEItemInputPortT3.class,
            TEItemInputPortT4.class,
            TEItemInputPortT5.class,
            TEItemInputPortT6.class);
        setHardness(5.0F);
        setResistance(10.0F);
    }

    public static BlockItemInputPort create() {
        return new BlockItemInputPort();
    }

    @Override
    public String getTextureName() {
        return "modular_machine_casing";
    }

    @Override
    public int colorMultiplier(@Nullable IBlockAccess world, int x, int y, int z, int tintIndex) {
        // TODO: Add Tier Color
        return -1;
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockItemInputPort.class;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(itemIn, 1, 0));
        list.add(new ItemStack(itemIn, 1, 1));
        list.add(new ItemStack(itemIn, 1, 2));
        list.add(new ItemStack(itemIn, 1, 3));
        list.add(new ItemStack(itemIn, 1, 4));
        list.add(new ItemStack(itemIn, 1, 5));
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {}

    @Override
    protected void processDrop(World world, int x, int y, int z, TileEntityOK te, ItemStack stack) {}

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        dropStacks(world, x, y, z);
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public int getRenderType() {
        return JSON_ISBRH_ID;
    }

    @Override
    public void getWailaInfo(List<String> tooltip, ItemStack itemStack, IWailaDataAccessor accessor,
        IWailaConfigHandler config) {
        // TODO: Show filter status if enabled
        TileEntity tileEntity = accessor.getTileEntity();
        if (tileEntity instanceof IInventory handler) {
            tooltip.add(WailaUtils.getInventoryTooltip(handler));
        }
    }

    public static class ItemBlockItemInputPort extends ItemBlockOK {

        public ItemBlockItemInputPort(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
        }

        @Override
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            // TODO: Add tooltips
        }
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.ITEM;
    }

    @Override
    public IPortType.Direction getPortDirection() {
        return IPortType.Direction.INPUT;
    }
}
