package ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.value;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.type.LogicType;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.type.LogicTypes;

public class FluidStackValue implements ILogicValue {

    private final FluidStack fluid;

    public FluidStackValue(FluidStack fluid) {
        this.fluid = fluid;
    }

    @Override
    public LogicType<?> getType() {
        return LogicTypes.FLUID;
    }

    @Override
    public Object raw() {
        return fluid;
    }

    @Override
    public boolean asBoolean() {
        return fluid != null;
    }

    @Override
    public int asInt() {
        return fluid.getFluidID();
    }

    @Override
    public long asLong() {
        return asInt();
    }

    @Override
    public float asFloat() {
        return asInt();
    }

    @Override
    public double asDouble() {
        return asInt();
    }

    @Override
    public String asString() {
        if (fluid == null) return "null";
        String name = FluidRegistry.getFluidName(fluid);
        return name != null ? name : "unknown";
    }
}
