package ruiseki.omoshiroikamo.core.tileentity;

import java.util.Map;

import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Maps;

import ruiseki.omoshiroikamo.core.capabilities.Capability;
import ruiseki.omoshiroikamo.core.event.OKEventFactory;

public abstract class AbstractSideCapabilityTE extends TileEntityOK {

    private final Map<Pair<Capability<?>, ForgeDirection>, Object> capabilities = Maps.newHashMap();

    public AbstractSideCapabilityTE() {
        OKEventFactory.attachCapability(this);
    }

    public <T> void addCapabilityInternal(Capability<T> capability, T value) {
        capabilities.put(Pair.<Capability<?>, ForgeDirection>of(capability, null), value);
    }

    public <T> void addCapabilitySided(Capability<T> capability, ForgeDirection facing, T value) {
        capabilities.put(Pair.<Capability<?>, ForgeDirection>of(capability, facing), value);
    }

    protected Map<Pair<Capability<?>, ForgeDirection>, Object> getCapabilities() {
        return capabilities;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, ForgeDirection facing) {
        return capabilities.containsKey(Pair.<Capability<?>, ForgeDirection>of(capability, facing))
            || (facing != null && capabilities.containsKey(Pair.<Capability<?>, ForgeDirection>of(capability, null)));
    }

    @Override
    public <T> T getCapability(Capability<T> capability, ForgeDirection facing) {
        Object value = capabilities.get(Pair.<Capability<?>, ForgeDirection>of(capability, facing));
        if (value == null && facing != null) {
            value = capabilities.get(Pair.<Capability<?>, ForgeDirection>of(capability, null));
        }
        if (value != null) {
            return (T) value;
        }
        return null;
    }
}
