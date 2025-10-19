package ruiseki.omoshiroikamo.common;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.common.fluid.FluidMaterialRegister;
import ruiseki.omoshiroikamo.common.fluid.FluidRegister;
import ruiseki.omoshiroikamo.common.init.ModBlocks;
import ruiseki.omoshiroikamo.common.init.ModItems;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

public class OKCreativeTab extends CreativeTabs {

    public static final CreativeTabs INSTANCE = new OKCreativeTab();
    List<ItemStack> list;
    private static final List<ItemStack> externalStacks = new ArrayList<>();

    public OKCreativeTab() {
        super(LibMisc.MOD_ID);
    }

    @Override
    public ItemStack getIconItemStack() {
        return new ItemStack(ModItems.MATERIAL.get());
    }

    @Override
    public Item getTabIconItem() {
        return getIconItemStack().getItem();
    }

    @Override
    public boolean hasSearchBar() {
        return true;
    }

    @Override
    public void displayAllReleventItems(List<ItemStack> list) {
        this.list = list;

        addItem(ModItems.MATERIAL.get());
        addItem(ModItems.WIRE_COIL.get());
        addItem(ModItems.ORE.get());
        addItem(ModItems.HAMMER.get());
        addItem(ModItems.BACKPACK.get());
        addItem(ModItems.BASE_UPGRADE.get());
        addItem(ModItems.STACK_UPGRADE.get());
        addItem(ModItems.CRAFTING_UPGRADE.get());
        addItem(ModItems.MAGNET_UPGRADE.get());
        addItem(ModItems.FEEDING_UPGRADE.get());
        addItem(ModItems.BATTERY_UPGRADE.get());
        addItem(ModItems.EVERLASTING_UPGRADE.get());
        addItem(ModItems.LIGHT_UPGRADE.get());
        addItem(ModItems.STABILIZED_ENDER_PEAR.get());
        addItem(ModItems.PHOTOVOLTAIC_CELL.get());
        addItem(ModItems.ASSEMBLER.get());
        addItem(FluidMaterialRegister.itemBucketMaterial);
        addItem(FluidRegister.itemBucketFluid);

        addBlock(ModBlocks.MATERIAL.get());
        addBlock(ModBlocks.ANVIL.get());
        addBlock(ModBlocks.FURNACE.get());
        addBlock(ModBlocks.BLOCK_ALABASTER.get());
        addBlock(ModBlocks.BLOCK_BASALT.get());
        addBlock(ModBlocks.BLOCK_HARDENED_STONE.get());
        addBlock(ModBlocks.BLOCK_MICA.get());
        addBlock(ModBlocks.STRUCTURE_FRAME.get());
        addBlock(ModBlocks.MACHINE_BASE.get());
        addBlock(ModBlocks.MODIFIER_NULL.get());
        addBlock(ModBlocks.MODIFIER_ACCURACY.get());
        addBlock(ModBlocks.MODIFIER_SPEED.get());
        addBlock(ModBlocks.MODIFIER_PIEZO.get());
        addBlock(ModBlocks.SOLAR_ARRAY.get());
        addBlock(ModBlocks.SOLAR_CELL.get());
        addBlock(ModBlocks.VOID_ORE_MINER.get());
        addBlock(ModBlocks.VOID_RES_MINER.get());
        addBlock(ModBlocks.NANO_BOT_BEACON.get());
        addBlock(ModBlocks.LASER_LENS.get());
        addBlock(ModBlocks.LASER_CORE.get());

        for (ItemStack stack : externalStacks) {
            addStack(stack);
        }
    }

    private void addItem(Item item) {
        item.getSubItems(item, this, list);
    }

    private void addBlock(Block block) {
        ItemStack stack = new ItemStack(block);
        block.getSubBlocks(stack.getItem(), this, list);
    }

    private void addStack(ItemStack stack) {
        list.add(stack);
    }

    public static void addToTab(Item item) {
        externalStacks.add(new ItemStack(item));
    }

    public static void addToTab(Block block) {
        externalStacks.add(new ItemStack(block));
    }

    public static void addToTab(ItemStack stack) {
        externalStacks.add(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getTabLabel() {
        return LibMisc.MOD_NAME;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getTranslatedTabLabel() {
        return LibMisc.MOD_NAME;
    }
}
