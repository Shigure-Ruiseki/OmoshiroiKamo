package ruiseki.omoshiroikamo.api.ids.network;

import ruiseki.omoshiroikamo.api.ids.part.IPartContainerFacade;
import ruiseki.omoshiroikamo.api.ids.part.IPartState;
import ruiseki.omoshiroikamo.api.ids.part.IPartType;
import ruiseki.omoshiroikamo.api.ids.part.PartTarget;

/**
 * A part network element.
 *
 * @author rubensworks
 */
public interface IPartNetworkElement<P extends IPartType<P, S>, S extends IPartState<P>>
    extends IEventListenableNetworkElement<IPartNetwork, P> {

    /**
     * @return The part.
     */
    public P getPart();

    /**
     * @return The state for this part.
     */
    public S getPartState();

    /**
     * @return The container in which this part resides.
     */
    public IPartContainerFacade getPartContainerFacade();

    /**
     * @return The target and position of this part.
     */
    public PartTarget getTarget();

}
