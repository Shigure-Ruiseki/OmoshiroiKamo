package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.item.AbstractItemIOPortTE;

/**
 * Recipe input requirement for items.
 */
public class ItemInput implements IRecipeInput {

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
    public boolean process(List<IModularPort> ports, boolean simulate) {
        int remaining = required.stackSize;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.ITEM) continue;
            if (port.getPortDirection() != IPortType.Direction.INPUT) continue;
            if (!(port instanceof AbstractItemIOPortTE)) {
                throw new IllegalStateException(
                    "ITEM INPUT port must be AbstractItemIOPortTE, got: " + port.getClass()
                        .getName());
            }
            AbstractItemIOPortTE itemPort = (AbstractItemIOPortTE) port;

            for (int i = 0; i < itemPort.getSizeInventory() && remaining > 0; i++) {
                ItemStack stack = itemPort.getStackInSlot(i);
                if (stack != null && stacksMatch(stack, required)) {
                    int consume = Math.min(stack.stackSize, remaining);
                    if (!simulate) {
                        stack.stackSize -= consume;
                        if (stack.stackSize <= 0) {
                            itemPort.setInventorySlotContents(i, null);
                        }
                    }
                    remaining -= consume;
                }
            }
        }

        return remaining <= 0;
    }

    private boolean stacksMatch(ItemStack a, ItemStack b) {
        if (a == null || b == null) return false;
        if (a.getItem() != b.getItem()) return false;
        // 32767 is wildcard
        if (b.getItemDamage() != 32767 && a.getItemDamage() != b.getItemDamage()) return false;
        return true;
    }
}
