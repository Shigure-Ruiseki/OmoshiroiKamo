package ruiseki.omoshiroikamo.api.ids.part.aspect;

import ruiseki.omoshiroikamo.api.ids.evaluate.variable.IValue;
import ruiseki.omoshiroikamo.api.ids.evaluate.variable.IValueType;

/**
 * An instance of a property type with a onLabelPacket.
 * 
 * @author rubensworks
 */
public interface IAspectPropertyTypeInstance<T extends IValueType<V>, V extends IValue> {

    /**
     * @return The value type of this property.
     */
    public T getType();

    /**
     * @return The unique name of this property, also used for localization.
     */
    String getUnlocalizedName();
}
