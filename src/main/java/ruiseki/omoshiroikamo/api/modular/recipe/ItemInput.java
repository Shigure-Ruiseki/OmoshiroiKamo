package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.Collections;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.json.ItemJson;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.AbstractItemIOPortTE;

public class ItemInput extends AbstractRecipeInput {

    private final String oreDict;
    private final ItemStack required;

    public ItemInput(ItemStack required) {
        this.required = required.copy();
        this.oreDict = null;
    }

    public ItemInput(String oreDict, int count) {
        this.required = null;
        this.oreDict = oreDict;
        this.count = count;
    }

    private int count = 1;

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
            return OreDictionary.getOres(oreDict);
        }
        return Collections.emptyList();
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.ITEM;
    }

    @Override
    protected long getRequiredAmount() {
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

    public static ItemInput fromJson(JsonObject json) {
        if (json.has("ore")) {
            String ore = json.get("ore")
                .getAsString();
            int amount = json.has("amount") ? json.get("amount")
                .getAsInt() : 1;
            return new ItemInput(ore, amount);
        }

        ItemJson itemJson = new ItemJson();
        if (json.has("item")) {
            String itemId = json.get("item")
                .getAsString();
            if (itemId.startsWith("ore:")) {
                String ore = itemId.substring(4);
                int amount = json.has("amount") ? json.get("amount")
                    .getAsInt() : 1;
                return new ItemInput(ore, amount);
            } else {
                itemJson.name = itemId;
            }
        } else {
            Logger.warn("ItemInput requires 'item' or 'ore' key: {}", json);
            return null;
        }

        itemJson.amount = json.has("amount") ? json.get("amount")
            .getAsInt() : 1;
        itemJson.meta = json.has("meta") ? json.get("meta")
            .getAsInt() : 0;

        ItemStack stack = ItemJson.resolveItemStack(itemJson);
        if (stack == null) {
            // Don't warn here, let JSONLoader handle the null return
            return null;
        }
        return new ItemInput(stack);
    }
}
