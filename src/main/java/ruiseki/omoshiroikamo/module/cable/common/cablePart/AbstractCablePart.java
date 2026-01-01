package ruiseki.omoshiroikamo.module.cable.common.cablePart;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.cable.ICable;
import ruiseki.omoshiroikamo.api.cable.ICablePart;

public abstract class AbstractCablePart implements ICablePart {

    protected ICable cable;
    protected ForgeDirection side;

    @Override
    public void setCable(ICable cable, ForgeDirection side) {
        this.cable = cable;
        this.side = side;
    }

    @Override
    public ICable getCable() {
        return cable;
    }

    @Override
    public ForgeDirection getSide() {
        return side;
    }

    @Override
    public void onAttached() {}

    @Override
    public void onDetached() {}

    @Override
    public void update() {}
}
