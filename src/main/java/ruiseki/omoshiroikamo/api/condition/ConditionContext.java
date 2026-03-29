package ruiseki.omoshiroikamo.api.condition;

import net.minecraft.world.World;

import ruiseki.omoshiroikamo.api.recipe.context.IRecipeContext;

/**
 * Represents a context in which a condition is checked.
 */
public class ConditionContext {

    private final World world;
    private final int x;
    private final int y;
    private final int z;
    private final IRecipeContext recipeContext;
    private final long evaluationSeed;

    public ConditionContext(World world, int x, int y, int z, IRecipeContext recipeContext, long evaluationSeed) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.recipeContext = recipeContext;
        this.evaluationSeed = evaluationSeed;
    }

    public ConditionContext(World world, int x, int y, int z, IRecipeContext recipeContext) {
        this(world, x, y, z, recipeContext, 0L);
    }

    public ConditionContext(World world, int x, int y, int z) {
        this(world, x, y, z, null, 0L);
    }

    public World getWorld() {
        return world;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public IRecipeContext getRecipeContext() {
        return recipeContext;
    }

    /**
     * Get a stable seed for deterministic evaluation during this check.
     */
    public long getEvaluationSeed() {
        return evaluationSeed;
    }
}
