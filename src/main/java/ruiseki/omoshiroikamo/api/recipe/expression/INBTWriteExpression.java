package ruiseki.omoshiroikamo.api.recipe.expression;

import net.minecraft.nbt.NBTTagCompound;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * Interface for expressions that can write to NBT data.
 * Expressions implementing this interface can modify NBT when evaluated.
 */
public interface INBTWriteExpression extends IExpression {

    /**
     * Apply this expression's NBT modification to the given NBT compound.
     *
     * @param nbt     The NBT compound to modify
     * @param context The condition context for evaluation
     */
    void applyToNBT(NBTTagCompound nbt, ConditionContext context);

    /**
     * Get the NBT key this expression writes to.
     *
     * @return The NBT key name
     */
    String getNBTKey();

    /**
     * Get the operation type (=, +=, -=, *=, /=).
     *
     * @return The operation string
     */
    String getOperation();
}
