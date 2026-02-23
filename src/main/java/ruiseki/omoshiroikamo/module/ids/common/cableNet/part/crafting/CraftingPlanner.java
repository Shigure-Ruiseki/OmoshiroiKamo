package ruiseki.omoshiroikamo.module.ids.common.cableNet.part.crafting;

import java.util.List;

import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

public class CraftingPlanner {

    public static boolean plan(CraftingNetwork network, Object targetKey, long requiredAmount, CraftingPlan plan) {
        return planInternal(
            network,
            targetKey,
            requiredAmount,
            plan,
            new Object2LongOpenHashMap<>(),
            new ObjectOpenHashSet<>());
    }

    private static boolean planInternal(CraftingNetwork network, Object targetKey, long requiredAmount,
        CraftingPlan plan, Object2LongOpenHashMap<Object> simulated, ObjectOpenHashSet<Object> visiting) {

        // detect cycle
        if (visiting.contains(targetKey)) {
            return false;
        }

        long stored = network.getItemNetwork()
            .getAmount(targetKey);
        long alreadyPlanned = simulated.getLong(targetKey);

        long available = stored + alreadyPlanned;

        if (available >= requiredAmount) {
            return true;
        }

        long missing = requiredAmount - available;

        List<ICraftingPattern> patterns = network.getIndex()
            .getPatterns(targetKey);

        if (patterns.isEmpty()) {
            return false;
        }

        ICraftingPattern pattern = patterns.get(0);

        IngredientStack output = pattern.getOutputs()
            .stream()
            .filter(
                o -> o.getKey()
                    .equals(targetKey))
            .findFirst()
            .orElse(null);

        if (output == null) {
            return false;
        }

        long outputAmount = output.getAmount();
        long times = (long) Math.ceil((double) missing / outputAmount);

        visiting.add(targetKey);

        // plan inputs first
        for (IngredientStack input : pattern.getInputs()) {

            long needed = input.getAmount() * times;

            if (!planInternal(network, input.getKey(), needed, plan, simulated, visiting)) {
                visiting.remove(targetKey);
                return false;
            }
        }

        visiting.remove(targetKey);

        // simulate outputs
        for (IngredientStack out : pattern.getOutputs()) {
            simulated.addTo(out.getKey(), out.getAmount() * times);
        }

        plan.addStep(pattern, times);

        return true;
    }
}
