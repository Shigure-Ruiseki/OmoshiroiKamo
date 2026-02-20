package ruiseki.omoshiroikamo.api.ids.path.read;

import java.util.List;

import ruiseki.omoshiroikamo.api.ids.evaluate.variable.IValue;
import ruiseki.omoshiroikamo.api.ids.evaluate.variable.IValueType;
import ruiseki.omoshiroikamo.api.ids.part.IPartType;
import ruiseki.omoshiroikamo.api.ids.part.PartTarget;
import ruiseki.omoshiroikamo.api.ids.part.aspect.IAspectRead;
import ruiseki.omoshiroikamo.api.ids.part.aspect.IAspectVariable;

/**
 * A part type for readers.
 * 
 * @author rubensworks
 */
public interface IPartTypeReader<P extends IPartTypeReader<P, S>, S extends IPartStateReader<P>>
    extends IPartType<P, S> {

    /**
     * @return All possible read aspects that can be used in this part type.
     */
    public List<IAspectRead> getReadAspects();

    /**
     * Get the singleton variable for an aspect.
     * 
     * @param target    The target block.
     * @param partState The state of this part.
     * @param aspect    The aspect from the part of this state.
     * @param <V>       The value type.
     * @param <T>       The value type type.
     * @return The variable that exists only once for an aspect in the given part state.
     */
    public <V extends IValue, T extends IValueType<V>> IAspectVariable<V> getVariable(PartTarget target, S partState,
        IAspectRead<V, T> aspect);

}
