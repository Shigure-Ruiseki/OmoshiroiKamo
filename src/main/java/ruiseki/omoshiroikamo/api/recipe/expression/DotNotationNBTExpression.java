package ruiseki.omoshiroikamo.api.recipe.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * Expression for nested NBT access using dot notation.
 */
public class DotNotationNBTExpression implements IExpression {

    private final String fullPath;
    private final List<String> pathSegments;
    private final EvaluationValue defaultValue;

    public DotNotationNBTExpression(String fullPath, List<String> pathSegments) {
        this(fullPath, pathSegments, EvaluationValue.ZERO);
    }

    public DotNotationNBTExpression(String fullPath, List<String> pathSegments, EvaluationValue defaultValue) {
        this.fullPath = fullPath;
        this.pathSegments = new ArrayList<>(pathSegments);
        this.defaultValue = defaultValue;
    }

    @Override
    public EvaluationValue evaluate(ConditionContext context) {
        if (context == null || context.getWorld() == null) return defaultValue;

        ChunkCoordinates pos = new ChunkCoordinates(context.getX(), context.getY(), context.getZ());
        String teCacheKey = "te_nbt_" + pos.posX + "_" + pos.posY + "_" + pos.posZ;
        EvaluationValue teNbtValue = context.getCachedValue(teCacheKey);
        NBTTagCompound nbt;

        if (teNbtValue != null && teNbtValue.isNbt() && teNbtValue.asNbt() instanceof NBTTagCompound) {
            nbt = (NBTTagCompound) teNbtValue.asNbt();
        } else {
            TileEntity te = context.getWorld()
                .getTileEntity(pos.posX, pos.posY, pos.posZ);
            if (te == null) return defaultValue;
            nbt = new NBTTagCompound();
            te.writeToNBT(nbt);
            context.setCachedValue(teCacheKey, new EvaluationValue(nbt));
        }

        NBTBase result = navigateNBTPath(nbt, pathSegments);
        return result != null ? new EvaluationValue(result) : defaultValue;
    }

    private static NBTBase navigateNBTPath(NBTTagCompound root, List<String> segments) {
        NBTBase current = root;
        for (String segment : segments) {
            if (current instanceof NBTTagCompound) {
                NBTTagCompound compound = (NBTTagCompound) current;
                if (!compound.hasKey(segment)) return null;
                current = compound.getTag(segment);
            } else {
                return null;
            }
        }
        return current;
    }

    @Override
    public String toString() {
        return fullPath;
    }

    public String getFullPath() {
        return fullPath;
    }

    public List<String> getPathSegments() {
        return Collections.unmodifiableList(pathSegments);
    }

    // Helper used by ComparisonCondition in the past, but now evaluate() is enough
    public NBTBase getNestedNBT(ConditionContext context) {
        EvaluationValue val = evaluate(context);
        return val.isNbt() ? val.asNbt() : null;
    }
}
