package ruiseki.omoshiroikamo.core.common.search;

import ruiseki.omoshiroikamo.api.item.ItemStackKey;

public interface SearchNode {

    boolean matches(ItemStackKey key);
}
