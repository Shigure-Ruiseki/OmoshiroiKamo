package ruiseki.omoshiroikamo.core.fluid;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;

import org.intellij.lang.annotations.MagicConstant;

import com.gtnewhorizon.gtnhlib.capability.CapabilityProvider;

import ruiseki.omoshiroikamo.core.fluid.capability.FluidSink;
import ruiseki.omoshiroikamo.core.fluid.capability.FluidSource;
import ruiseki.omoshiroikamo.core.fluid.capability.OKFluidSink;
import ruiseki.omoshiroikamo.core.fluid.capability.OKFluidSource;

public class FluidUtils {

    private static int counter = 0;
    public static final int WRAP_HANDLER = 0b1 << counter++;
    public static final int FOR_INSERTS = 0b1 << counter++;
    public static final int FOR_EXTRACTS = 0b1 << counter++;
    public static final int DEFAULT = WRAP_HANDLER | FOR_INSERTS | FOR_EXTRACTS;

    public static FluidSource getFluidSource(Object obj, ForgeDirection side) {
        return getFluidSource(obj, side, DEFAULT);
    }

    public static FluidSource getFluidSource(Object obj, ForgeDirection side,
        @MagicConstant(flagsFromClass = FluidUtils.class) int usage) {
        if ((usage & FOR_EXTRACTS) == 0) {
            return null;
        }

        if (obj instanceof FluidSource source) {
            return source;
        }

        if (obj instanceof CapabilityProvider capabilityProvider) {
            FluidSource source = capabilityProvider.getCapability(FluidSource.class, side);

            if (source != null) {
                return source;
            }
        }

        if (obj instanceof IFluidHandler handler) {
            FluidSource source = new OKFluidSource(handler, side);
            if (source != null) {
                return source;
            }
        }

        return null;
    }

    public static FluidSink getFluidSink(Object obj, ForgeDirection side) {
        return getFluidSink(obj, side, DEFAULT);
    }

    public static FluidSink getFluidSink(Object obj, ForgeDirection side,
        @MagicConstant(flagsFromClass = FluidUtils.class) int usage) {
        if ((usage & FOR_INSERTS) == 0) {
            return null;
        }

        if (obj instanceof FluidSink sink) {
            return sink;
        }

        if (obj instanceof CapabilityProvider capabilityProvider) {
            FluidSink sink = capabilityProvider.getCapability(FluidSink.class, side);

            if (sink != null) {
                return sink;
            }
        }

        if (obj instanceof IFluidHandler handler) {
            FluidSink source = new OKFluidSink(handler, side);
            if (source != null) {
                return source;
            }
        }
        return null;
    }

}
