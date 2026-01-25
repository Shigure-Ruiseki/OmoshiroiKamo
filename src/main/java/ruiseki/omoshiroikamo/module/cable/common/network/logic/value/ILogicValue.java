package ruiseki.omoshiroikamo.module.cable.common.network.logic.value;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.omoshiroikamo.api.block.BlockStack;
import ruiseki.omoshiroikamo.module.cable.common.network.logic.type.LogicType;

public interface ILogicValue {

    LogicType<?> getType();

    Object raw();

    boolean asBoolean();

    int asInt();

    long asLong();

    float asFloat();

    double asDouble();

    String asString();

    default BlockStack asBlockStack() {
        return null;
    }

    default ItemStack asItemStack() {
        return null;
    }

    default FluidStack asFluidStack() {
        return null;
    }
}
