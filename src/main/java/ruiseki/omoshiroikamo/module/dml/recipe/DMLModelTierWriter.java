package ruiseki.omoshiroikamo.module.dml.recipe;

import java.io.File;
import java.util.Collection;

import ruiseki.omoshiroikamo.api.entity.dml.ModelTierRegistryItem;
import ruiseki.omoshiroikamo.core.json.AbstractJsonWriter;

public class DMLModelTierWriter extends AbstractJsonWriter<Collection<ModelTierRegistryItem>> {

    public DMLModelTierWriter(File path) {
        super(path);
    }
}
