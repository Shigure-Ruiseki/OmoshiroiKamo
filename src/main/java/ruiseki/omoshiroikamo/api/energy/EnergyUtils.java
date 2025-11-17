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
import ruiseki.omoshiroikamo.api.energy.capability.ok.OKEnergyIO;
import ruiseki.omoshiroikamo.api.energy.capability.ok.OKEnergySink;
import ruiseki.omoshiroikamo.api.energy.capability.ok.OKEnergySource;
import ruiseki.omoshiroikamo.api.energy.capability.WrappedEnergyIO;
import ruiseki.omoshiroikamo.api.energy.capability.cofh.CoFHEnergyHandler;
import ruiseki.omoshiroikamo.api.energy.capability.cofh.CoFHEnergyProvider;
import ruiseki.omoshiroikamo.api.energy.capability.cofh.CoFHEnergyReceiver;
import ruiseki.omoshiroikamo.common.util.lib.LibMods;

public class EnergyUtils {

    public static final String STORED_ENERGY_NBT_KEY = "storedEnergyRF";

    private static int counter = 0;
    public static final int WRAP_HANDLER = 0b1 << counter++;
    public static final int FOR_INSERTS = 0b1 << counter++;
    public static final int FOR_EXTRACTS = 0b1 << counter++;
    public static final int WRAP_COFH = 0b1 << counter++;
    public static final int DEFAULT = WRAP_HANDLER | FOR_INSERTS | FOR_EXTRACTS | WRAP_COFH;

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

        if (obj instanceof IEnergySource provider) {
            EnergySource source = new OKEnergySource(provider, side);

            if (source != null) {
                return source;
            }
        }

        if ((usage & WRAP_COFH) != 0 && obj instanceof IEnergyProvider handler && LibMods.CoFHLib.isLoaded()) {
            return new CoFHEnergyProvider(handler, side);
        }

        if (obj instanceof CapabilityProvider capabilityProvider) {
            EnergySource source = capabilityProvider.getCapability(EnergySource.class, side);

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
                                           @MagicConstant(flagsFromClass = EnergyUtils.class) int usage) {
        if ((usage & FOR_INSERTS) == 0) {
            return null;
        }

        if (obj instanceof EnergySink sink) {
            return sink;
        }

        if (obj instanceof IEnergySink receiver) {
            EnergySink source = new OKEnergySink(receiver, side);

            if (source != null) {
                return source;
            }
        }

        if ((usage & WRAP_COFH) != 0 && obj instanceof IEnergyReceiver handler && LibMods.CoFHLib.isLoaded()) {
            return new CoFHEnergyReceiver(handler, side);
        }

        if (obj instanceof CapabilityProvider capabilityProvider) {
            EnergySink sink = capabilityProvider.getCapability(EnergySink.class, side);

            if (sink != null) {
                return sink;
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

        if ((usage & WRAP_COFH) != 0 && obj instanceof IEnergyHandler handler && LibMods.CoFHLib.isLoaded()) {
            return new CoFHEnergyHandler(handler, side);
        }

        if (obj instanceof CapabilityProvider capabilityProvider) {
            EnergyIO energyIO = capabilityProvider.getCapability(EnergyIO.class, side);

            if (energyIO != null) {
                return energyIO;
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
