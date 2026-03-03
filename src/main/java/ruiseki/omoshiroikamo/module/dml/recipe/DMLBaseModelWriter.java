package ruiseki.omoshiroikamo.module.dml.recipe;

import java.io.File;
import java.util.Collection;

import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistryItem;
import ruiseki.omoshiroikamo.core.json.AbstractJsonWriter;

public class DMLBaseModelWriter extends AbstractJsonWriter<Collection<ModelRegistryItem>> {

    public DMLBaseModelWriter(File path) {
        super(path);
    }
}
