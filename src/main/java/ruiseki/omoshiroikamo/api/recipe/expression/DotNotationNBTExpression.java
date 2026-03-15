package ruiseki.omoshiroikamo.api.recipe.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * Expression for nested NBT access using dot notation.
 * Examples: display.Name, ench[0].id
 *
 * This expression navigates through NBT hierarchy using path segments.
 */
public class DotNotationNBTExpression implements IExpression {

    private final String fullPath;
    private final List<String> pathSegments;
    private final double defaultValue;

    public DotNotationNBTExpression(String fullPath, List<String> pathSegments) {
        this(fullPath, pathSegments, 0.0);
    }

    public DotNotationNBTExpression(String fullPath, List<String> pathSegments, double defaultValue) {
        this.fullPath = fullPath;
        this.pathSegments = new ArrayList<>(pathSegments);
        this.defaultValue = defaultValue;
    }

    /**
     * Get the full path as a dot-separated string.
     * 
     * @return Full path (e.g., "display.Name")
     */
    public String getFullPath() {
        return fullPath;
    }

    /**
     * Get the list of path segments.
     * 
     * @return Unmodifiable list of path segments (e.g., ["display", "Name"])
     */
    public List<String> getPathSegments() {
        return Collections.unmodifiableList(pathSegments);
    }

    /**
     * Evaluate the expression by navigating through NBT hierarchy.
     * For string values, returns 0 (numeric context).
     * For numeric values, returns the actual value.
     */
    @Override
    public double evaluate(ConditionContext context) {
        NBTBase value = getNestedNBT(context);
        if (value == null) return defaultValue;

        // Convert to double based on NBT type
        if (value instanceof NBTTagDouble) {
            return ((NBTTagDouble) value).func_150286_g(); // getDouble()
        } else if (value instanceof NBTTagFloat) {
            return ((NBTTagFloat) value).func_150288_h(); // getFloat()
        } else if (value instanceof NBTTagInt) {
            return ((NBTTagInt) value).func_150287_d(); // getInt()
        } else if (value instanceof NBTTagString) {
            // String values evaluate to 0 in numeric context
            return 0;
        }

        return defaultValue;
    }

    /**
     * Get the actual NBT value by navigating the path.
     * This is used for string comparison in ComparisonCondition.
     *
     * @param context The condition context
     * @return The NBT value at the path, or null if not found
     */
    public NBTBase getNestedNBT(ConditionContext context) {
        if (context == null || context.getWorld() == null) return null;

        // Get TileEntity position
        ChunkCoordinates pos = new ChunkCoordinates(context.getX(), context.getY(), context.getZ());

        // Get TileEntity and its NBT
        TileEntity te = context.getWorld()
            .getTileEntity(pos.posX, pos.posY, pos.posZ);
        if (te == null) return null;

        NBTTagCompound nbt = new NBTTagCompound();
        te.writeToNBT(nbt);

        // Navigate through path
        return navigateNBTPath(nbt, pathSegments);
    }

    /**
     * Navigate through nested NBT structure using path segments.
     *
     * @param root     The root NBT compound
     * @param segments The path segments to navigate
     * @return The NBT value at the path, or null if not found
     */
    private static NBTBase navigateNBTPath(NBTTagCompound root, List<String> segments) {
        NBTBase current = root;

        for (String segment : segments) {
            if (current instanceof NBTTagCompound) {
                NBTTagCompound compound = (NBTTagCompound) current;
                if (!compound.hasKey(segment)) {
                    return null; // Path not found
                }
                current = compound.getTag(segment);
            } else {
                // Current is not a compound, cannot navigate further
                return null;
            }
        }

        return current;
    }

    @Override
    public String toString() {
        return fullPath;
    }
}
