package ruiseki.omoshiroikamo.module.multiblock.common.registries;

import java.io.File;
import java.util.List;

import ruiseki.omoshiroikamo.core.json.AbstractJsonWriter;

/**
 * Writer for Multiblock Focusable definitions.
 */
public class FocusableJsonWriter extends AbstractJsonWriter<List<FocusableMaterial>> {

    public FocusableJsonWriter(File path) {
        super(path);
    }
}
