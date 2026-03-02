package ruiseki.omoshiroikamo.api.modular.recipe.decorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.modular.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.expression.ExpressionsParser;
import ruiseki.omoshiroikamo.api.modular.recipe.expression.IExpression;
import ruiseki.omoshiroikamo.api.modular.recipe.io.IRecipeOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.parser.OutputParserRegistry;

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
            double finalChance = baseChanceExpr.evaluate(null);
            // TODO: In the future, fetch modifier value from context or machine state using
            // modifierKey

            if (rand.nextFloat() < finalChance) {
                for (IRecipeOutput bonus : bonusOutputs) {
                    List<IModularPort> filtered = filterByType(outputPorts, bonus.getPortType());
                    // We don't block recipe if bonus fails (e.g. port full), we just attempt to
                    // apply it.
                    // This matches "bonus" behavior.
                    if (bonus.checkCapacity(filtered)) {
                        bonus.apply(filtered);
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
