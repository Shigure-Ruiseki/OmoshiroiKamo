package ruiseki.omoshiroikamo.module.ids.common.network;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.Lists;

import ruiseki.omoshiroikamo.api.capabilities.CapabilityDispatcher;
import ruiseki.omoshiroikamo.api.ids.network.AttachCapabilitiesEventNetwork;
import ruiseki.omoshiroikamo.api.ids.network.IFullNetworkListener;
import ruiseki.omoshiroikamo.api.ids.network.INetwork;
import ruiseki.omoshiroikamo.api.ids.path.IPathElement;
import ruiseki.omoshiroikamo.api.ids.path.ISidedPathElement;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.module.ids.common.path.Cluster;
import ruiseki.omoshiroikamo.module.ids.common.path.PathFinder;
import ruiseki.omoshiroikamo.module.ids.common.persist.world.NetworkWorldStorage;

/**
 * A network instance that can hold a set of {@link INetworkElement}s.
 * Note that this network only contains references to the relevant data, it does not contain the actual information.
 *
 * @author rubensworks
 */
public class Network implements INetwork {

    private Cluster baseCluster;

    private final CapabilityDispatcher capabilityDispatcher;
    private IFullNetworkListener[] fullNetworkListeners;

    private NBTTagCompound toRead = null;
    private volatile boolean changed = false;
    private volatile boolean killed = false;

    private boolean crashed = false;

    /**
     * Initiate a full network from the given start position.
     *
     * @param sidedPathElement The sided path element to start from.
     * @return The newly formed network.
     */
    public static Network initiateNetworkSetup(ISidedPathElement sidedPathElement) {
        Network network = new Network(PathFinder.getConnectedCluster(sidedPathElement));
        NetworkWorldStorage.getInstance(LibMisc.MOD_ID)
            .addNewNetwork(network);
        return network;
    }

    /**
     * Check if two networks are equal.
     *
     * @param networkA A network.
     * @param networkB Another network.
     * @return If they are equal.
     */
    // public static boolean areNetworksEqual(Network networkA, Network networkB) {
    // return networkA.elements.containsAll(networkB.elements) && networkA.elements.size() == networkB.elements.size();
    // }

    /**
     * This constructor should not be called, except for the process of constructing networks from NBT.
     */
    public Network() {
        this.baseCluster = new Cluster();
        this.capabilityDispatcher = gatherCapabilities();
        onConstruct();
    }

    /**
     * Create a new network from a given cluster of path elements.
     * Each path element will be checked if it has a {@link INetworkElementProvider} capability at its position
     * and will add all its elements to the network in that case.
     * Each path element that has an {@link org.cyclops.integrateddynamics.api.part.IPartContainer} capability
     * will have the network stored in its part container.
     *
     * @param pathElements The path elements that make up the connections in the network which can potentially provide
     *                     network
     *                     elements.
     */
    public Network(Cluster pathElements) {
        this.baseCluster = pathElements;
        this.capabilityDispatcher = gatherCapabilities();
        onConstruct();
        deriveNetworkElements(baseCluster);
    }

    protected CapabilityDispatcher gatherCapabilities() {
        AttachCapabilitiesEventNetwork event = new AttachCapabilitiesEventNetwork(this);
        MinecraftForge.EVENT_BUS.post(event);
        List<IFullNetworkListener> listeners = event.getFullNetworkListeners();
        this.fullNetworkListeners = listeners.toArray(new IFullNetworkListener[listeners.size()]);
        return event.getCapabilities()
            .size() > 0 ? new CapabilityDispatcher(event.getCapabilities()) : null;
    }

    protected IFullNetworkListener[] gatherFullNetworkListeners() {
        List<IFullNetworkListener> listeners = Lists.newArrayList();

        return listeners.toArray(new IFullNetworkListener[listeners.size()]);
    }

    protected void onConstruct() {

    }

    private void deriveNetworkElements(Cluster pathElements) {
        // if(!killIfEmpty()) {
        // for (ISidedPathElement sidedPathElement : pathElements) {
        // World world = sidedPathElement.getPathElement().getPosition().getWorld();
        // BlockPos pos = sidedPathElement.getPathElement().getPosition().getBlockPos();
        // ForgeDirection side = sidedPathElement.getSide();
        // INetworkCarrier networkCarrier = TileHelpers.getCapability(
        // world, pos, side, NetworkCarrierConfig.CAPABILITY);
        // if (networkCarrier != null) {
        // // Correctly remove any previously saved network in this carrier
        // // and set the new network to this.
        // INetwork network = networkCarrier.getNetwork();
        // if (network != null) {
        // network.removePathElement(sidedPathElement.getPathElement(), side);
        // }
        // networkCarrier.setNetwork(null);
        // networkCarrier.setNetwork(this);
        // }
        // INetworkElementProvider networkElementProvider = TileHelpers.getCapability(
        // world, pos, side, NetworkElementProviderConfig.CAPABILITY);
        // if (networkElementProvider != null) {
        // for(INetworkElement element : networkElementProvider.createNetworkElements(world, pos)) {
        // addNetworkElement(element, true);
        // }
        // }
        // }
        // onNetworkChanged();
        // }
    }

    @Override
    public NBTTagCompound toNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("baseCluster", this.baseCluster.toNBT());
        tag.setBoolean("crashed", this.crashed);
        if (this.capabilityDispatcher != null) {
            tag.setTag("ForgeCaps", this.capabilityDispatcher.serializeNBT());
        }
        return tag;
    }

    @Override
    public void fromNBT(NBTTagCompound tag) {
        this.toRead = tag;
    }

    @Override
    public void kill() {

    }

    @Override
    public void update() {

    }

    @Override
    public boolean removePathElement(IPathElement pathElement, ForgeDirection side) {
        return false;
    }

    @Override
    public void afterServerLoad() {

    }

    @Override
    public void beforeServerStop() {
        for (IFullNetworkListener fullNetworkListener : this.fullNetworkListeners) {
            fullNetworkListener.beforeServerStop();
        }
    }
}
