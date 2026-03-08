package ruiseki.omoshiroikamo.api.condition;

import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.google.gson.JsonObject;

/**
 * Condition that checks the current weather.
 */
public class WeatherCondition implements ICondition {

    public enum Weather {
        CLEAR,
        RAIN,
        THUNDER
    }

    private final Weather weather;

    public WeatherCondition(Weather weather) {
        this.weather = weather;
    }

    @Override
    public boolean isMet(ConditionContext context) {
        World world = context.getWorld();
        if (world == null) return false;

        switch (weather) {
            case THUNDER:
                return world.isThundering();
            case RAIN:
                return world.isRaining();
            case CLEAR:
                return !world.isRaining() && !world.isThundering();
            default:
                return false;
        }
    }

    @Override
    public String getDescription() {
        return StatCollector.translateToLocal(
            "omoshiroikamo.condition.weather." + weather.name()
                .toLowerCase());
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("type", "weather");
        json.addProperty(
            "weather",
            weather.name()
                .toLowerCase());
    }

    public static ICondition fromJson(JsonObject json) {
        String w = json.get("weather")
            .getAsString();
        return new WeatherCondition(Weather.valueOf(w.toUpperCase()));
    }
}
