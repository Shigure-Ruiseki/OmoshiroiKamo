package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.Random;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * Decorator that adds a random chance for the recipe to meet its conditions.
 */
public class ChanceRecipeDecorator extends RecipeDecorator {

    private final IExpression chanceExpr;
    private final Random rand = new Random();

    public ChanceRecipeDecorator(IModularRecipe internal, IExpression chanceExpr) {
        super(internal);
        this.chanceExpr = chanceExpr;
    }

    @Override
    public boolean isConditionMet(ConditionContext context) {
        // First check internal conditions, then roll for chance
        double chance = chanceExpr.evaluate(context);
        return internal.isConditionMet(context) && rand.nextFloat() < chance;
    }

    public IExpression getChanceExpression() {
        return chanceExpr;
    }
}
