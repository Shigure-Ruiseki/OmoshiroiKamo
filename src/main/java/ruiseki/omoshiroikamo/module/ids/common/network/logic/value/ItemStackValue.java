package ruiseki.omoshiroikamo.module.ids.common.network.logic.value;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.block.BlockStack;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.type.LogicType;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.type.LogicTypes;

public class ItemStackValue implements ILogicValue {

    private final ItemStack item;

    public ItemStackValue(ItemStack stack) {
        item = stack;
    }

    @Override
    public LogicType<?> getType() {
        return LogicTypes.ITEM;
    }

    @Override
    public Object raw() {
        return item;
    }

    @Override
    public boolean asBoolean() {
        return item != null;
    }

    @Override
    public int asInt() {
        return item != null ? Item.getIdFromItem(item.getItem()) : -1;
    }

    @Override
    public long asLong() {
        return asInt();
    }

    @Override
    public float asFloat() {
        return asInt();
    }

    @Override
    public double asDouble() {
        return asInt();
    }

    @Override
    public String asString() {
        if (item == null) return "null";

        String name = item.getDisplayName();
        return name != null ? name : "unknown";
    }

    @Override
    public BlockStack asBlockStack() {
        return BlockStack.fromItemStack(item);
    }

    @Override
    public ItemStack asItemStack() {
        return item;
    }
}
