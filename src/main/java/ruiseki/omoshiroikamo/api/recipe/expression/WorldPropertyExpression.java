package ruiseki.omoshiroikamo.api.recipe.expression;

import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * Expression that retrieves properties from the world (time, days, moon phase).
 */
public class WorldPropertyExpression implements IExpression {

    private final String property;

    public WorldPropertyExpression(String property) {
        this.property = property;
    }

    @Override
    public double evaluate(ConditionContext context) {
        if (context == null || context.getWorld() == null) return 0;
        World world = context.getWorld();
        int x = context.getX();
        int y = context.getY();
        int z = context.getZ();

        switch (property.toLowerCase()) {
            case "x":
                return x;
            case "y":
                return y;
            case "z":
                return z;
            case "dimension":
                return world.provider.dimensionId;
            case "time":
                // 0 to 23999
                return world.getWorldTime() % 24000;
            case "total_days":
                // Days since world creation
            case "day": // Alias for total_days
                return world.getTotalWorldTime() / 24000;
            case "moon_phase":
            case "moon": // Alias for moon_phase
                // 0 to 7 (0 is full moon)
                return world.provider.getMoonPhase(world.getWorldTime());
            case "light":
                return world.getBlockLightValue(x, y, z);
            case "light_block":
                return world.getSavedLightValue(EnumSkyBlock.Block, x, y, z);
            case "light_sky":
                return world.getSavedLightValue(EnumSkyBlock.Sky, x, y, z);
            case "temp":
                if (!world.blockExists(x, y, z)) return 0.5; // Fallback to standard temp
                return world.getBiomeGenForCoords(x, z)
                    .getFloatTemperature(x, y, z);
            case "humidity":
                if (!world.blockExists(x, y, z)) return 0.5; // Fallback to standard humidity
                return world.getBiomeGenForCoords(x, z)
                    .getFloatRainfall();
            case "is_day":
                return world.isDaytime() ? 1.0 : 0.0;
            case "is_night":
                return world.isDaytime() ? 0.0 : 1.0;
            case "is_raining":
                return world.isRaining() ? 1.0 : 0.0;
            case "is_thundering":
                return world.isThundering() ? 1.0 : 0.0;
            case "can_see_sky":
                return world.canBlockSeeTheSky(x, y, z) ? 1.0 : 0.0;
            case "can_see_void":
                for (int ty = y - 1; ty >= 0; ty--) {
                    if (!world.isAirBlock(x, ty, z)) return 0.0;
                }
                return 1.0;
            case "tick":
                return world.getTotalWorldTime();
            case "recipe_tick":
                return world.getTotalWorldTime() - context.getRecipeContext()
                    .getRecipeStartTick();
            case "progress_tick":
                return context.getRecipeContext()
                    .getMachineState() != null ? context.getRecipeContext()
                        .getMachineState()
                        .getProgress() : 0;
            case "redstone":
                return context.getRecipeContext()
                    .getRedstoneLevel();
            case "seed":
                return context.getEvaluationSeed();
            default:
                return 0;
        }
    }

    public static IExpression fromJson(JsonObject json) {
        String prop = json.get("property")
            .getAsString();
        return new WorldPropertyExpression(prop);
    }
}
