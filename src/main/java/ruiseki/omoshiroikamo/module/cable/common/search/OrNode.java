package ruiseki.omoshiroikamo.module.cable.common.search;

import java.util.List;

import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemStackKey;

final class OrNode implements SearchNode {

    private final List<SearchNode> children;

    OrNode(List<SearchNode> children) {
        this.children = children;
    }

    @Override
    public boolean matches(ItemStackKey k) {
        for (SearchNode n : children) {
            if (n.matches(k)) return true;
        }
        return false;
    }
}
