package ruiseki.omoshiroikamo.module.ids.common.item.part.crafting;

import ruiseki.omoshiroikamo.api.item.ItemStackKey;

public class IngredientStack {

    private final Object key;
    private final long amount;

    public IngredientStack(Object key, long amount) {
        this.key = key;
        this.amount = amount;
    }

    public ItemStackKey getKey() {
        return (ItemStackKey) key;
    }

    public long getAmount() {
        return amount;
    }
}
