package ruiseki.omoshiroikamo.core.tileentity;

import lombok.experimental.Delegate;

public class AbstractTickingTE extends AbstractSideCapabilityTE implements TileEntityOK.ITickingTile {

    @Delegate
    protected final ITickingTile tickingTileComponent = new TickingTileComponent(this);

    public AbstractTickingTE() {

    }
}
