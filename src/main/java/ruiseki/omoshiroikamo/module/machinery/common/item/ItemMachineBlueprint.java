package ruiseki.omoshiroikamo.module.machinery.common.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import com.gtnewhorizon.structurelib.alignment.constructable.ConstructableUtility;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.structure.core.IStructureEntry;
import ruiseki.omoshiroikamo.core.common.structure.CustomStructureRegistry;
import ruiseki.omoshiroikamo.core.common.structure.StructureManager;
import ruiseki.omoshiroikamo.core.item.ItemOK;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.machinery.common.tile.TEMachineController;

/**
 * Machine Blueprint item that defines which custom structure a controller
 * should use.
 * Each registered CustomStructure generates a corresponding blueprint in the
 * creative tab.
 */
public class ItemMachineBlueprint extends ItemOK {

    public static final String STRUCTURE_NBT_KEY = "customStructure";

    public ItemMachineBlueprint() {
        super("machineBlueprint");
        setMaxStackSize(1);
        setTextureName("modular/machineBlueprint");
    }

    /**
     * Get the structure name associated with this blueprint.
     */
    @Nullable
    public static String getStructureName(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return null;
        }
        String name = stack.getTagCompound()
            .getString(STRUCTURE_NBT_KEY);
        return name.isEmpty() ? null : name;
    }

    /**
     * Set the structure name for this blueprint.
     */
    public static void setStructureName(ItemStack stack, String name) {
        if (stack == null) return;
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound()
            .setString(STRUCTURE_NBT_KEY, name);
    }

    /**
     * Create a blueprint ItemStack for the given structure name.
     */
    public static ItemStack createBlueprint(Item blueprintItem, String structureName) {
        ItemStack stack = new ItemStack(blueprintItem);
        setStructureName(stack, structureName);
        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list) {
        // Generate a blueprint for each registered custom structure
        for (String name : CustomStructureRegistry.getRegisteredNames()) {
            list.add(createBlueprint(item, name));
        }

        // Also add an empty blueprint for manual assignment
        list.add(new ItemStack(item));
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        String structureName = getStructureName(stack);
        if (structureName != null && !structureName.isEmpty()) {
            IStructureEntry entry = StructureManager.getInstance()
                .getCustomStructure(structureName);
            if (entry != null && entry.getDisplayName() != null
                && !entry.getDisplayName()
                    .isEmpty()) {
                return LibMisc.LANG.localize("item.machineBlueprint.display.name.format", entry.getDisplayName());
            }
            return LibMisc.LANG.localize("item.machineBlueprint.display.name.format", structureName);
        }
        return LibMisc.LANG.localize("item.machineBlueprint.display.name.empty");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        String structureName = getStructureName(stack);
        if (structureName != null) {
            tooltip.add(
                EnumChatFormatting.GRAY + LibMisc.LANG.localize("tooltip.machineBlueprint.structure", structureName));

            // Show if the structure is valid
            if (CustomStructureRegistry.hasDefinition(structureName)) {
                tooltip.add(EnumChatFormatting.GREEN + LibMisc.LANG.localize("tooltip.machineBlueprint.valid"));
            } else {
                tooltip.add(EnumChatFormatting.RED + LibMisc.LANG.localize("tooltip.machineBlueprint.invalid"));
            }
        } else {
            tooltip.add(EnumChatFormatting.GRAY + LibMisc.LANG.localize("tooltip.machineBlueprint.null"));
        }

        tooltip.add(EnumChatFormatting.DARK_GRAY + LibMisc.LANG.localize("tooltip.machineBlueprint.show"));
        tooltip.add(EnumChatFormatting.DARK_GRAY + LibMisc.LANG.localize("tooltip.machineBlueprint.build"));
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TEMachineController)) {
            return false;
        }

        String structureName = getStructureName(stack);
        if (structureName == null || structureName.isEmpty()) {
            if (!world.isRemote) {
                player.addChatComponentMessage(
                    new ChatComponentText(
                        EnumChatFormatting.YELLOW + LibMisc.LANG.localize("message.machineBlueprint.empty")));
            }
            return false;
        }

        // Server side: set the structure name on the controller
        TEMachineController controller = (TEMachineController) te;
        if (!world.isRemote) {
            controller.setCustomStructureName(structureName);
        }

        // Trigger StructureLib hologram display / survival build
        return ConstructableUtility.handle(stack, player, world, x, y, z, side);
    }

}
