package ruiseki.omoshiroikamo.module.dml.recipe;

import java.util.ArrayList;
import java.util.List;

public class DMLRecipeRegistry {

    public static final DMLRecipeRegistry INSTANCE = new DMLRecipeRegistry();

    private final List<SimulationChamberRecipe> simulationRecipes = new ArrayList<>();
    private final List<LootFabricatorRecipe> fabricationRecipes = new ArrayList<>();

    private DMLRecipeRegistry() {}

    public void addSimulationRecipe(SimulationChamberRecipe recipe) {
        simulationRecipes.add(recipe);
    }

    public void addFabricationRecipe(LootFabricatorRecipe recipe) {
        fabricationRecipes.add(recipe);
    }

    public List<SimulationChamberRecipe> getSimulationRecipes() {
        return simulationRecipes;
    }

    public List<LootFabricatorRecipe> getFabricationRecipes() {
        return fabricationRecipes;
    }
}
