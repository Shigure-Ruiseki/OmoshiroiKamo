package ruiseki.omoshiroikamo.module.cable.common.network.logic.reader.fluid;

import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.module.cable.common.network.AbstractPartItem;

public class ItemFluidReader extends AbstractPartItem {

    public ItemFluidReader() {
        super(ModObject.itemFluidReader.unlocalisedName);
    }

    @Override
    public ICablePart createPart() {
        return new FluidReader();
    }
}
