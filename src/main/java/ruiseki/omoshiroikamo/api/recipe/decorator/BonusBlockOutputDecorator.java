package ruiseki.omoshiroikamo.api.recipe.decorator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.recipe.context.IRecipeContext;
import ruiseki.omoshiroikamo.api.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionsParser;
import ruiseki.omoshiroikamo.api.recipe.expression.IExpression;
import ruiseki.omoshiroikamo.api.recipe.io.BlockOutput;

/**
 * BlockOutput-specific decorator for applying outputs with probability.
 * Similar to BonusOutputDecorator but uses IRecipeContext instead of IModularPort.
 */
public class BonusBlockOutputDecorator extends RecipeDecorator {

    private final IExpression chanceExpr;
    private final List<BlockOutput> bonusOutputs;
    private final Random rand = new Random();

    public BonusBlockOutputDecorator(IModularRecipe internal, IExpression chanceExpr, List<BlockOutput> bonusOutputs) {
        super(internal);
        this.chanceExpr = chanceExpr;
        this.bonusOutputs = bonusOutputs;
    }

    @Override
    public boolean processOutputs(List<IModularPort> outputPorts, boolean simulate) {
        // 1. Process base recipe outputs
        if (!internal.processOutputs(outputPorts, simulate)) {
            return false;
        }

        // 2. Apply probability check and bonus BlockOutputs
        if (!simulate) {
            // Find IRecipeContext to get ConditionContext for evaluation
            IRecipeContext context = findRecipeContext(outputPorts);
            ConditionContext condContext = context != null ? context.getConditionContext() : null;

            double chance = chanceExpr.evaluate(condContext);

            if (rand.nextDouble() < chance) {
                if (context != null) {
                    // Apply all bonus BlockOutputs
                    for (BlockOutput output : bonusOutputs) {
                        output.apply(context, 1);
                    }
                }
            }
        }

        return true;
    }

    /**
     * Find IRecipeContext from outputPorts.
     * TEMachineController implements both IModularPort and IRecipeContext.
     */
    private IRecipeContext findRecipeContext(List<IModularPort> outputPorts) {
        for (IModularPort port : outputPorts) {
            if (port instanceof IRecipeContext) {
                return (IRecipeContext) port;
            }
        }
        return null;
    }

    /**
     * Create decorator from JSON.
     *
     * Expected format:
     * {
     * "type": "bonusBlockOutput",
     * "chance": { "type": "constant", "value": 0.5 },
     * "outputs": [
     * { "type": "block", "symbol": "L", "block": "modid:blockname:meta", "count": 4 }
     * ]
     * }
     */
    public static IModularRecipe fromJson(IModularRecipe recipe, JsonObject json) {
        IExpression chance = ExpressionsParser.parse(json.get("chance"));

        List<BlockOutput> outputs = new ArrayList<>();
        JsonArray outputsArray = json.getAsJsonArray("outputs");
        for (JsonElement elem : outputsArray) {
            JsonObject outputObj = elem.getAsJsonObject();
            // Only accept block type outputs
            if ("block".equals(
                outputObj.get("type")
                    .getAsString())) {
                outputs.add(BlockOutput.fromJson(outputObj));
            }
        }

        return new BonusBlockOutputDecorator(recipe, chance, outputs);
    }
}
