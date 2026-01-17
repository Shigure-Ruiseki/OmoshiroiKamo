package ruiseki.omoshiroikamo.module.cable.common.search;

import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemStackKey;

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
