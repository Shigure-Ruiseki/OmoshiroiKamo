package ruiseki.omoshiroikamo.api.recipe.decorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.context.IRecipeContext;
import ruiseki.omoshiroikamo.api.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionsParser;
import ruiseki.omoshiroikamo.api.recipe.expression.IExpression;
import ruiseki.omoshiroikamo.api.recipe.io.IModularRecipeOutput;
import ruiseki.omoshiroikamo.api.recipe.io.IRecipeOutput;
import ruiseki.omoshiroikamo.api.recipe.parser.OutputParserRegistry;

/**
 * Decorator that adds a chance for bonus outputs when the recipe is completed.
 */
public class BonusOutputDecorator extends RecipeDecorator {

    private final IExpression baseChanceExpr;
    private final List<IRecipeOutput> bonusOutputs;
    private final String modifierKey;
    private final Random rand = new Random();

    public BonusOutputDecorator(IModularRecipe internal, IExpression baseChanceExpr, List<IRecipeOutput> bonusOutputs,
        String modifierKey) {
        super(internal);
        this.baseChanceExpr = baseChanceExpr;
        this.bonusOutputs = bonusOutputs;
        this.modifierKey = modifierKey;
    }

    @Override
    public boolean processOutputs(List<IModularPort> outputPorts, boolean simulate) {
        // First process original outputs. If they fail, bonus also "fails" (recipe
        // cannot complete)
        if (!internal.processOutputs(outputPorts, simulate)) {
            return false;
        }

        // Only process bonus if we are not simulating
        if (!simulate) {
            // Context needs current machine state
            // TODO: Pass context to processOutputs or store it in TE
            IRecipeContext context = findRecipeContext(outputPorts);
            ConditionContext condContext = context != null ? context.getConditionContext() : null;

            double finalChance = baseChanceExpr.evaluate(condContext);
            // TODO: In the future, fetch modifier value from context or machine state using
            // modifierKey

            if (rand.nextFloat() < finalChance) {
                for (IRecipeOutput bonus : bonusOutputs) {
                    if (bonus instanceof IModularRecipeOutput modularBonus) {
                        List<IModularPort> filtered = filterByType(outputPorts, modularBonus.getPortType());
                        // We don't block recipe if bonus fails (e.g. port full), we just attempt to
                        // apply it.
                        // This matches "bonus" behavior.
                        if (modularBonus.checkCapacity(filtered, 1, condContext)) {
                            modularBonus.apply(filtered, 1, condContext);
                        }
                    }
                }
            }
        }

        return true;
    }

    private List<IModularPort> filterByType(List<IModularPort> ports, IPortType.Type type) {
        List<IModularPort> filtered = new ArrayList<>();
        for (IModularPort port : ports) {
            if (port.getPortType() == type) {
                filtered.add(port);
            }
        }
        return filtered;
    }

    private IRecipeContext findRecipeContext(List<IModularPort> outputPorts) {
        for (IModularPort port : outputPorts) {
            if (port instanceof IRecipeContext context) {
                return context;
            }
        }
        return null;
    }

    public IExpression getBaseChanceExpression() {
        return baseChanceExpr;
    }

    public List<IRecipeOutput> getBonusOutputs() {
        return bonusOutputs;
    }

    public static IModularRecipe fromJson(IModularRecipe recipe, JsonObject json) {
        IExpression chance = ExpressionsParser.parse(json.get("chance"));
        List<IRecipeOutput> outputs = new ArrayList<>();
        JsonArray arr = json.getAsJsonArray("outputs");
        for (JsonElement e : arr) {
            outputs.add(OutputParserRegistry.parse(e.getAsJsonObject()));
        }
        String key = json.has("modifierKey") ? json.get("modifierKey")
            .getAsString() : null;
        return new BonusOutputDecorator(recipe, chance, outputs, key);
    }
}
