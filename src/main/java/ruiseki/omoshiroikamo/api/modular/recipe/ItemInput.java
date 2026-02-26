package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.Collections;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.json.ItemJson;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.AbstractItemIOPortTE;

public class ItemInput extends AbstractRecipeInput {

    private String oreDict;
    private ItemStack required;
    private int count = 1;

    public ItemInput(ItemStack required) {
        this.required = required != null ? required.copy() : null;
        this.oreDict = null;
        if (this.required != null) this.count = this.required.stackSize;
    }

    public ItemInput(String oreDict, int count) {
        this.required = null;
        this.oreDict = oreDict;
        this.count = count;
    }

    public ItemInput(Item item, int count) {
        this(new ItemStack(item, count));
    }

    public ItemInput(Item item, int count, int meta) {
        this(new ItemStack(item, count, meta));
    }

    public ItemStack getRequired() {
        return required != null ? required.copy() : null;
    }

    public List<ItemStack> getItems() {
        if (required != null) {
            return Collections.singletonList(required);
        } else if (oreDict != null) {
            List<ItemStack> ores = OreDictionary.getOres(oreDict);
            for (ItemStack ore : ores) {
                ore.stackSize = count;
            }
            return ores;
        }
        return Collections.emptyList();
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.ITEM;
    }

    @Override
    public long getRequiredAmount() {
        return required != null ? required.stackSize : count;
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port instanceof AbstractItemIOPortTE;
    }

    @Override
    protected long consume(IModularPort port, long remaining, boolean simulate) {
        AbstractItemIOPortTE itemPort = (AbstractItemIOPortTE) port;
        long consumed = 0;

        for (int i = 0; i < itemPort.getSizeInventory() && remaining > 0; i++) {
            ItemStack stack = itemPort.getStackInSlot(i);
            if (stack != null && stacksMatch(stack)) {
                int consume = (int) Math.min(stack.stackSize, remaining);
                if (!simulate) {
                    stack.stackSize -= consume;
                    if (stack.stackSize <= 0) {
                        itemPort.setInventorySlotContents(i, null);
                    }
                }
                remaining -= consume;
                consumed += consume;
            }
        }
        return consumed;
    }

    private boolean stacksMatch(ItemStack input) {
        if (input == null) return false;

        if (oreDict != null) {
            int[] ids = OreDictionary.getOreIDs(input);
            int targetId = OreDictionary.getOreID(oreDict);
            for (int id : ids) {
                if (id == targetId) return true;
            }
            return false;
        }

        if (required == null) return false;
        if (required.getItem() != input.getItem()) return false;
        // 32767 is wildcard
        if (required.getItemDamage() != 32767 && required.getItemDamage() != input.getItemDamage()) return false;
        return true;
    }

    @Override
    public void read(JsonObject json) {
        if (json.has("ore")) {
            this.required = null;
            this.oreDict = json.get("ore")
                .getAsString();
            this.count = json.has("amount") ? json.get("amount")
                .getAsInt() : 1;
            return;
        }

        ItemJson itemJson = new ItemJson();
        if (json.has("item")) {
            String itemId = json.get("item")
                .getAsString();
            if (itemId.startsWith("ore:")) {
                this.required = null;
                this.oreDict = itemId.substring(4);
                this.count = json.has("amount") ? json.get("amount")
                    .getAsInt() : 1;
                return;
            } else {
                itemJson.name = itemId;
            }
        } else {
            Logger.warn("ItemInput requires 'item' or 'ore' key: {}", json);
            return;
        }

        itemJson.amount = json.has("amount") ? json.get("amount")
            .getAsInt() : 1;
        itemJson.meta = json.has("meta") ? json.get("meta")
            .getAsInt() : 0;

        ItemStack stack = ItemJson.resolveItemStack(itemJson);
        if (stack != null) {
            this.required = stack;
            this.oreDict = null;
            this.count = stack.stackSize;
        }
    }

    @Override
    public void write(JsonObject json) {
        if (oreDict != null) {
            json.addProperty("ore", oreDict);
            if (count != 1) json.addProperty("amount", count);
        } else if (required != null) {
            ItemJson data = ItemJson.parseItemStack(required);
            if (data != null) {
                json.addProperty("item", data.name);
                if (data.amount != 1) json.addProperty("amount", data.amount);
                if (data.meta != 0) json.addProperty("meta", data.meta);
            }
        }
    }

    @Override
    public boolean validate() {
        return required != null || oreDict != null;
    }

    @Override
    public void set(String key, Object value) {}

    @Override
    public Object get(String key) {
        return null;
    }

    public static ItemInput fromJson(JsonObject json) {
        ItemInput input = new ItemInput((ItemStack) null);
        input.read(json);
        return input.validate() ? input : null;
    }
}
