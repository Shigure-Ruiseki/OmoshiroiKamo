package ruiseki.omoshiroikamo.module.cable.common.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ruiseki.omoshiroikamo.api.cable.ICable;
import ruiseki.omoshiroikamo.api.cable.ICablePart;
import ruiseki.omoshiroikamo.module.cable.CableCommon;

public abstract class AbstractCableNetwork<T extends ICablePart> {

    protected final List<T> parts = new ArrayList<>();
    protected final Class<T> basePartClass;

    protected AbstractCableNetwork(Class<T> basePartClass) {
        this.basePartClass = basePartClass;
    }

    @SuppressWarnings("unchecked")
    public void addPart(ICablePart part) {
        if (part == null) return;
        if (!basePartClass.isInstance(part)) return;

        for (T p : parts) {
            if (p == part || (p.getCable() == part.getCable() && p.getSide() == part.getSide())) {
                return;
            }
        }

        if (parts.isEmpty()) {
            CableCommon.cableNetworkTickHandler.registerNetwork(this);
        }
        parts.add((T) part);
    }

    @SuppressWarnings("unchecked")
    public void removePart(T part) {
        if (part != null) parts.remove(part);
        if (parts.isEmpty()) {
            CableCommon.cableNetworkTickHandler.unregisterNetwork(this);
        }
    }

    public Collection<T> getParts() {
        return parts;
    }

    public void destroyNetwork() {
        for (T part : parts) {
            ICable cable = part.getCable();
            if (cable != null) {
                cable.getNetworks()
                    .remove(basePartClass);
            }
        }

        parts.clear();
        CableCommon.cableNetworkTickHandler.unregisterNetwork(this);
    }

    public void notifyNetworkOfUpdate() {
        for (T part : parts) {
            ICable cable = part.getCable();
            if (cable != null) cable.dirty();
        }
    }

    @SuppressWarnings("unchecked")
    public void addPartsFromCable(ICable cable) {
        if (cable == null) return;
        parts.addAll((Collection<? extends T>) cable.getParts());
        notifyNetworkOfUpdate();
    }

    public void doNetworkTick() {}

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [parts=" + parts.size() + "]";
    }
}
