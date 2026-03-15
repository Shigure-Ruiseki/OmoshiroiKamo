package ruiseki.omoshiroikamo.api.recipe;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import ruiseki.omoshiroikamo.api.recipe.io.*;
import ruiseki.omoshiroikamo.test.RegistryMocker;

/**
 * Comprehensive integration tests for all major features.
 * Tests verify that JSON recipes can be parsed without errors,
 * covering BlockNBT, Expressions, Conditions, and Decorators.
 */
public class ComprehensiveIntegrationTest {

    private static JsonObject blockNbtRecipes;
    private static JsonObject expressionRecipes;
    private static JsonObject conditionRecipes;
    private static JsonObject decoratorRecipes;
    private static JsonObject tierComponentRecipes;

    @BeforeAll
    public static void loadTestRecipes() throws IOException {
        // Setup registry mocks
        RegistryMocker.mockAll();

        // Load block_nbt_comprehensive_test.json
        InputStream stream1 = ComprehensiveIntegrationTest.class
            .getResourceAsStream("/recipes/block_nbt_comprehensive_test.json");
        assertNotNull(stream1, "block_nbt_comprehensive_test.json should exist");

        InputStreamReader reader1 = new InputStreamReader(stream1);
        blockNbtRecipes = new JsonParser().parse(reader1)
            .getAsJsonObject();
        reader1.close();

        assertNotNull(blockNbtRecipes);
        assertTrue(blockNbtRecipes.has("recipes"));

        // Load expression_comprehensive_test.json
        InputStream stream2 = ComprehensiveIntegrationTest.class
            .getResourceAsStream("/recipes/expression_comprehensive_test.json");
        assertNotNull(stream2, "expression_comprehensive_test.json should exist");

        InputStreamReader reader2 = new InputStreamReader(stream2);
        expressionRecipes = new JsonParser().parse(reader2)
            .getAsJsonObject();
        reader2.close();

        assertNotNull(expressionRecipes);
        assertTrue(expressionRecipes.has("recipes"));

        // Load condition_comprehensive_test.json
        InputStream stream3 = ComprehensiveIntegrationTest.class
            .getResourceAsStream("/recipes/condition_comprehensive_test.json");
        assertNotNull(stream3, "condition_comprehensive_test.json should exist");

        InputStreamReader reader3 = new InputStreamReader(stream3);
        conditionRecipes = new JsonParser().parse(reader3)
            .getAsJsonObject();
        reader3.close();

        assertNotNull(conditionRecipes);
        assertTrue(conditionRecipes.has("recipes"));

        // Load decorator_comprehensive_test.json
        InputStream stream4 = ComprehensiveIntegrationTest.class
            .getResourceAsStream("/recipes/decorator_comprehensive_test.json");
        assertNotNull(stream4, "decorator_comprehensive_test.json should exist");

        InputStreamReader reader4 = new InputStreamReader(stream4);
        decoratorRecipes = new JsonParser().parse(reader4)
            .getAsJsonObject();
        reader4.close();

        assertNotNull(decoratorRecipes);
        assertTrue(decoratorRecipes.has("recipes"));

        // Load tier_component_test.json
        InputStream stream5 = ComprehensiveIntegrationTest.class
            .getResourceAsStream("/recipes/tier_component_test.json");
        assertNotNull(stream5, "tier_component_test.json should exist");

        InputStreamReader reader5 = new InputStreamReader(stream5);
        tierComponentRecipes = new JsonParser().parse(reader5)
            .getAsJsonObject();
        reader5.close();

        assertNotNull(tierComponentRecipes);
        assertTrue(tierComponentRecipes.has("recipes"));
    }

    // ========== Block NBT Tests ==========

    @Test
    public void testBlockNbtRecipesLoad() {
        assertNotNull(blockNbtRecipes);
        assertEquals(
            12,
            blockNbtRecipes.getAsJsonArray("recipes")
                .size(),
            "block_nbt_comprehensive_test.json should have 12 recipes");
    }

