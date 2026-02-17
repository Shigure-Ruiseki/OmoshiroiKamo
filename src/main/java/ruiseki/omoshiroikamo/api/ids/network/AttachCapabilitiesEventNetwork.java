package ruiseki.omoshiroikamo.api.ids.network;

import java.util.List;

import com.google.common.collect.Lists;

import ruiseki.omoshiroikamo.api.capabilities.AttachCapabilitiesEvent;

/**
 * Event fired when an INetwork is constructed.
 * Allows adding IFullNetworkListener instances to the network.
 */
public class AttachCapabilitiesEventNetwork extends AttachCapabilitiesEvent<INetwork> {

    private final List<IFullNetworkListener> fullNetworkListeners;

    public AttachCapabilitiesEventNetwork(INetwork network) {
        super(INetwork.class, network);
        this.fullNetworkListeners = Lists.newArrayList();
    }

    public INetwork getNetwork() {
        return getObject();
    }

    public void addFullNetworkListener(IFullNetworkListener fullNetworkListener) {
        this.fullNetworkListeners.add(fullNetworkListener);
    }

    public List<IFullNetworkListener> getFullNetworkListeners() {
        return fullNetworkListeners;
    }
}
