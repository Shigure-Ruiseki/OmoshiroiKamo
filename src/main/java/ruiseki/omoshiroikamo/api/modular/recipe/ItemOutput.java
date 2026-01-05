package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.registry.GameRegistry;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.AbstractItemIOPortTE;

/**
 * Recipe output for items.
 */
public class ItemOutput implements IRecipeOutput {

    private final ItemStack output;

    public ItemOutput(ItemStack output) {
        this.output = output.copy();
    }

    public ItemOutput(Item item, int count) {
        this(new ItemStack(item, count));
    }

    public ItemOutput(Item item, int count, int meta) {
        this(new ItemStack(item, count, meta));
    }

    public ItemStack getOutput() {
        return output.copy();
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.ITEM;
    }

    @Override
    public boolean process(List<IModularPort> ports, boolean simulate) {
        int remaining = output.stackSize;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.ITEM) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT) continue;
            if (!(port instanceof AbstractItemIOPortTE)) {
                throw new IllegalStateException(
                    "ITEM OUTPUT port must be AbstractItemIOPortTE, got: " + port.getClass()
                        .getName());
            }
            AbstractItemIOPortTE itemPort = (AbstractItemIOPortTE) port;

            for (int i = 0; i < itemPort.getSizeInventory() && remaining > 0; i++) {
                ItemStack stack = itemPort.getStackInSlot(i);
                if (stack == null) {
                    int insert = Math.min(remaining, output.getMaxStackSize());
                    if (!simulate) {
                        ItemStack newStack = output.copy();
                        newStack.stackSize = insert;
                        itemPort.setInventorySlotContents(i, newStack);
                    }
                    remaining -= insert;
                } else if (stacksMatch(stack, output)) {
                    int space = stack.getMaxStackSize() - stack.stackSize;
                    int insert = Math.min(remaining, space);
                    if (!simulate) {
                        stack.stackSize += insert;
                    }
                    remaining -= insert;
                }
            }
        }

        return remaining <= 0;
    }

    private boolean stacksMatch(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false;
        if (a.getItem() != b.getItem()) return false;
        if (a.getItemDamage() != b.getItemDamage()) return false;
        return ItemStack.areItemStackTagsEqual(a, b);
    }

    /**
     * Create ItemOutput from JSON.
     * Format: { "item": "modid:itemname", "amount": 1, "meta": 0 }
     */
    public static ItemOutput fromJson(JsonObject json) {
        String itemId = json.get("item")
            .getAsString();
        int amount = json.has("amount") ? json.get("amount")
            .getAsInt() : 1;
        int meta = json.has("meta") ? json.get("meta")
            .getAsInt() : 0;

        String[] parts = itemId.split(":");
        String modId = parts[0];
        String itemName = parts[1];

        Item item = GameRegistry.findItem(modId, itemName);
        if (item == null) {
            Logger.warn("Unknown item in recipe: {}", itemId);
            return null;
        }
        return new ItemOutput(new ItemStack(item, amount, meta));
    }
}
