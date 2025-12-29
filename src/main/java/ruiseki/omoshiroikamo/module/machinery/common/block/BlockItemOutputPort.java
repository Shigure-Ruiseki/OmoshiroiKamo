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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.core.common.block.TileEntityOK;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractTieredBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.output.TEItemOutputPort;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.output.TEItemOutputPortT1;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.output.TEItemOutputPortT2;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.output.TEItemOutputPortT3;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.output.TEItemOutputPortT4;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.output.TEItemOutputPortT5;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.output.TEItemOutputPortT6;

/**
 * Item Output Port - outputs items from machine processing.
 * Can be placed at IO slot positions in machine structures.
 * Uses JSON model with base + overlay textures via GTNHLib.
 * TODO List:
 * - Implement BlockColor tinting for machine color customization
 * - Add animation/particle effects when outputting items
 * - Support comparator output for automation
 */
public class BlockItemOutputPort extends AbstractTieredBlock<TEItemOutputPort> {

    protected BlockItemOutputPort() {
        super(
            ModObject.blockModularItemOutput.unlocalisedName,
            TEItemOutputPortT1.class,
            TEItemOutputPortT2.class,
            TEItemOutputPortT3.class,
            TEItemOutputPortT4.class,
            TEItemOutputPortT5.class,
            TEItemOutputPortT6.class);
        setHardness(5.0F);
        setResistance(10.0F);
    }

    public static BlockItemOutputPort create() {
        return new BlockItemOutputPort();
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
    protected Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockItemOutputPort.class;
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
    public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
        // TODO: Display current output item count
        // TODO: Show auto-push status
    }

    public static class ItemBlockItemOutputPort extends ItemBlockOK {

        public ItemBlockItemOutputPort(Block block) {
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
}
