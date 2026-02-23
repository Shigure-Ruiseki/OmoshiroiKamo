package ruiseki.omoshiroikamo.core.util.search;

import ruiseki.omoshiroikamo.core.item.ItemStackKey;

final class ModNode implements SearchNode {

    private final String mod;

    ModNode(String mod) {
        this.mod = mod;
    }

    @Override
    public boolean matches(ItemStackKey k) {
        return k.getModId()
            .toLowerCase()
            .contains(mod);
    }
}
