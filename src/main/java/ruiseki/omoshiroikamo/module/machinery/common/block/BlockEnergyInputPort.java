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
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPort;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPortT1;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPortT2;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPortT3;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPortT4;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPortT5;
import ruiseki.omoshiroikamo.module.machinery.common.tile.energy.input.TEEnergyInputPortT6;

/**
 * Energy Input Port - accepts energy (RF) for machine processing.
 * Can be placed at IO slot positions in machine structures.
 * Uses JSON model with base + overlay textures via GTNHLib.
 *
 * TODO List:
 * - Add GUI for viewing energy level
 * - Add visual indicator for energy level (texture animation or overlay)
 * - Implement BlockColor tinting for machine color customization
 * - Add comparator output for energy monitoring
 * - Support EU (IC2) input mode (configurable)
 * - Add Tesla coil-style wireless energy input
 */
public class BlockEnergyInputPort extends AbstractTieredBlock<TEEnergyInputPort> {

    protected BlockEnergyInputPort() {
        super(
            ModObject.blockModularEnergyInput.unlocalisedName,
            TEEnergyInputPortT1.class,
            TEEnergyInputPortT2.class,
            TEEnergyInputPortT3.class,
            TEEnergyInputPortT4.class,
            TEEnergyInputPortT5.class,
            TEEnergyInputPortT6.class);
        setHardness(5.0F);
        setResistance(10.0F);
    }

    public static BlockEnergyInputPort create() {
        return new BlockEnergyInputPort();
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
        return ItemBlockEnergyOutputPort.class;
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
    public int getRenderType() {
        return JSON_ISBRH_ID;
    }

    @Override
    public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
        // TODO: Display current RF stored / max capacity
        // TODO: Show energy transfer rate
        // TODO: Show connected machine name if part of structure
    }

    public static class ItemBlockEnergyOutputPort extends ItemBlockOK {

        public ItemBlockEnergyOutputPort(Block block) {
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
