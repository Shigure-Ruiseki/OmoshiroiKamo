package ruiseki.omoshiroikamo.core.tileentity;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.common.util.ForgeDirection;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import ruiseki.omoshiroikamo.core.capabilities.Capability;
import ruiseki.omoshiroikamo.core.event.OKEventFactory;

public abstract class AbstractSideCapabilityTE extends TileEntityOK {

    private Map<Pair<Capability<?>, ForgeDirection>, Object> capabilities = Maps.newHashMap();

    public AbstractSideCapabilityTE() {
        OKEventFactory.attachCapability(this);
    }

    /**
     * When the tile is loaded or created.
     */
    @Override
    public void onLoad() {
        if (capabilities instanceof HashMap) {
            capabilities = ImmutableMap.copyOf(capabilities);
        }
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
    public boolean hasCapability(@NotNull Capability<?> capability, ForgeDirection facing) {
        return capabilities.containsKey(Pair.<Capability<?>, ForgeDirection>of(capability, facing))
            || (facing != null && capabilities.containsKey(Pair.<Capability<?>, ForgeDirection>of(capability, null)));
    }

    @Override
    public <T> T getCapability(@NotNull Capability<T> capability, ForgeDirection facing) {
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
