package ruiseki.omoshiroikamo.module.ids.common.network.logic.value;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import ruiseki.omoshiroikamo.api.block.BlockStack;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.type.LogicType;
import ruiseki.omoshiroikamo.module.ids.common.network.logic.type.LogicTypes;

public class BlockStackValue implements ILogicValue {

    private final BlockStack block;

    public BlockStackValue(BlockStack block) {
        this.block = block;
    }

    @Override
    public LogicType<?> getType() {
        return LogicTypes.BLOCK;
    }

    @Override
    public Object raw() {
        return block;
    }

    @Override
    public boolean asBoolean() {
        return block != null;
    }

    @Override
    public int asInt() {
        return block.getBlockId();
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
        if (block == null) return "null";

        String name = Block.blockRegistry.getNameForObject(block);
        return name != null ? name : "unknown";
    }

    @Override
    public BlockStack asBlockStack() {
        return block;
    }

    @Override
    public ItemStack asItemStack() {
        return block.toItemStack();
    }

    @Override
    public FluidStack asFluidStack() {
        return block.toFluidStack();
    }
}
