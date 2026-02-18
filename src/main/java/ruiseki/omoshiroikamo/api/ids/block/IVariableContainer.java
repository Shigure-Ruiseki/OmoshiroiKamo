package ruiseki.omoshiroikamo.api.ids.block;

import java.util.Map;

import ruiseki.omoshiroikamo.api.block.DimPos;
import ruiseki.omoshiroikamo.api.ids.item.IVariableFacade;

/**
 * An interface for containers that can hold {@link IVariableFacade}s.
 * 
 * @author rubensworks
 */
public interface IVariableContainer {

    /**
     * @return The position this container is at.
     */
    public DimPos getPosition();

    /**
     * @return The stored variable facades for this tile.
     */
    public Map<Integer, IVariableFacade> getVariableCache();

}
