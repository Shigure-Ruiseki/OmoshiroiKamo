package ruiseki.omoshiroikamo.api.recipe.decorator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.util.ChunkCoordinates;

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
 * Decorator that randomly selects N positions from all positions with a specific symbol
 * and applies BlockOutput only to selected positions.
 */
public class RandomBlockOutputDecorator extends RecipeDecorator {

    private final IExpression countExpr;
    private final List<BlockOutputSelection> selections;
    private final Random rand = new Random();

    public static class BlockOutputSelection {

        public final char symbol;
        public final BlockOutput output;

        public BlockOutputSelection(char symbol, BlockOutput output) {
            this.symbol = symbol;
            this.output = output;
        }
    }

    public RandomBlockOutputDecorator(IModularRecipe internal, IExpression countExpr,
        List<BlockOutputSelection> selections) {
        super(internal);
        this.countExpr = countExpr;
        this.selections = selections;
    }

    @Override
    public boolean processOutputs(List<IModularPort> outputPorts, boolean simulate) {
        // 1. Process base recipe outputs
        if (!internal.processOutputs(outputPorts, simulate)) {
            return false;
        }

        // 2. Apply random BlockOutput selections
        if (!simulate) {
            IRecipeContext context = findRecipeContext(outputPorts);

            if (context != null) {
                ConditionContext condContext = context.getConditionContext();
                for (BlockOutputSelection selection : selections) {
                    // Evaluate selection count
                    int selectCount = (int) countExpr.evaluateDouble(condContext);

                    // Get all positions for this symbol
                    List<ChunkCoordinates> allPositions = context.getSymbolPositions(selection.symbol);

                    // Randomly select N positions
                    List<ChunkCoordinates> selectedPositions = selectRandomPositions(allPositions, selectCount);

                    // Apply BlockOutput to selected positions only
                    for (ChunkCoordinates pos : selectedPositions) {
                        selection.output.applyAt(context, pos, condContext);
                    }
                }
            }
        }

        return true;
    }

    /**
     * Randomly select N positions from the list.
     */
    private List<ChunkCoordinates> selectRandomPositions(List<ChunkCoordinates> positions, int count) {
        if (positions.isEmpty() || count <= 0) {
            return Collections.emptyList();
        }

        List<ChunkCoordinates> copy = new ArrayList<>(positions);
        Collections.shuffle(copy, rand);

        int actualCount = Math.min(count, copy.size());
        return copy.subList(0, actualCount);
    }

    /**
     * Find IRecipeContext from outputPorts.
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
     * "type": "randomBlockOutput",
     * "count": { "type": "constant", "value": 2 },
     * "selections": [
     * {
     * "symbol": "L",
     * "output": { "symbol": "L", "block": "modid:blockname:meta" }
     * }
     * ]
     * }
     */
    public static IModularRecipe fromJson(IModularRecipe recipe, JsonObject json) {
        IExpression count = ExpressionsParser.parse(json.get("count"));

        List<BlockOutputSelection> selections = new ArrayList<>();
        JsonArray selectionsArray = json.getAsJsonArray("selections");
        for (JsonElement elem : selectionsArray) {
            JsonObject selObj = elem.getAsJsonObject();
            char symbol = selObj.get("symbol")
                .getAsString()
                .charAt(0);
            BlockOutput output = BlockOutput.fromJson(selObj.getAsJsonObject("output"));
            selections.add(new BlockOutputSelection(symbol, output));
        }

        return new RandomBlockOutputDecorator(recipe, count, selections);
    }
}
