package ruiseki.omoshiroikamo.api.modular.recipe.decorator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.condition.ICondition;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.modular.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.io.IRecipeInput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.IRecipeOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.visitor.IRecipeVisitor;

/**
 * RequirementDecorator のユニットテスト
 */
public class RequirementDecoratorTest {

    @Test
    @DisplayName("条件の論理積を検証")
    public void testRequirementLogic() {
        ConditionContext context = new ConditionContext(null, 0, 0, 0);

        // ケース1: 両方True
        IModularRecipe recipeTrue = new StubRecipe(true);
        ICondition condTrue = new StubCondition(true);
        assertTrue(new RequirementDecorator(recipeTrue, condTrue).isConditionMet(context));

        // ケース2: レシピ側のみFalse
        IModularRecipe recipeFalse = new StubRecipe(false);
        assertFalse(new RequirementDecorator(recipeFalse, condTrue).isConditionMet(context));

        // ケース3: 追加条件のみFalse
        ICondition condFalse = new StubCondition(false);
        assertFalse(new RequirementDecorator(recipeTrue, condFalse).isConditionMet(context));
    }

    private static class StubCondition implements ICondition {

        private final boolean result;

        public StubCondition(boolean result) {
            this.result = result;
        }

        @Override
        public boolean isMet(ConditionContext context) {
            return result;
        }

        @Override
        public String getDescription() {
            return "stub";
        }

        @Override
        public void write(JsonObject json) {}
    }

    private static class StubRecipe implements IModularRecipe {

        private final boolean result;

        public StubRecipe(boolean result) {
            this.result = result;
        }

        @Override
        public boolean isConditionMet(ConditionContext context) {
            return result;
        }

        @Override
        public String getRegistryName() {
            return "stub";
        }

        @Override
        public String getRecipeGroup() {
            return "stub";
        }

        @Override
        public String getName() {
            return "stub";
        }

        @Override
        public int getDuration() {
            return 100;
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public List<IRecipeInput> getInputs() {
            return Collections.emptyList();
        }

        @Override
        public List<IRecipeOutput> getOutputs() {
            return Collections.emptyList();
        }

        @Override
        public List<ICondition> getConditions() {
            return Collections.emptyList();
        }

        @Override
        public boolean processInputs(List<IModularPort> inputPorts, boolean simulate) {
            return true;
        }

        @Override
        public boolean processOutputs(List<IModularPort> outputPorts, boolean simulate) {
            return true;
        }

        @Override
        public boolean matchesInput(List<IModularPort> inputPorts) {
            return true;
        }

        @Override
        public boolean canOutput(List<IModularPort> outputPorts) {
            return true;
        }

        @Override
        public IPortType.Type checkOutputCapacity(List<IModularPort> outputPorts) {
            return null;
        }

        @Override
        public void onTick(ConditionContext context) {}

        @Override
        public void accept(IRecipeVisitor visitor) {}
    }
}
