package ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.value;

import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.omoshiroikamo.core.datastructure.BlockStack;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.type.LogicType;

public interface ILogicValue {

    LogicType<?> getType();

    Object raw();

    boolean asBoolean();

    int asInt();

    long asLong();

    float asFloat();

    double asDouble();

    String asString();

    default List<ILogicValue> asList() {
        return Collections.emptyList();
    }

    default BlockStack asBlockStack() {
        return null;
    }

    default ItemStack asItemStack() {
        return null;
    }

    default FluidStack asFluidStack() {
        return null;
    }

    default NBTTagCompound asNBT() {
        return null;
    }
}
