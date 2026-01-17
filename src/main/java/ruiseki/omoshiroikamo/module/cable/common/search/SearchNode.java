package ruiseki.omoshiroikamo.module.cable.common.search;

import ruiseki.omoshiroikamo.module.cable.common.network.item.ItemStackKey;

public interface SearchNode {

    boolean matches(ItemStackKey key);
}
