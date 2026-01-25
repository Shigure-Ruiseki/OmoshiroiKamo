package ruiseki.omoshiroikamo.module.cable.common.network.logic.value;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.omoshiroikamo.api.block.BlockStack;

public class LogicValues {

    public static final ILogicValue NULL = new NullValue();

    public static ILogicValue of(boolean value) {
        return new BooleanValue(value);
    }

    public static ILogicValue of(int value) {
        return new IntValue(value);
    }

    public static ILogicValue of(long value) {
        return new LongValue(value);
    }

    public static ILogicValue of(float value) {
        return new FloatValue(value);
    }

    public static ILogicValue of(double value) {
        return new DoubleValue(value);
    }

    public static ILogicValue of(String value) {
        return new StringValue(value);
    }

    public static ILogicValue of(BlockStack value) {
        return new BlockValue(value);
    }

    public static ILogicValue of(ItemStack value) {
        return NULL;
    }

    public static ILogicValue of(FluidStack value) {
        return NULL;
    }

    private LogicValues() {}
}
