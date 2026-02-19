package ruiseki.omoshiroikamo.module.ids.common.item.part.logic.fluid;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.api.ids.ICablePart;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.AbstractPartItem;

public class ItemFluidReader extends AbstractPartItem {

    public ItemFluidReader() {
        super(ModObject.itemFluidReader.unlocalisedName);
    }

    @Override
    public ICablePart createPart() {
        return new FluidReader();
    }
}
