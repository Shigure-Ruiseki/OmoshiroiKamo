package ruiseki.omoshiroikamo.module.ids.common.part.read;

import ruiseki.omoshiroikamo.module.ids.common.part.IPart;
import ruiseki.omoshiroikamo.module.ids.common.part.IPartState;

/**
 * A part type for readers.
 * 
 * @author rubensworks
 */
public interface IPartReader<P extends IPartReader<P, S>, S extends IPartState<P>> extends IPart<P, S> {
}
