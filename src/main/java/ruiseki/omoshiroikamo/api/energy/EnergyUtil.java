package ruiseki.omoshiroikamo.api.energy;

import net.minecraftforge.common.util.ForgeDirection;

import org.intellij.lang.annotations.MagicConstant;

import com.gtnewhorizon.gtnhlib.capability.CapabilityProvider;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;

public class EnergyUtil {

    public static final String STORED_ENERGY_NBT_KEY = "storedEnergyRF";

    private static int counter = 0;
    public static final int WRAP_HANDLER = 0b1 << counter++;
    public static final int FOR_INSERTS = 0b1 << counter++;
    public static final int FOR_EXTRACTS = 0b1 << counter++;
    public static final int DEFAULT = WRAP_HANDLER | FOR_INSERTS | FOR_EXTRACTS;

    public static EnergySource getEnergySource(Object obj, ForgeDirection side) {
        return getEnergySource(obj, side, DEFAULT);
    }

    public static EnergySource getEnergySource(Object obj, ForgeDirection side,
        @MagicConstant(flagsFromClass = EnergyUtil.class) int usage) {
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

        if (obj instanceof IEnergyProvider provider) {
            EnergySource source = new OKEnergySource(provider);

            if (source != null) {
                return source;
            }
        }

        return null;
    }

    public static EnergySink getEnergySink(Object obj, ForgeDirection side) {
        return getEnergySink(obj, side, DEFAULT);
    }

    public static EnergySink getEnergySink(Object obj, ForgeDirection side,
        @MagicConstant(flagsFromClass = EnergyUtil.class) int usage) {
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

        if (obj instanceof IEnergyReceiver receiver) {
            EnergySink source = new OKEnergySink(receiver);

            if (source != null) {
                return source;
            }
        }

        return null;
    }

    public static EnergyIO getEnergyIO(Object obj, ForgeDirection side) {
        return getEnergyIO(obj, side, DEFAULT);
    }

    public static EnergyIO getEnergyIO(Object obj, ForgeDirection side,
        @MagicConstant(flagsFromClass = EnergyUtil.class) int usage) {
        if (obj instanceof EnergyIO energyIO) {
            return energyIO;
        }

        if (obj instanceof CapabilityProvider capabilityProvider) {
            EnergyIO energyIO = capabilityProvider.getCapability(EnergyIO.class, side);

            if (energyIO != null) {
                return energyIO;
            }
        }

        if (obj instanceof IEnergyHandler handler) {
            EnergyIO source = new OKEnergyIO(handler);

            if (source != null) {
                return source;
            }
        }

        EnergySource source = getEnergySource(obj, side, usage);
        EnergySink sink = getEnergySink(obj, side, usage);

        if (source == null && sink == null) {
            return null;
        }

        return new WrappedEnergyIO(source, sink);
    }
}
