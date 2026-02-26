package ruiseki.omoshiroikamo.api.ids.part;

import net.minecraftforge.common.util.ForgeDirection;

/**
 * Reports a selected part from th IPartHost
 */
public class SelectedPart {

    /**
     * selected part.
     */
    // public final IPart part;

    /**
     * facade part.
     */
    // public final IFacadePart facade;

    /**
     * side the part is mounted too, or {@link ForgeDirection}.UNKNOWN for cables.
     */
    public final ForgeDirection side;

    public SelectedPart() {
        // this.part = null;
        // this.facade = null;
        this.side = ForgeDirection.UNKNOWN;
    }

    // public SelectedPart(final IPart part, final ForgeDirection side) {
    // this.part = part;
    // this.facade = null;
    // this.side = side;
    // }
    //
    // public SelectedPart(final IFacadePart facade, final ForgeDirection side) {
    // this.part = null;
    // this.facade = facade;
    // this.side = side;
    // }
}
