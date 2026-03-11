package ruiseki.omoshiroikamo.module.dml.recipe;

import java.io.File;
import java.util.Collection;

import ruiseki.omoshiroikamo.api.entity.dml.LivingRegistryItem;
import ruiseki.omoshiroikamo.core.json.AbstractJsonWriter;

public class DMLLivingMatterWriter extends AbstractJsonWriter<Collection<LivingRegistryItem>> {

    public DMLLivingMatterWriter(File path) {
        super(path);
    }
}
