package ruiseki.omoshiroikamo.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

import org.intellij.lang.annotations.MagicConstant;

import com.gtnewhorizon.gtnhlib.capability.CapabilityProvider;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import ruiseki.omoshiroikamo.api.energy.capability.EnergyIO;
import ruiseki.omoshiroikamo.api.energy.capability.EnergySink;
import ruiseki.omoshiroikamo.api.energy.capability.EnergySource;
import ruiseki.omoshiroikamo.api.energy.capability.WrappedEnergyIO;
import ruiseki.omoshiroikamo.api.energy.capability.cofh.CoFHEnergyHandler;
import ruiseki.omoshiroikamo.api.energy.capability.cofh.CoFHEnergyProvider;
import ruiseki.omoshiroikamo.api.energy.capability.cofh.CoFHEnergyReceiver;
import ruiseki.omoshiroikamo.api.energy.capability.ic2.IC2EnergySink;
import ruiseki.omoshiroikamo.api.energy.capability.ic2.IC2EnergySource;
import ruiseki.omoshiroikamo.api.energy.capability.ok.OKEnergyIO;
import ruiseki.omoshiroikamo.api.energy.capability.ok.OKEnergySink;
import ruiseki.omoshiroikamo.api.energy.capability.ok.OKEnergySource;
import ruiseki.omoshiroikamo.config.general.energy.EnergyConfig;

public class EnergyUtils {

    private static int counter = 0;
    public static final int WRAP_HANDLER = 0b1 << counter++;
    public static final int FOR_INSERTS = 0b1 << counter++;
    public static final int FOR_EXTRACTS = 0b1 << counter++;
    public static final int WRAP_COFH = 0b1 << counter++;
    public static final int WRAP_IC2 = 0b1 << counter++;
    public static final int DEFAULT = WRAP_HANDLER | FOR_INSERTS | FOR_EXTRACTS | WRAP_COFH | WRAP_IC2;

    public static EnergySource getEnergySource(Object obj, ForgeDirection side) {
        return getEnergySource(obj, side, DEFAULT);
    }

    public static EnergySource getEnergySource(Object obj, ForgeDirection side,
        @MagicConstant(flagsFromClass = EnergyUtils.class) int usage) {
        if ((usage & FOR_EXTRACTS) == 0) {
            return null;
        }

        if (obj instanceof EnergySource source) {
            return source;
        }

        if (obj instanceof CapabilityProvider capabilityProvider) {
            EnergySource source = capabilityProvider.getCapability(EnergySource.class, side);

            if (source != null) {
                return source;
            }
        }

        if (obj instanceof IEnergySource provider) {
            EnergySource source = new OKEnergySource(provider, side);

            if (source != null) {
                return source;
            }
        }

        if ((usage & WRAP_COFH) != 0) {
            EnergyIO cofh = wrapCoFHEnergy(obj, side);
            if (cofh != null) {
                return cofh;
            }
        }

        if ((usage & WRAP_IC2) != 0 && EnergyConfig.ic2Capability) {
            EnergyIO ic2 = wrapIC2Energy(obj, side);
            if (ic2 != null) {
                return ic2;
            }
        }

        return null;
    }

    public static EnergySink getEnergySink(Object obj, ForgeDirection side) {
        return getEnergySink(obj, side, DEFAULT);
    }

    public static EnergySink getEnergySink(Object obj, ForgeDirection side,
        @MagicConstant(flagsFromClass = EnergyUtils.class) int usage) {
        if ((usage & FOR_INSERTS) == 0) {
            return null;
        }

        if (obj instanceof EnergySink sink) {
            return sink;
        }

        if (obj instanceof CapabilityProvider capabilityProvider) {
            EnergySink sink = capabilityProvider.getCapability(EnergySink.class, side);

            if (sink != null) {
                return sink;
            }
        }

        if (obj instanceof IEnergySink receiver) {
            EnergySink source = new OKEnergySink(receiver, side);

            if (source != null) {
                return source;
            }
        }

        if ((usage & WRAP_COFH) != 0) {
            EnergyIO cofh = wrapCoFHEnergy(obj, side);
            if (cofh != null) {
                return cofh;
            }
        }

        if ((usage & WRAP_IC2) != 0 && EnergyConfig.ic2Capability) {
            EnergyIO ic2 = wrapIC2Energy(obj, side);
            if (ic2 != null) {
                return ic2;
            }
        }

        return null;
    }

    public static EnergyIO getEnergyIO(Object obj, ForgeDirection side) {
        return getEnergyIO(obj, side, DEFAULT);
    }

    public static EnergyIO getEnergyIO(Object obj, ForgeDirection side,
        @MagicConstant(flagsFromClass = EnergyUtils.class) int usage) {
        if (obj instanceof EnergyIO energyIO) {
            return energyIO;
        }

        if (obj instanceof IEnergyIO ioHandler) {
            return new OKEnergyIO(ioHandler, side);
        }

        if (obj instanceof CapabilityProvider capabilityProvider) {
            EnergyIO energyIO = capabilityProvider.getCapability(EnergyIO.class, side);

            if (energyIO != null) {
                return energyIO;
            }
        }

        if ((usage & WRAP_COFH) != 0) {
            EnergyIO cofh = wrapCoFHEnergy(obj, side);
            if (cofh != null) {
                return cofh;
            }
        }

        if ((usage & WRAP_IC2) != 0 && EnergyConfig.ic2Capability) {
            EnergyIO ic2 = wrapIC2Energy(obj, side);
            if (ic2 != null) {
                return ic2;
            }
        }

        EnergySource source = getEnergySource(obj, side, usage);
        EnergySink sink = getEnergySink(obj, side, usage);

        if (source == null && sink == null) {
            return null;
        }

        return new WrappedEnergyIO(source, sink);
    }

    public static EnergyIO wrapCoFHEnergy(Object obj, ForgeDirection side) {

        try {
            // IEnergyReceiver
            Class<?> receiverClass = Class.forName("cofh.api.energy.IEnergyReceiver");
            if (receiverClass.isInstance(obj)) {
                return new CoFHEnergyReceiver((IEnergyReceiver) obj, side);
            }
        } catch (ClassNotFoundException ignored) {}

        try {
            // IEnergyProvider
            Class<?> providerClass = Class.forName("cofh.api.energy.IEnergyProvider");
            if (providerClass.isInstance(obj)) {
                return new CoFHEnergyProvider((IEnergyProvider) obj, side);
            }
        } catch (ClassNotFoundException ignored) {}

        try {
            // IEnergyHandler
            Class<?> handlerClass = Class.forName("cofh.api.energy.IEnergyHandler");
            if (handlerClass.isInstance(obj)) {
                return new CoFHEnergyHandler((IEnergyHandler) obj, side);
            }
        } catch (ClassNotFoundException ignored) {}

        return null;
    }

    public static EnergyIO wrapIC2Energy(Object obj, ForgeDirection side) {
        if (obj == null) {
            return null;
        }

        // IC2 IEnergySink
        try {
            Class<?> sinkClass = Class.forName("ic2.api.energy.tile.IEnergySink");
            if (sinkClass.isInstance(obj)) {
                return new IC2EnergySink((ic2.api.energy.tile.IEnergySink) obj, side);
            }
        } catch (ClassNotFoundException ignored) {}

        // IC2 IEnergySource
        try {
            Class<?> sourceClass = Class.forName("ic2.api.energy.tile.IEnergySource");
            if (sourceClass.isInstance(obj)) {
                return new IC2EnergySource((ic2.api.energy.tile.IEnergySource) obj, side);
            }
        } catch (ClassNotFoundException ignored) {}

        return null;
    }
}
