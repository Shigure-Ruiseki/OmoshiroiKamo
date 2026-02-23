package ruiseki.omoshiroikamo.module.ids.common.part.write;

import ruiseki.omoshiroikamo.module.ids.common.part.IPart;
import ruiseki.omoshiroikamo.module.ids.common.part.IPartState;

/**
 * A part type for writers.
 * 
 * @author rubensworks
 */
public interface IPartWriter<P extends IPart<P, S>, S extends IPartState<P>> extends IPart<P, S> {
}
