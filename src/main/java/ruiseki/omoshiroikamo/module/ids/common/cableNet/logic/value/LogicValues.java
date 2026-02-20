package ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.value;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.omoshiroikamo.core.datastructure.BlockStack;

public class LogicValues {

    private LogicValues() {}

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

    public static ILogicValue of(List<ILogicValue> value) {
        return new ListValue(value);
    }

    public static ILogicValue of(BlockStack value) {
        return new BlockStackValue(value);
    }

    public static ILogicValue of(ItemStack value) {
        return new ItemStackValue(value);
    }

    public static ILogicValue of(FluidStack value) {
        return new FluidStackValue(value);
    }

    public static ILogicValue of(NBTTagCompound value) {
        return new NBTValue(value);
    }
}
