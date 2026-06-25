package ruiseki.omoshiroikamo.core.tileentity;

import lombok.experimental.Delegate;
import ruiseki.okcore.tileentity.TileEntityOK;

public class AbstractTickingTE extends TileEntityOK implements TileEntityOK.ITickingTile {

    @Delegate
    protected final TileEntityOK.ITickingTile tickingTileComponent = new TileEntityOK.TickingTileComponent(this);

    public AbstractTickingTE() {

    }
}
