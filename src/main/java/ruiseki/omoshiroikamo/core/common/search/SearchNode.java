package ruiseki.omoshiroikamo.core.common.search;

import ruiseki.omoshiroikamo.core.item.ItemStackKey;

public interface SearchNode {

    boolean matches(ItemStackKey key);
}
