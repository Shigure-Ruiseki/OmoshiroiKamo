package ruiseki.omoshiroikamo.core.common.event;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import ruiseki.omoshiroikamo.api.capabilities.AttachCapabilitiesEvent;
import ruiseki.omoshiroikamo.api.capabilities.CapabilityDispatcher;
import ruiseki.omoshiroikamo.api.capabilities.ICapabilityCompat;
import ruiseki.omoshiroikamo.api.capabilities.ICapabilityProvider;

public class OKEventFactory {

    public static final Multimap<Class<? extends ICapabilityProvider>, Pair<ICapabilityCompat.ICapabilityReference<?>, ICapabilityCompat<? extends ICapabilityProvider>>> capabilityCompats = HashMultimap
        .create();

    @Nullable
    public static CapabilityDispatcher gatherCapabilities(TileEntity tileEntity) {
        return gatherCapabilities(new AttachCapabilitiesEvent<TileEntity>(TileEntity.class, tileEntity), null);
    }

    @Nullable
    public static CapabilityDispatcher gatherCapabilities(Entity entity) {
        return gatherCapabilities(new AttachCapabilitiesEvent<Entity>(Entity.class, entity), null);
    }

    @Nullable
    public static CapabilityDispatcher gatherCapabilities(Village village) {
        return gatherCapabilities(new AttachCapabilitiesEvent<Village>(Village.class, village), null);
    }

    @Nullable
    public static CapabilityDispatcher gatherCapabilities(ItemStack stack, ICapabilityProvider parent) {
        return gatherCapabilities(new AttachCapabilitiesEvent<ItemStack>(ItemStack.class, stack), parent);
    }

    @Nullable
    public static CapabilityDispatcher gatherCapabilities(World world, ICapabilityProvider parent) {
        return gatherCapabilities(new AttachCapabilitiesEvent<World>(World.class, world), parent);
    }

    @Nullable
    public static CapabilityDispatcher gatherCapabilities(Chunk chunk) {
        return gatherCapabilities(new AttachCapabilitiesEvent<Chunk>(Chunk.class, chunk), null);
    }

    @Nullable
    private static CapabilityDispatcher gatherCapabilities(AttachCapabilitiesEvent<?> event,
        @Nullable ICapabilityProvider parent) {
        MinecraftForge.EVENT_BUS.post(event);
        return event.getCapabilities()
            .size() > 0 || parent != null ? new CapabilityDispatcher(event.getCapabilities(), parent) : null;
    }

    /**
     * Register a new capability compatibility.
     * 
     * @param providerClazz       The capability provider class.
     * @param capabilityReference A reference to the capability.
     * @param capabilityCompat    The compatibility instance, nothing in this will be called unless the capability is
     *                            present.
     * @param <P>                 The capability provider type.
     * @param <C>                 The capability.
     */
    public <P extends ICapabilityProvider, C> void addCapabilityCompat(Class<P> providerClazz,
        ICapabilityCompat.ICapabilityReference<C> capabilityReference, ICapabilityCompat<P> capabilityCompat) {
        capabilityCompats.put(
            providerClazz,
            Pair.<ICapabilityCompat.ICapabilityReference<?>, ICapabilityCompat<? extends ICapabilityProvider>>of(
                capabilityReference,
                capabilityCompat));
    }

    public static void attachCapability(ICapabilityProvider capabilityProvider) {
        for (Map.Entry<Class<? extends ICapabilityProvider>, Pair<ICapabilityCompat.ICapabilityReference<?>, ICapabilityCompat<? extends ICapabilityProvider>>> entry : capabilityCompats
            .entries()) {
            if (entry.getValue()
                .getLeft()
                .getCapability() != null && entry.getKey()
                    .isInstance(capabilityProvider)) {
                attachCapability(
                    (ICapabilityCompat<ICapabilityProvider>) entry.getValue()
                        .getRight(),
                    capabilityProvider);
            }
        }
    }

    protected static void attachCapability(ICapabilityCompat<ICapabilityProvider> compat,
        ICapabilityProvider provider) {
        compat.attach(provider);
    }
}
