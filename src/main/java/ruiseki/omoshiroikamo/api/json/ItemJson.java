package ruiseki.omoshiroikamo.api.json;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameData;

public class ItemJson {

    public String name;
    public String ore;
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
        if (data == null || data.name == null) return null;
        Item item = GameData.getItemRegistry()
            .getObject(data.name);
        if (item == null) return null;
        return new ItemStack(item, data.amount > 0 ? data.amount : 1, data.meta);
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

    public static ItemJson parseItemString(String s) {
        if (s == null || s.trim()
            .isEmpty()) return null;

        String[] parts = s.split(",", -1);

        ItemJson item = new ItemJson();
        item.name = parts[0].trim();

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
