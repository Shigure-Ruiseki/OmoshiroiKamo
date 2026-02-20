package ruiseki.omoshiroikamo.core.item.weighted;

import java.util.ArrayList;
import java.util.List;

public class WeightedRandomUtil {

    public static void copyWSList(List<WeightedStackBase> dest, List<WeightedStackBase> src) {
        if (dest == null) {
            dest = new ArrayList<>();
        }

        for (WeightedStackBase weightedStackBase : src) {
            dest.add(weightedStackBase.copy());
        }

    }
}
