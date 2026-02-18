package ruiseki.omoshiroikamo.module.ids.common.network;

import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import ruiseki.omoshiroikamo.api.block.BlockPos;
import ruiseki.omoshiroikamo.api.ids.block.cable.ICable;
import ruiseki.omoshiroikamo.api.ids.network.IEventListenableNetworkElement;
import ruiseki.omoshiroikamo.api.ids.network.INetwork;
import ruiseki.omoshiroikamo.api.ids.network.INetworkCarrier;
import ruiseki.omoshiroikamo.api.ids.network.INetworkElement;
import ruiseki.omoshiroikamo.api.ids.network.INetworkElementProvider;
import ruiseki.omoshiroikamo.api.ids.network.INetworkEventListener;
import ruiseki.omoshiroikamo.api.ids.network.event.INetworkEvent;
import ruiseki.omoshiroikamo.api.ids.network.event.INetworkEventBus;
import ruiseki.omoshiroikamo.api.ids.path.ICablePathElement;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.ids.common.network.event.NetworkElementAddEvent;
import ruiseki.omoshiroikamo.module.ids.common.network.event.NetworkElementRemoveEvent;
import ruiseki.omoshiroikamo.module.ids.common.network.event.NetworkEventBus;
import ruiseki.omoshiroikamo.module.ids.common.path.Cluster;
import ruiseki.omoshiroikamo.module.ids.common.persist.world.NetworkWorldStorage;
import ruiseki.omoshiroikamo.module.ids.common.util.CableHelpers;

/**
 * A network instance that can hold a set of {@link INetworkElement}s.
 * Note that this network only contains references to the relevant data, it does not contain the actual information.
 *
 * @param <N> The materialized type of the network.
 * @author rubensworks
 */
public class Network<N extends INetwork<N>> implements INetwork<N> {

    private Cluster<ICablePathElement> baseCluster;

    private final INetworkEventBus<N> eventBus = new NetworkEventBus<>();
    private final TreeSet<INetworkElement<N>> elements = Sets.newTreeSet();
    private TreeSet<INetworkElement<N>> updateableElements = null;
    private TreeMap<INetworkElement<N>, Integer> updateableElementsTicks = null;

    private volatile boolean killed = false;

    /**
     * This constructor should not be called, except for the process of constructing networks from NBT.
     */
    public Network() {
        this.baseCluster = new Cluster<ICablePathElement>();
        onConstruct();
    }

    /**
     * Create a new network from a given cluster of cables.
     * Each cable will be checked if it is an instance of {@link INetworkElementProvider} and will add all its
     * elements to the network in that case.
     * Each cable that is an instance of {@link IPartContainerFacade}
     * will have the network stored in its part container.
     *
     * @param cables The cables that make up the connections in the network which can potentially provide network
     *               elements.
     */
    public Network(Cluster<ICablePathElement> cables) {
        this.baseCluster = cables;
        onConstruct();
        deriveNetworkElements(baseCluster);
    }

    protected void onConstruct() {

    }

    protected N getMaterializedThis() {
        return (N) this;
    }

    private void deriveNetworkElements(Cluster<ICablePathElement> cables) {
        if (!killIfEmpty()) {
            for (ICablePathElement cable : cables) {
                World world = cable.getPosition()
                    .getWorld();
                BlockPos pos = cable.getPosition()
                    .getBlockPos();
                INetworkElementProvider networkElementProvider = CableHelpers
                    .getInterface(world, pos, INetworkElementProvider.class);
                if (networkElementProvider != null) {
                    for (INetworkElement<N> element : ((INetworkElementProvider<N>) networkElementProvider)
                        .createNetworkElements(world, pos)) {
                        addNetworkElement(element, true);
                    }
                }
                INetworkCarrier<N> networkCarrier = CableHelpers.getInterface(world, pos, INetworkCarrier.class);
                if (networkCarrier != null) {
                    // Correctly remove any previously saved network in this carrier
                    // and set the new network to this.
                    INetwork<N> network = networkCarrier.getNetwork(world, pos);
                    if (network != null) {
                        network.removeCable((ICable) networkCarrier, cable);
                    }
                    networkCarrier.resetCurrentNetwork(world, pos);
                    networkCarrier.setNetwork(getMaterializedThis(), world, pos);
                }
            }
        }
    }

    @Override
    public INetworkEventBus<N> getEventBus() {
        return this.eventBus;
    }

    /**
     * Initialize the network element data.
     */
    public void initialize() {
        initialize(false);
    }

    /**
     * Check if two networks are equal.
     *
     * @param networkA A network.
     * @param networkB Another network.
     * @param <N>      The networkl ty
     * @return If they are equal.
     */
    public static <N extends INetwork<N>> boolean areNetworksEqual(Network<N> networkA, Network<N> networkB) {
        return networkA.elements.containsAll(networkB.elements) && networkA.elements.size() == networkB.elements.size();
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Network && areNetworksEqual(this, (Network) object);
    }

