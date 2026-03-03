package ruiseki.omoshiroikamo.module.cows.common.registries;

import java.io.File;
import java.util.List;

import ruiseki.omoshiroikamo.core.json.AbstractJsonWriter;

/**
 * Writer for Cow JSON files.
 */
public class CowJsonWriter extends AbstractJsonWriter<List<CowMaterial>> {

    public CowJsonWriter(File file) {
        super(file);
    }

    // AbstractJsonWriter already handles Collection<?> and calls write on
    // materials.
}
