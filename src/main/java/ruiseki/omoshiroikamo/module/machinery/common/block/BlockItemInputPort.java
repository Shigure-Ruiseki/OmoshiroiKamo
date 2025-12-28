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
import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.core.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractTieredBlock;
import ruiseki.omoshiroikamo.module.machinery.common.tile.itemInput.AbstractItemInputPortTE;
import ruiseki.omoshiroikamo.module.machinery.common.tile.itemInput.TEItemInputPortT1;
import ruiseki.omoshiroikamo.module.machinery.common.tile.itemInput.TEItemInputPortT2;
import ruiseki.omoshiroikamo.module.machinery.common.tile.itemInput.TEItemInputPortT3;
import ruiseki.omoshiroikamo.module.machinery.common.tile.itemInput.TEItemInputPortT4;
import ruiseki.omoshiroikamo.module.machinery.common.tile.itemInput.TEItemInputPortT5;
import ruiseki.omoshiroikamo.module.machinery.common.tile.itemInput.TEItemInputPortT6;
import ruiseki.omoshiroikamo.module.machinery.common.tile.itemInput.TEItemInputPortT7;

/**
 * Item Input Port - accepts items for machine processing.
 * Can be placed at IO slot positions in machine structures.
 * Uses JSON model with base + overlay textures via GTNHLib.
 *
 * TODO List:
 * - Implement GUI for viewing/managing stored items
 * - Add filter support for specific item types (should filter with pipe)
 * - Implement BlockColor tinting for machine color customization
 * - Add animation/particle effects when receiving items
 */
public class BlockItemInputPort extends AbstractTieredBlock<AbstractItemInputPortTE> {

    protected BlockItemInputPort() {
        super(
            ModObject.blockModularItemInput.unlocalisedName,
            TEItemInputPortT1.class,
            TEItemInputPortT2.class,
            TEItemInputPortT3.class,
            TEItemInputPortT4.class,
            TEItemInputPortT5.class,
            TEItemInputPortT6.class,
            TEItemInputPortT7.class);
        setHardness(5.0F);
        setResistance(10.0F);
    }

    public static BlockItemInputPort create() {
        return new BlockItemInputPort();
    }

    @Override
    public void registerBlockColor() {
        // TODO: Add Tier Color
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockInputPort.class;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(itemIn, 1, 0));
        list.add(new ItemStack(itemIn, 1, 1));
        list.add(new ItemStack(itemIn, 1, 2));
        list.add(new ItemStack(itemIn, 1, 3));
        list.add(new ItemStack(itemIn, 1, 4));
        list.add(new ItemStack(itemIn, 1, 5));
        list.add(new ItemStack(itemIn, 1, 6));
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {}

    @Override
    public String getTextureName() {
        return "modular_machine_casing";
    }

    @Override
    public int getRenderType() {
        return JSON_ISBRH_ID;
    }

    @Override
    public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
        // TODO: Display current item count and types in slots
        // TODO: Show filter status if enabled
        // TODO: Show connected machine name if part of structure
    }

    public static class ItemBlockInputPort extends ItemBlockOK {

        public ItemBlockInputPort(Block block) {
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
