package ruiseki.omoshiroikamo.api.modular.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.json.ItemJson;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.AbstractItemIOPortTE;

public class ItemInput extends AbstractRecipeInput {

    private final ItemStack required;

    public ItemInput(ItemStack required) {
        this.required = required.copy();
    }

    public ItemInput(Item item, int count) {
        this(new ItemStack(item, count));
    }

    public ItemInput(Item item, int count, int meta) {
        this(new ItemStack(item, count, meta));
    }

    public ItemStack getRequired() {
        return required.copy();
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.ITEM;
    }

    @Override
    protected long getRequiredAmount() {
        return required.stackSize;
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
            if (stack != null && stacksMatch(stack, required)) {
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

    private boolean stacksMatch(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false;
        if (a.getItem() != b.getItem()) return false;
        // 32767 is wildcard
        if (b.getItemDamage() != 32767 && a.getItemDamage() != b.getItemDamage()) return false;
        return true;
    }

    public static ItemInput fromJson(JsonObject json) {
        ItemJson itemJson = new ItemJson();
        if (json.has("ore")) {
            itemJson.ore = json.get("ore")
                .getAsString();
        } else if (json.has("item")) {
            String itemId = json.get("item")
                .getAsString();
            if (itemId.startsWith("ore:")) {
                itemJson.ore = itemId.substring(4);
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
            Logger.warn("Unknown item in recipe: {}", json);
            return null;
        }
        return new ItemInput(stack);
    }
}
