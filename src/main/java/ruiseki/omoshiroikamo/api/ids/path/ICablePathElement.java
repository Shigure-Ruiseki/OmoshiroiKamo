package ruiseki.omoshiroikamo.api.ids.path;

import ruiseki.omoshiroikamo.api.ids.block.cable.ICable;

/**
 * A path element for {@link ICable}.
 * 
 * @author rubensworks
 */
public interface ICablePathElement extends IPathElement<ICablePathElement> {

    /**
     * @return The cable.
     */
    public ICable getCable();

}
