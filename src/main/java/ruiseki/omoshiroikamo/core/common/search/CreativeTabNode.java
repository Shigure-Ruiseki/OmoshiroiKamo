package ruiseki.omoshiroikamo.core.common.search;

import ruiseki.omoshiroikamo.core.item.ItemStackKey;

final class CreativeTabNode implements SearchNode {

    private final String tab;

    CreativeTabNode(String tab) {
        this.tab = tab;
    }

    @Override
    public boolean matches(ItemStackKey k) {
        return k.getCreativeTab()
            .contains(tab);
    }
}
