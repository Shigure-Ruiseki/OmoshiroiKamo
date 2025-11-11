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

        for (ModItems item : ModItems.values()) {
            addItem(item.get());
        }

        addItem(FluidMaterialRegister.itemBucketMaterial);
        addItem(FluidRegister.itemBucketFluid);

        for (ModBlocks block : ModBlocks.values()) {
            addBlock(block.get());
        }

        for (ItemStack stack : externalStacks) {
            addStack(stack);
        }
    }

    public void addItem(Item item) {
        item.getSubItems(item, this, list);
    }

    public void addBlock(Block block) {
        ItemStack stack = new ItemStack(block);
        block.getSubBlocks(stack.getItem(), this, list);
    }

    public void addStack(ItemStack stack) {
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
