package ruiseki.omoshiroikamo.api.ids.part.aspect;

import ruiseki.omoshiroikamo.api.ids.evaluate.variable.IValue;
import ruiseki.omoshiroikamo.api.ids.evaluate.variable.IValueType;
import ruiseki.omoshiroikamo.api.ids.part.PartTarget;

/**
 * An element that can be used inside parts to access a specific aspect of something to read.
 * 
 * @author rubensworks
 */
public interface IAspectRead<V extends IValue, T extends IValueType<V>> extends IAspect<V, T> {

    /**
     * Creates a new variable for this aspect.
     * 
     * @param target The target for this aspect.
     * @return The variable pointing to the given target.
     */
    public IAspectVariable<V> createNewVariable(PartTarget target);

}
