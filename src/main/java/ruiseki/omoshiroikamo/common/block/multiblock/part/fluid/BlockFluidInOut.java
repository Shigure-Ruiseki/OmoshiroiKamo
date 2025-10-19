package ruiseki.omoshiroikamo.common.block.multiblock.part.fluid;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.material.MaterialEntry;
import ruiseki.omoshiroikamo.api.material.MaterialRegistry;
import ruiseki.omoshiroikamo.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.common.block.abstractClass.AbstractBlock;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;

public class BlockFluidInOut extends AbstractBlock<TEFluidInOut> {

    protected BlockFluidInOut() {
        super(ModObject.blockFluidInOut, TEFluidInOut.class);
    }

    public static BlockFluidInOut create() {
        return new BlockFluidInOut();
    }

    @Override
    public void init() {
        GameRegistry.registerBlock(this, ItemBlockFluidInOut.class, modObject.unlocalisedName);
        GameRegistry.registerTileEntity(TEFluidInput.class, modObject.unlocalisedName + "TEFluidInput");
        GameRegistry.registerTileEntity(TEFluidOutput.class, modObject.unlocalisedName + "TEFluidOutput");
    }

    @Override
    public String getUnlocalizedName() {
        return super.getUnlocalizedName();
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
            return new TEFluidOutput(meta);
        } else {
            return new TEFluidInput(meta);
        }
    }

    public static class ItemBlockFluidInOut extends ItemBlockOK implements IAdvancedTooltipProvider {

        public ItemBlockFluidInOut(Block block) {
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
                list.add(new ItemStack(this, 1, meta));;
                list.add(new ItemStack(this, 1, LibResources.META1 + meta));
            }
        }

        @Override
        public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {}

        @Override
        public void addBasicEntries(ItemStack itemstack, EntityPlayer player, List<String> list, boolean advanced) {
            int meta = itemstack.getItemDamage();
            MaterialEntry material = MaterialRegistry.fromMeta(meta % LibResources.META1);

            list.add(String.format("§7Material:§f %s", material.getName()));
            list.add(String.format("§7Volume:§f %,dL", material.getVolumeMB()));
            list.add(String.format("§7Melting Point:§f %d K", (int) material.getMeltingPointK()));

            NBTTagCompound tag = itemstack.getTagCompound();
            if (tag != null && tag.hasKey("tank")) {
                FluidStack fl = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("tank"));
                if (fl != null && fl.getFluid() != null) {
                    list.add(
                        String.format(
                            "§7Stored:§f %,dL %s",
                            fl.amount,
                            fl.getFluid()
                                .getName()));
                }
            } else {
                list.add("§7Stored:§f " + LibMisc.LANG_UTILS.localize("fluid.empty"));
            }
        }

        @Override
        public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list,
            boolean flag) {}
    }

}
