package ruiseki.omoshiroikamo.core.util.search;

import ruiseki.omoshiroikamo.core.item.ItemStackKey;

public interface SearchNode {

    boolean matches(ItemStackKey key);
}
