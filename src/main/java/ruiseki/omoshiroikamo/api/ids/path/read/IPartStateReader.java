package ruiseki.omoshiroikamo.api.ids.path.read;

import ruiseki.omoshiroikamo.api.ids.evaluate.variable.IValue;
import ruiseki.omoshiroikamo.api.ids.evaluate.variable.IValueType;
import ruiseki.omoshiroikamo.api.ids.part.IPartState;
import ruiseki.omoshiroikamo.api.ids.part.aspect.IAspect;
import ruiseki.omoshiroikamo.api.ids.part.aspect.IAspectRead;
import ruiseki.omoshiroikamo.api.ids.part.aspect.IAspectVariable;

/**
 * A value holder for an {@link IPartTypeReader}.
 * This is what will be serialized from and to NBT.
 * This object is mutable and should not be recreated.
 * 
 * @author rubensworks
 */
public interface IPartStateReader<P extends IPartTypeReader> extends IPartState<P> {

    /**
     * Get the singleton variable for an aspect.
     * This only retrieves the previously stored state.
     * Better to call {@link IPartTypeReader#getVariable(PartTarget, IPartStateReader, IAspectRead)}.
     * 
     * @param aspect The aspect from the part of this state.
     * @param <V>    The value type
     * @param <T>    The value type type
     * @return The variable that exists only once for an aspect in this part state.
     */
    public <V extends IValue, T extends IValueType<V>> IAspectVariable<V> getVariable(IAspectRead<V, T> aspect);

    /**
     * Get the singleton variable for an aspect.
     * 
     * @param aspect   The aspect from the part of this state.
     * @param variable The variable that exists only once for an aspect in this part state.
     */
    public void setVariable(IAspect aspect, IAspectVariable variable);

}
