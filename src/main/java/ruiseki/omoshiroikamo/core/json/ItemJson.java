package ruiseki.omoshiroikamo.core.json;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import org.jetbrains.annotations.Nullable;

import cpw.mods.fml.common.registry.GameData;

public class ItemJson {

    public String name; // registry name
    public String ore; // ore dict name(s), support a|b|c
    public int amount;
    public int meta;

    public static List<ItemStack> resolveListItemStack(ItemJson[] items) {
        List<ItemStack> result = new ArrayList<>();
        if (items == null) return result;

        for (ItemJson json : items) {
            ItemStack stack = ItemJson.resolveItemStack(json);
            if (stack != null) result.add(stack);
        }
        return result;
    }

    public static ItemStack resolveItemStack(ItemJson data) {
        if (data == null) return null;

        int count = data.amount > 0 ? data.amount : 1;

        // OreDictionary (support multiple)
        if (data.ore != null && !data.ore.isEmpty()) {
            return resolveFromOre(data.ore, count);
        }

        // Direct item
        if (data.name == null || data.name.isEmpty()) return null;

        Item item = GameData.getItemRegistry()
            .getObject(data.name);
        if (item == null) return null;

        return new ItemStack(item, count, data.meta);
    }

    @Nullable
    private static ItemStack resolveFromOre(String oreNames, int count) {
        if (oreNames == null || oreNames.trim()
            .isEmpty()) return null;

        String[] ores = oreNames.split("\\|");

        for (String ore : ores) {
            if (ore == null) continue;

            List<ItemStack> list = OreDictionary.getOres(ore);
            if (list == null || list.isEmpty()) continue;

            ItemStack base = list.get(0);
            base.stackSize = count;
            return base;
        }

        return null;
    }

    public static ItemJson parseItemStack(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return null;

        ItemJson json = new ItemJson();
        json.name = GameData.getItemRegistry()
            .getNameForObject(stack.getItem());
        json.amount = stack.stackSize;
        json.meta = stack.getItemDamage();
        return json;
    }

    /**
     * Parse string
     * Formats:
     * - item,amount,meta
     * - ore:ingotIron,amount,meta
     * - ore:ingotCopper|ingotCu,amount
     */
    public static ItemJson parseItemString(String s) {
        if (s == null || s.trim()
            .isEmpty()) return null;

        String[] parts = s.split(",", -1);
        ItemJson item = new ItemJson();

        // name or oredict
        String id = parts[0].trim();
        if (id.startsWith("ore:")) {
            item.ore = id.substring(4); // keep full "a|b|c"
        } else {
            item.name = id;
        }

        // amount
        try {
            item.amount = (parts.length > 1 && !parts[1].isEmpty()) ? Integer.parseInt(parts[1]) : 1;
        } catch (NumberFormatException e) {
            item.amount = 1;
        }

        // meta
        try {
            item.meta = (parts.length > 2 && !parts[2].isEmpty()) ? Integer.parseInt(parts[2]) : 0;
        } catch (NumberFormatException e) {
            item.meta = 0;
        }

        return item;
    }
}
