package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.Random;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * Decorator that adds a random chance for the recipe to meet its conditions.
 */
public class ChanceRecipeDecorator extends RecipeDecorator {

    private final float chance;
    private final Random rand = new Random();

    public ChanceRecipeDecorator(IModularRecipe internal, float chance) {
        super(internal);
        this.chance = chance;
    }

    @Override
    public boolean isConditionMet(ConditionContext context) {
        // First check internal conditions, then roll for chance
        return internal.isConditionMet(context) && rand.nextFloat() < chance;
    }

    public float getChance() {
        return chance;
    }
}
