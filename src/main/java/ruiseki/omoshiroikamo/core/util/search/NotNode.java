package ruiseki.omoshiroikamo.core.util.search;

import ruiseki.omoshiroikamo.core.item.ItemStackKey;

final class NotNode implements SearchNode {

    private final SearchNode child;

    NotNode(SearchNode child) {
        this.child = child;
    }

    @Override
    public boolean matches(ItemStackKey k) {
        return !child.matches(k);
    }
}
