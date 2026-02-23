package ruiseki.omoshiroikamo.module.ids.common.tileentity;

import ruiseki.omoshiroikamo.core.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.core.tileentity.TileEntityOK;
import ruiseki.omoshiroikamo.module.ids.common.part.CableBusContainer;

public class TileCableBus extends TileEntityOK {

    @NBTPersist
    public CableBusContainer cb = new CableBusContainer();

    private int oldLV = -1;

    private TileCableBus() {

    }
}
