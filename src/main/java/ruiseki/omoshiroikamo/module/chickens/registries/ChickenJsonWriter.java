package ruiseki.omoshiroikamo.module.chickens.registries;

import java.io.File;
import java.util.List;

import ruiseki.omoshiroikamo.core.json.AbstractJsonWriter;

/**
 * Writer for Chicken definitions.
 */
public class ChickenJsonWriter extends AbstractJsonWriter<List<ChickenMaterial>> {

    public ChickenJsonWriter(File file) {
        super(file);
    }
}
