package ruiseki.omoshiroikamo.module.ids.common.network.logic.reader.fluid;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.network.AbstractPartItem;

public class ItemFluidReader extends AbstractPartItem {

    public ItemFluidReader() {
        super(ModObject.itemFluidReader.unlocalisedName);
    }

    @Override
    public ICablePart createPart() {
        return new FluidReader();
    }
}
