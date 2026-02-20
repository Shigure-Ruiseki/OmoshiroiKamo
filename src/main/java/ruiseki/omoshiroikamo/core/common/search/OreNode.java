package ruiseki.omoshiroikamo.core.common.search;

import net.minecraftforge.oredict.OreDictionary;

import ruiseki.omoshiroikamo.core.item.ItemStackKey;

final class OreNode implements SearchNode {

    private final int oreId;

    OreNode(String name) {
        this.oreId = OreDictionary.getOreID(name);
    }

    @Override
    public boolean matches(ItemStackKey k) {
        if (oreId == -1) return false;
        for (int id : k.getOreIds()) {
            if (id == oreId) return true;
        }
        return false;
    }
}