    @Override
    public NBTTagCompound toNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("baseCluster", this.baseCluster.toNBT());
        return tag;
    }

    @Override
    public void fromNBT(NBTTagCompound tag) {
        this.baseCluster.fromNBT(tag.getCompoundTag("baseCluster"));
        deriveNetworkElements(baseCluster);
        initialize(true);
    }

    @Override
    public boolean addNetworkElement(INetworkElement<N> element, boolean networkPreinit) {
        if (getEventBus().postCancelable(new NetworkElementAddEvent.Pre<N>(getMaterializedThis(), element))) {
            elements.add(element);
            if (!element.onNetworkAddition(getMaterializedThis())) {
                elements.remove(element);
                return false;
            }
            if (!networkPreinit) {
                addNetworkElementUpdateable(element);
            }
            if (element instanceof IEventListenableNetworkElement) {
                IEventListenableNetworkElement<N, ?> listenableElement = (IEventListenableNetworkElement<N, ?>) element;
                INetworkEventListener<N, ?> listener = listenableElement.getNetworkEventListener();
                if (listener != null && listener.hasEventSubscriptions()) {
                    for (Class<? extends INetworkEvent<N>> eventType : listener.getSubscribedEvents()) {
                        getEventBus().register(listenableElement, eventType);
                    }
                }
            }
            getEventBus().post(new NetworkElementAddEvent.Post<N>(getMaterializedThis(), element));
            return true;
        }
        return false;
    }

    @Override
    public void addNetworkElementUpdateable(INetworkElement<N> element) {
        if (element.isUpdate()) {
            updateableElements.add(element);
            updateableElementsTicks.put(element, 0);
        }
    }

    @Override
    public boolean removeNetworkElementPre(INetworkElement<N> element) {
        return getEventBus().postCancelable(new NetworkElementRemoveEvent.Pre<N>(getMaterializedThis(), element));
    }

    @Override
    public void removeNetworkElementPost(INetworkElement<N> element) {
        if (element instanceof IEventListenableNetworkElement) {
            IEventListenableNetworkElement<N, ?> listenableElement = (IEventListenableNetworkElement<N, ?>) element;
            INetworkEventListener<N, ?> listener = listenableElement.getNetworkEventListener();
            if (listener != null && listener.hasEventSubscriptions()) {
                getEventBus().unregister(listenableElement);
            }
        }
        element.beforeNetworkKill(getMaterializedThis());
        element.onNetworkRemoval(getMaterializedThis());
        elements.remove(element);
        removeNetworkElementUpdateable(element);
        getEventBus().post(new NetworkElementRemoveEvent.Post<N>(getMaterializedThis(), element));
    }

    @Override
    public void removeNetworkElementUpdateable(INetworkElement element) {
        updateableElements.remove(element);
        updateableElementsTicks.remove(element);
    }

    /**
     * Called when a network is server-loaded or newly created.
     *
     * @param silent If the element should not be notified for the network becoming alive.
     */
    protected void initialize(boolean silent) {
        updateableElements = Sets.newTreeSet();
        updateableElementsTicks = Maps.newTreeMap();
        for (INetworkElement<N> element : elements) {
            addNetworkElementUpdateable(element);
            if (!silent) {
                element.afterNetworkAlive(getMaterializedThis());
            }
            element.afterNetworkReAlive(getMaterializedThis());
        }
    }

    @Override
    public void kill() {
        for (INetworkElement<N> element : elements) {
            element.beforeNetworkKill(getMaterializedThis());
        }
        killed = true;
    }

    @Override
    public boolean killIfEmpty() {
        if (baseCluster.isEmpty()) {
            kill();
            return true;
        }
        return false;
    }

    protected boolean canUpdate(INetworkElement<N> element) {
        return true;
    }

    protected void postUpdate(INetworkElement<N> element) {

    }

    /**
     * When the given element is not being updated because {@link Network#canUpdate(INetworkElement)}
     * returned false.
     *
     * @param element The element that is not being updated.
     */
    protected void onSkipUpdate(INetworkElement<N> element) {

    }

    @Override
    public final void update() {
        if (killIfEmpty() || killed) {
            NetworkWorldStorage.getInstance(LibMisc.MOD_ID)
                .removeInvalidatedNetwork(this);
        } else {
            onUpdate();

            // Update updateable network elements
            for (INetworkElement<N> element : updateableElements) {
                if (canUpdate(element)) {
                    if (updateableElementsTicks.get(element) <= 0) {
                        updateableElementsTicks.put(element, element.getUpdateInterval());
                        element.update(getMaterializedThis());
                        postUpdate(element);
                    }
                } else {
                    onSkipUpdate(element);
                }
                updateableElementsTicks.put(element, updateableElementsTicks.get(element) - 1);
            }
        }
    }

    protected void onUpdate() {

    }

    @Override
    public boolean removeCable(ICable cable, ICablePathElement cablePathElement) {
        if (baseCluster.remove(cablePathElement)) {
            if (cable instanceof INetworkElementProvider) {
                Collection<INetworkElement<N>> networkElements = ((INetworkElementProvider<N>) cable)
                    .createNetworkElements(
                        cablePathElement.getPosition()
                            .getWorld(),
                        cablePathElement.getPosition()
                            .getBlockPos());
                for (INetworkElement<N> networkElement : networkElements) {
                    if (!removeNetworkElementPre(networkElement)) {
                        return false;
                    }
                }
                for (INetworkElement<N> networkElement : networkElements) {
                    removeNetworkElementPost(networkElement);
                }
                return true;
            }
        } else {
            Thread.dumpStack();
            Logger.warn("Tried to remove a cable from a network it was not present in.");
            System.out.println("Cluster: " + baseCluster);
            System.out.println("Tried removing element: " + cablePathElement);
        }
        return false;
    }

    @Override
    public void afterServerLoad() {

    }

    @Override
    public void beforeServerStop() {

    }

    @Override
    public Set<INetworkElement<N>> getElements() {
        return this.elements;
    }

}
