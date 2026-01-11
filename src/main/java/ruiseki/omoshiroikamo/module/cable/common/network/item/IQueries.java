package ruiseki.omoshiroikamo.module.cable.common.network.item;

import java.util.Map;

public interface IQueries {

    void collectItems(Map<ItemKey, Long> db);
}
