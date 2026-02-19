package ruiseki.omoshiroikamo.api.ids.evaluate.variable;

import ruiseki.omoshiroikamo.api.ids.evaluate.EvaluationException;

/**
 * Facade through which a value can be retrieved.
 * 
 * @author rubensworks
 */
public interface IVariable<V extends IValue> {

    /**
     * @return The type of value this variable provides.
     */
    public IValueType<V> getType();

    /**
     * @return The current value of this variable.
     * @throws EvaluationException If something went wrong while evaluating
     */
    public V getValue() throws EvaluationException;

}
