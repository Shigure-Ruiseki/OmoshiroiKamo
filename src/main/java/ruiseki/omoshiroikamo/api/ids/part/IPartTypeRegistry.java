package ruiseki.omoshiroikamo.api.ids.part;

import java.util.Collection;

import ruiseki.omoshiroikamo.core.init.IInitListener;
import ruiseki.omoshiroikamo.core.init.IRegistry;

/**
 * Registry for {@link IPartType}.
 *
 * @author rubensworks
 */
public interface IPartTypeRegistry extends IInitListener, IRegistry {

    /**
     * Register a new part type.
     *
     * @param partType The part type.
     * @param <P>      The part type.
     * @param <S>      The state type.
     * @return The registered part type.
     */
    public <P extends IPartType<P, S>, S extends IPartState<P>> P register(P partType);

    /**
     * @return All registered part types.
     */
    public Collection<IPartType> getPartTypes();

    /**
     * Get the part type by unique name.
     *
     * @param partName The unique part type name.
     * @return The associated part type or null.
     */
    public IPartType getPartType(String partName);

}
