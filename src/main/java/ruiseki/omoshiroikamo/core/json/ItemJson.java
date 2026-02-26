package ruiseki.omoshiroikamo.core.json;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.registry.GameData;

public class ItemJson implements IJsonMaterial {

    public String name; // registry name
    public String ore; // ore dict name(s), support a|b|c
    public int amount;
    public int meta;

    @Override
    public void read(JsonObject json) {
        this.name = json.has("name") ? json.get("name")
            .getAsString() : null;
        this.ore = json.has("ore") ? json.get("ore")
            .getAsString() : null;
        this.amount = json.has("amount") ? json.get("amount")
            .getAsInt() : 1;
        this.meta = json.has("meta") ? json.get("meta")
            .getAsInt() : 0;
    }

    @Override
    public void write(JsonObject json) {
        if (name != null) json.addProperty("name", name);
        if (ore != null) json.addProperty("ore", ore);
        if (amount != 1) json.addProperty("amount", amount);
        if (meta != 0) json.addProperty("meta", meta);
    }

    @Override
    public boolean validate() {
        return (name != null && !name.isEmpty()) || (ore != null && !ore.isEmpty());
    }

    public Object get(String key) {
        return null;
    }

    public void set(String key, Object value) {}

    public static ItemJson fromJson(JsonObject json) {
        ItemJson item = new ItemJson();
        item.read(json);
        return item;
    }

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

    public static ItemJson parseItemString(String s) {
        if (s == null || s.trim()
            .isEmpty()) return null;

        String[] parts = s.split(",", -1);
        ItemJson item = new ItemJson();

        String id = parts[0].trim();
        if (id.startsWith("ore:")) {
            item.ore = id.substring(4);
        } else {
            item.name = id;
        }

        try {
            item.amount = (parts.length > 1 && !parts[1].isEmpty()) ? Integer.parseInt(parts[1]) : 1;
        } catch (NumberFormatException e) {
            item.amount = 1;
        }

        try {
            item.meta = (parts.length > 2 && !parts[2].isEmpty()) ? Integer.parseInt(parts[2]) : 0;
        } catch (NumberFormatException e) {
            item.meta = 0;
        }

        return item;
    }
}
