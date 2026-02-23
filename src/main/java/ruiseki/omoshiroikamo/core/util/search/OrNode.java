package ruiseki.omoshiroikamo.core.util.search;

import java.util.List;

import ruiseki.omoshiroikamo.core.item.ItemStackKey;

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
