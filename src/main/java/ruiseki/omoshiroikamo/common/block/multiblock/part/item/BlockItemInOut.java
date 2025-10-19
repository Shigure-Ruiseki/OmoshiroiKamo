package ruiseki.omoshiroikamo.common.block.multiblock.part.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.material.MaterialEntry;
import ruiseki.omoshiroikamo.api.material.MaterialRegistry;
import ruiseki.omoshiroikamo.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractBlock;
import ruiseki.omoshiroikamo.common.block.multiblock.part.fluid.TEFluidInput;
import ruiseki.omoshiroikamo.common.block.multiblock.part.fluid.TEFluidOutput;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class BlockItemInOut extends AbstractBlock<TEItemInOut> {

    protected BlockItemInOut() {
        super(ModObject.blockItemInOut, TEItemInOut.class);
    }

    public static BlockItemInOut create() {
        return new BlockItemInOut();
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, ItemBlockItemInOut.class, modObject.unlocalisedName);
        GameRegistry.registerTileEntity(TEFluidInput.class, modObject.unlocalisedName + "TEItemInput");
        GameRegistry.registerTileEntity(TEFluidOutput.class, modObject.unlocalisedName + "TEItemOutput");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        for (MaterialEntry materialEntry : MaterialRegistry.all()) {
            int meta = materialEntry.meta;
            list.add(new ItemStack(this, 1, meta));;
            list.add(new ItemStack(this, 1, LibResources.META1 + meta));
        }
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        if (meta >= 100) {
            return new TEItemOutput(meta);
        } else {
            return new TEItemInput(meta);
        }
    }

    public static class ItemBlockItemInOut extends ItemBlockOK implements IAdvancedTooltipProvider {

        public ItemBlockItemInOut(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int meta = stack.getItemDamage();
            boolean isOutput = meta >= LibResources.META1;
            MaterialEntry material = MaterialRegistry.fromMeta(meta % LibResources.META1);

            String base = super.getUnlocalizedName(stack);
            String type = isOutput ? "output" : "input";
            String mat = material.getUnlocalizedName();

            return base + "." + type + "." + mat;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
            for (MaterialEntry materialEntry : MaterialRegistry.all()) {
                int meta = materialEntry.meta;
                list.add(new ItemStack(this, 1, meta));

                list.add(new ItemStack(this, 1, LibResources.META1 + meta));
            }
        }

        @Override
        public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {}

        @Override
        public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
            int meta = itemstack.getItemDamage();
            MaterialEntry material = MaterialRegistry.fromMeta(meta % LibResources.META1);

            list.add(String.format("§7Material:§f %s", material.getName()));
            list.add(String.format("§7Slot:§f %d", material.getItemSlotCount()));
        }

        @Override
        public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list,
            boolean flag) {}

    }

}
