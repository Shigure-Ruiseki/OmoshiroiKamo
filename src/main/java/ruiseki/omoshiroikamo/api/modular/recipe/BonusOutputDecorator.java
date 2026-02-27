package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;

/**
 * Decorator that adds a chance for bonus outputs when the recipe is completed.
 */
public class BonusOutputDecorator extends RecipeDecorator {

    private final float baseChance;
    private final List<IRecipeOutput> bonusOutputs;
    private final String modifierKey;
    private final Random rand = new Random();

    public BonusOutputDecorator(IModularRecipe internal, float baseChance, List<IRecipeOutput> bonusOutputs,
        String modifierKey) {
        super(internal);
        this.baseChance = baseChance;
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
            float finalChance = baseChance;
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

    public float getBaseChance() {
        return baseChance;
    }

    public List<IRecipeOutput> getBonusOutputs() {
        return bonusOutputs;
    }
}
