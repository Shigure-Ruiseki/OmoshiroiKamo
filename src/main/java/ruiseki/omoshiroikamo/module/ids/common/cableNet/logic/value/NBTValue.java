package ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.value;

import net.minecraft.nbt.NBTTagCompound;

import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.type.LogicType;
import ruiseki.omoshiroikamo.module.ids.common.cableNet.logic.type.LogicTypes;

public class NBTValue implements ILogicValue {

    private final NBTTagCompound nbt;

    public NBTValue(NBTTagCompound nbt) {
        this.nbt = nbt;
    }

    @Override
    public LogicType<?> getType() {
        return LogicTypes.NBT;
    }

    @Override
    public Object raw() {
        return nbt;
    }

    @Override
    public boolean asBoolean() {
        return false;
    }

    @Override
    public int asInt() {
        return 0;
    }

    @Override
    public long asLong() {
        return 0;
    }

    @Override
    public float asFloat() {
        return 0;
    }

    @Override
    public double asDouble() {
        return 0;
    }

    @Override
    public String asString() {
        return nbt.toString();
    }

    @Override
    public NBTTagCompound asNBT() {
        return nbt;
    }

    @Override
    public String toString() {
        return "NBT(" + nbt + ")";
    }
}
