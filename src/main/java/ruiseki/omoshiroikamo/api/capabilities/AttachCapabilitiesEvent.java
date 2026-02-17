package ruiseki.omoshiroikamo.api.capabilities;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.util.ResourceLocation;

public class AttachCapabilitiesEvent<T> extends Event {

    private final Class<T> type;
    private final T obj;
    private final Map<ResourceLocation, ICapabilityProvider> caps = Maps.newLinkedHashMap();
    private final Map<ResourceLocation, ICapabilityProvider> view = Collections.unmodifiableMap(caps);

    public AttachCapabilitiesEvent(Class<T> type, T obj) {
        this.type = type;
        this.obj = obj;
    }

    public Class<T> getType() {
        return type;
    }

    public T getObject() {
        return obj;
    }


    /**
     * Adds a capability to be attached to this object.
     * Keys MUST be unique, it is suggested that you set the domain to your mod ID.
     * If the capability is an instance of INBTSerializable, this key will be used when serializing this capability.
     *
     * @param key The name of owner of this capability provider.
     * @param cap The capability provider
     */
    public void addCapability(ResourceLocation key, ICapabilityProvider cap)
    {
        if (caps.containsKey(key))
            throw new IllegalStateException("Duplicate Capability Key: " + key  + " " + cap);
        this.caps.put(key, cap);
    }

    /**
     * A unmodifiable view of the capabilities that will be attached to this object.
     */
    public Map<ResourceLocation, ICapabilityProvider> getCapabilities()
    {
        return view;
    }
}
