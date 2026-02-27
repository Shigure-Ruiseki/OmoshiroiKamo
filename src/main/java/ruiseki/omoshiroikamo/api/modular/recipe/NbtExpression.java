package ruiseki.omoshiroikamo.api.modular.recipe;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * An expression that retrieves a value from the TileEntity's NBT data.
 */
public class NbtExpression implements IExpression {

    private final String nbtKey;
    private final double defaultValue;

    public NbtExpression(String nbtKey, double defaultValue) {
        this.nbtKey = nbtKey;
        this.defaultValue = defaultValue;
    }

    @Override
    public double evaluate(ConditionContext context) {
        TileEntity te = context.getWorld()
            .getTileEntity(context.getX(), context.getY(), context.getZ());
        if (te != null) {
            NBTTagCompound nbt = new NBTTagCompound();
            te.writeToNBT(nbt);
            if (nbt.hasKey(nbtKey)) {
                // Try to get as double, falling back to float/int if needed
                return nbt.getDouble(nbtKey);
            }
        }
        return defaultValue;
    }
}
