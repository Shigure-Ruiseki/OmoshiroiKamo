package ruiseki.omoshiroikamo.module.backpack.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class BackpackItemStackUtils {

    public static NBTTagCompound saveAllSlotsExtended(NBTTagCompound nbt, List<ItemStack> inventory) {
        NBTTagList list = new NBTTagList();

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);

            if (stack != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte) i);
                writeToNBTExtended(stack, tag);
                list.appendTag(tag);
            }
        }

        nbt.setTag("Items", list);
        return nbt;
    }

    /**
     * Equivalent to Kotlin extension fun ItemStack.writeToNBTExtended
     */
    public static NBTTagCompound writeToNBTExtended(ItemStack stack, NBTTagCompound nbt) {
        stack.writeToNBT(nbt);
        nbt.setInteger("Count", stack.stackSize);
        return nbt;
    }

    public static void loadAllItemsExtended(NBTTagCompound nbt, List<ItemStack> inventory) {
        NBTTagList list = nbt.getTagList("Items", 10);

        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            int j = tag.getByte("Slot") & 255;

            if (j < inventory.size()) {
                inventory.set(j, loadItemStackExtended(tag));
            }
        }
    }

    public static ItemStack loadItemStackExtended(NBTTagCompound nbt) {
        ItemStack stack = ItemStack.loadItemStackFromNBT(nbt);
        if (stack != null) {
            stack.stackSize = nbt.getInteger("Count");
        }
        return stack;
    }

    public static List<String> getTooltipLower(ItemStack stack) {
        try {
            List<String> list = stack.getTooltip(
                Minecraft.getMinecraft().thePlayer,
                Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
            List<String> lower = new ArrayList<>(list.size());
            for (String s : list) lower.add(s.toLowerCase());
            return lower;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static boolean matchesAllTerms(String group, ItemStack stack, String lowerName, List<String> tooltip,
        String creativeTab) {
        String[] terms = splitTerms(group);

        for (String term : terms) {
            if (term.isEmpty()) continue;

            boolean negated = term.startsWith("-");
            if (negated) term = term.substring(1);

            boolean result = matchesSingleTerm(term, stack, lowerName, tooltip, creativeTab);

            if (negated && result) return false;
            if (!negated && !result) return false;
        }
        return true;
    }

    public static String[] splitTerms(String input) {
        List<String> result = new ArrayList<>();
        boolean inQuote = false;
        StringBuilder current = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (c == '"') {
                inQuote = !inQuote;
                continue;
            }
            if (c == ' ' && !inQuote) {
                if (current.length() > 0) {
                    result.add(current.toString());
                    current.setLength(0);
                }
                continue;
            }
            current.append(c);
        }
        if (current.length() > 0) result.add(current.toString());

        return result.toArray(new String[0]);
    }

    public static String[] getModInfo(ItemStack stack) {
        Item item = stack.getItem();
        String name = Item.itemRegistry.getNameForObject(item);

        // modid
        String modid = "minecraft";
        if (name.contains(":")) {
            modid = name.substring(0, name.indexOf(":"));
        }

        // mod name (display)
        String modname = modid;

        ModContainer mc = Loader.instance()
            .getIndexedModList()
            .get(modid);
        if (mc != null) {
            modname = mc.getName()
                .toLowerCase();
        }

        return new String[] { modid.toLowerCase(), modname.toLowerCase() };
    }

    public static boolean matchesSingleTerm(String term, ItemStack stack, String lowerName, List<String> tooltip,
        String creativeTab) {
        if (term == null || term.isEmpty()) return true;
        term = term.toLowerCase();

        // @modname
        String[] info = getModInfo(stack);
        String modId = info[0];
        String modName = info[1];
        if (term.startsWith("@")) {
            String mod = term.substring(1);
            return modId.contains(mod) || modName.contains(mod);
        }

        // #tooltip only
        if (term.startsWith("#")) {
            String t = term.substring(1);
            for (String line : tooltip) {
                if (line.contains(t)) return true;
            }
            return false;
        }

        // $oredict
        if (term.startsWith("$")) {
            String key = term.substring(1);
            int id = OreDictionary.getOreID(key);
            if (id == -1) return false;

            for (int x : OreDictionary.getOreIDs(stack)) {
                if (Objects.equals(
                    OreDictionary.getOreName(x)
                        .toLowerCase(),
                    OreDictionary.getOreName(id)
                        .toLowerCase())) {
                    return true;
                }
            }
            return false;
        }

        // %creativetab
        if (term.startsWith("%")) {
            String tab = term.substring(1);
            return creativeTab.contains(tab);
        }

        // Normal: check name
        if (lowerName.contains(term)) {
            return true;
        }

        // Normal: check mod name
        if (modName.contains(term)) {
            return true;
        }

        // Normal: check tooltip
        for (String line : tooltip) {
            if (line.contains(term)) {
                return true;
            }
        }

        return false;
    }
}
