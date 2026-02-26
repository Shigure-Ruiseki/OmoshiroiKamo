package ruiseki.omoshiroikamo.core.json;

import net.minecraft.item.ItemStack;

import com.google.gson.JsonObject;

/**
 * Material representation of an ItemStack in JSON.
 */
public class ItemMaterial extends AbstractJsonMaterial {

    public String name;
    public String ore;
    public int amount = 1;
    public int meta = 0;

    public ItemMaterial() {}

    public ItemMaterial(ItemStack stack) {
        ItemJson internal = ItemJson.parseItemStack(stack);
        if (internal != null) {
            this.name = internal.name;
            this.amount = internal.amount;
            this.meta = internal.meta;
        }
    }

    @Override
    public void read(JsonObject json) {
        this.name = getString(json, "name", null);
        this.ore = getString(json, "ore", null);
        this.amount = getInt(json, "amount", 1);
        this.meta = getInt(json, "meta", 0);
    }

    @Override
    public void write(JsonObject json) {
        if (name != null) json.addProperty("name", name);
        if (ore != null) json.addProperty("ore", ore);
        if (amount != 1) json.addProperty("amount", amount);
        if (meta != 0) json.addProperty("meta", meta);
    }

    @Override
    public Object get(String key) {
        if ("name".equals(key)) return name;
        if ("ore".equals(key)) return ore;
        if ("amount".equals(key)) return amount;
        if ("meta".equals(key)) return meta;
        return null;
    }

    @Override
    public void set(String key, Object value) {
        if ("name".equals(key)) this.name = (String) value;
        if ("ore".equals(key)) this.ore = (String) value;
        if ("amount".equals(key))
            this.amount = value instanceof Integer ? (Integer) value : Integer.parseInt(value.toString());
        if ("meta".equals(key))
            this.meta = value instanceof Integer ? (Integer) value : Integer.parseInt(value.toString());
    }

    public ItemStack resolve() {
        return ItemJson.resolveItemStack(toItemJson());
    }

    public ItemJson toItemJson() {
        ItemJson internal = new ItemJson();
        internal.name = this.name;
        internal.ore = this.ore;
        internal.amount = this.amount;
        internal.meta = this.meta;
        return internal;
    }

    public static ItemMaterial parseItemStack(ItemStack stack) {
        ItemJson internal = ItemJson.parseItemStack(stack);
        if (internal == null) return null;
        ItemMaterial mat = new ItemMaterial();
        mat.name = internal.name;
        mat.amount = internal.amount;
        mat.meta = internal.meta;
        return mat;
    }

    public static ItemStack resolveItemStack(ItemMaterial mat) {
        return mat != null ? mat.resolve() : null;
    }
}