    @Test
    public void testAllBlockNbtRecipesParseable() {
        for (int i = 0; i < blockNbtRecipes.getAsJsonArray("recipes")
            .size(); i++) {
            JsonObject recipe = blockNbtRecipes.getAsJsonArray("recipes")
                .get(i)
                .getAsJsonObject();

            String recipeName = recipe.get("name")
                .getAsString();

            // Parse all inputs
            if (recipe.has("inputs")) {
                for (int j = 0; j < recipe.getAsJsonArray("inputs")
                    .size(); j++) {
                    JsonObject inputJson = recipe.getAsJsonArray("inputs")
                        .get(j)
                        .getAsJsonObject();
                    try {
                        IRecipeInput input = parseInput(inputJson);
                        assertNotNull(input, "Input should be created for recipe: " + recipeName);
                    } catch (Exception e) {
                        fail("Failed to parse input " + j + " for recipe " + recipeName + ": " + e.getMessage());
                    }
                }
            }

            // Parse all outputs
            if (recipe.has("outputs")) {
                for (int j = 0; j < recipe.getAsJsonArray("outputs")
                    .size(); j++) {
                    JsonObject outputJson = recipe.getAsJsonArray("outputs")
                        .get(j)
                        .getAsJsonObject();
                    try {
                        IRecipeOutput output = parseOutput(outputJson);
                        assertNotNull(output, "Output should be created for recipe: " + recipeName);
                    } catch (Exception e) {
                        fail("Failed to parse output " + j + " for recipe " + recipeName + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    // ========== Expression Tests ==========

    @Test
    public void testExpressionRecipesLoad() {
        assertNotNull(expressionRecipes);
        assertEquals(
            22,
            expressionRecipes.getAsJsonArray("recipes")
                .size(),
            "expression_comprehensive_test.json should have 22 recipes");
    }

    @Test
    public void testAllExpressionRecipesParseable() {
        for (int i = 0; i < expressionRecipes.getAsJsonArray("recipes")
            .size(); i++) {
            JsonObject recipe = expressionRecipes.getAsJsonArray("recipes")
                .get(i)
                .getAsJsonObject();

            String recipeName = recipe.get("name")
                .getAsString();

            // Parse all inputs
            if (recipe.has("inputs")) {
                for (int j = 0; j < recipe.getAsJsonArray("inputs")
                    .size(); j++) {
                    JsonObject inputJson = recipe.getAsJsonArray("inputs")
                        .get(j)
                        .getAsJsonObject();
                    try {
                        IRecipeInput input = parseInput(inputJson);
                        assertNotNull(input, "Input should be created for recipe: " + recipeName);
                    } catch (Exception e) {
                        fail("Failed to parse input " + j + " for recipe " + recipeName + ": " + e.getMessage());
                    }
                }
            }

            // Parse all outputs
            if (recipe.has("outputs")) {
                for (int j = 0; j < recipe.getAsJsonArray("outputs")
                    .size(); j++) {
                    JsonObject outputJson = recipe.getAsJsonArray("outputs")
                        .get(j)
                        .getAsJsonObject();
                    try {
                        IRecipeOutput output = parseOutput(outputJson);
                        assertNotNull(output, "Output should be created for recipe: " + recipeName);
                    } catch (Exception e) {
                        fail("Failed to parse output " + j + " for recipe " + recipeName + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    // ========== Condition Tests ==========

    @Test
    public void testConditionRecipesLoad() {
        assertNotNull(conditionRecipes);
        assertEquals(
            24,
            conditionRecipes.getAsJsonArray("recipes")
                .size(),
            "condition_comprehensive_test.json should have 24 recipes");
    }

    @Test
    public void testAllConditionRecipesParseable() {
        for (int i = 0; i < conditionRecipes.getAsJsonArray("recipes")
            .size(); i++) {
            JsonObject recipe = conditionRecipes.getAsJsonArray("recipes")
                .get(i)
                .getAsJsonObject();

            String recipeName = recipe.get("name")
                .getAsString();

            // Parse all inputs
            if (recipe.has("inputs")) {
                for (int j = 0; j < recipe.getAsJsonArray("inputs")
                    .size(); j++) {
                    JsonObject inputJson = recipe.getAsJsonArray("inputs")
                        .get(j)
                        .getAsJsonObject();
                    try {
                        IRecipeInput input = parseInput(inputJson);
                        assertNotNull(input, "Input should be created for recipe: " + recipeName);
                    } catch (Exception e) {
                        fail("Failed to parse input " + j + " for recipe " + recipeName + ": " + e.getMessage());
                    }
                }
            }

            // Parse all outputs
            if (recipe.has("outputs")) {
                for (int j = 0; j < recipe.getAsJsonArray("outputs")
                    .size(); j++) {
                    JsonObject outputJson = recipe.getAsJsonArray("outputs")
                        .get(j)
                        .getAsJsonObject();
                    try {
                        IRecipeOutput output = parseOutput(outputJson);
                        assertNotNull(output, "Output should be created for recipe: " + recipeName);
                    } catch (Exception e) {
                        fail("Failed to parse output " + j + " for recipe " + recipeName + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    // ========== Decorator Tests ==========

    @Test
    public void testDecoratorRecipesLoad() {
        assertNotNull(decoratorRecipes);
        assertEquals(
            26,
            decoratorRecipes.getAsJsonArray("recipes")
                .size(),
            "decorator_comprehensive_test.json should have 26 recipes");
    }

    @Test
    public void testAllDecoratorRecipesParseable() {
        for (int i = 0; i < decoratorRecipes.getAsJsonArray("recipes")
            .size(); i++) {
            JsonObject recipe = decoratorRecipes.getAsJsonArray("recipes")
                .get(i)
                .getAsJsonObject();

            String recipeName = recipe.get("name")
                .getAsString();

            // Parse all inputs
            if (recipe.has("inputs")) {
                for (int j = 0; j < recipe.getAsJsonArray("inputs")
                    .size(); j++) {
                    JsonObject inputJson = recipe.getAsJsonArray("inputs")
                        .get(j)
                        .getAsJsonObject();
                    try {
                        IRecipeInput input = parseInput(inputJson);
                        assertNotNull(input, "Input should be created for recipe: " + recipeName);
                    } catch (Exception e) {
                        fail("Failed to parse input " + j + " for recipe " + recipeName + ": " + e.getMessage());
                    }
                }
            }

            // Parse all outputs
            if (recipe.has("outputs")) {
                for (int j = 0; j < recipe.getAsJsonArray("outputs")
                    .size(); j++) {
                    JsonObject outputJson = recipe.getAsJsonArray("outputs")
                        .get(j)
                        .getAsJsonObject();
                    try {
                        IRecipeOutput output = parseOutput(outputJson);
                        assertNotNull(output, "Output should be created for recipe: " + recipeName);
                    } catch (Exception e) {
                        fail("Failed to parse output " + j + " for recipe " + recipeName + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    // ========== Tier Component Tests ==========

    @Test
    public void testTierComponentRecipesLoad() {
        assertNotNull(tierComponentRecipes);
        assertEquals(
            8,
            tierComponentRecipes.getAsJsonArray("recipes")
                .size(),
            "tier_component_test.json should have 8 recipes");
    }

    @Test
    public void testAllTierComponentRecipesParseable() {
        for (int i = 0; i < tierComponentRecipes.getAsJsonArray("recipes")
            .size(); i++) {
            JsonObject recipe = tierComponentRecipes.getAsJsonArray("recipes")
                .get(i)
                .getAsJsonObject();

            String recipeName = recipe.get("name")
                .getAsString();

            // Parse all inputs
            if (recipe.has("inputs")) {
                for (int j = 0; j < recipe.getAsJsonArray("inputs")
                    .size(); j++) {
                    JsonObject inputJson = recipe.getAsJsonArray("inputs")
                        .get(j)
                        .getAsJsonObject();
                    try {
                        IRecipeInput input = parseInput(inputJson);
                        assertNotNull(input, "Input should be created for recipe: " + recipeName);
                    } catch (Exception e) {
                        fail("Failed to parse input " + j + " for recipe " + recipeName + ": " + e.getMessage());
                    }
                }
            }

            // Parse all outputs
            if (recipe.has("outputs")) {
                for (int j = 0; j < recipe.getAsJsonArray("outputs")
                    .size(); j++) {
                    JsonObject outputJson = recipe.getAsJsonArray("outputs")
                        .get(j)
                        .getAsJsonObject();
                    try {
                        IRecipeOutput output = parseOutput(outputJson);
                        assertNotNull(output, "Output should be created for recipe: " + recipeName);
                    } catch (Exception e) {
                        fail("Failed to parse output " + j + " for recipe " + recipeName + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    // ========== Helper Methods ==========

    private IRecipeInput parseInput(JsonObject json) {
        // Determine input type and parse accordingly
        if (json.has("item") || json.has("ore")) {
            return ItemInput.fromJson(json);
        } else if (json.has("fluid")) {
            return FluidInput.fromJson(json);
        } else if (json.has("energy")) {
            return EnergyInput.fromJson(json);
        } else if (json.has("mana")) {
            return ManaInput.fromJson(json);
        } else if (json.has("gas")) {
            return GasInput.fromJson(json);
        } else if (json.has("essentia")) {
            return EssentiaInput.fromJson(json);
        } else if (json.has("vis")) {
            return VisInput.fromJson(json);
        } else if (json.has("symbol") || json.has("block") || json.has("replace")) {
            return BlockInput.fromJson(json);
        }
        throw new IllegalArgumentException("Unknown input type: " + json);
    }

    private IRecipeOutput parseOutput(JsonObject json) {
        // Determine output type and parse accordingly
        if (json.has("item")) {
            return ItemOutput.fromJson(json);
        } else if (json.has("fluid")) {
            return FluidOutput.fromJson(json);
        } else if (json.has("energy")) {
            return EnergyOutput.fromJson(json);
        } else if (json.has("mana")) {
            return ManaOutput.fromJson(json);
        } else if (json.has("gas")) {
            return GasOutput.fromJson(json);
        } else if (json.has("essentia")) {
            return EssentiaOutput.fromJson(json);
        } else if (json.has("vis")) {
            return VisOutput.fromJson(json);
        } else if (json.has("symbol") || json.has("block")) {
            return BlockOutput.fromJson(json);
        }
        throw new IllegalArgumentException("Unknown output type: " + json);
    }
}
