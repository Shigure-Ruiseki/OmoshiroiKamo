package ruiseki.omoshiroikamo.module.ids.common.capability.path;

import javax.annotation.Nullable;

import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.ids.path.IPathElement;
import ruiseki.omoshiroikamo.api.ids.path.ISidedPathElement;

/**
 * @author rubensworks
 */
public class SidedPathElement implements ISidedPathElement {

    private final IPathElement pathElement;
    private final ForgeDirection side;

    public SidedPathElement(IPathElement pathElement, @Nullable ForgeDirection side) {
        this.pathElement = pathElement;
        this.side = side;
    }

    @Override
    public IPathElement getPathElement() {
        return pathElement;
    }

    @Override
    @Nullable
    public ForgeDirection getSide() {
        return side;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ISidedPathElement && compareTo((ISidedPathElement) o) == 0;
    }

    @Override
    public int compareTo(ISidedPathElement o) {
        int pathElement = getPathElement().getPosition()
            .compareTo(
                o.getPathElement()
                    .getPosition());
        if (pathElement == 0) {
            ForgeDirection thisSide = getSide();
            ForgeDirection thatSide = o.getSide();
            // If one of the sides is null, assume equality
            return thisSide != null && thatSide != null ? thisSide.compareTo(thatSide) : 0;
        }
        return pathElement;
    }

    @Override
    public int hashCode() {
        return getPathElement().getPosition()
            .hashCode();
    }

    @Override
    public String toString() {
        return "[Sided PE: " + getPathElement().getPosition() + " @ " + getSide() + "]";
    }

    public static SidedPathElement of(IPathElement pathElement, @Nullable ForgeDirection side) {
        return new SidedPathElement(pathElement, side);
    }
}
