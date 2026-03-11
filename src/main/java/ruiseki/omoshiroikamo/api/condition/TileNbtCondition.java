package ruiseki.omoshiroikamo.api.condition;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;

import com.google.gson.JsonObject;

/**
 * Condition that checks an NBT value of the TileEntity at the current position.
 * Supports basic numeric comparisons.
 */
public class TileNbtCondition implements ICondition {

    private final String key;
    private final ComparisonOp op;
    private final double value;

    public TileNbtCondition(String key, ComparisonOp op, double value) {
        this.key = key;
        this.op = op;
        this.value = value;
    }

    @Override
    public boolean isMet(ConditionContext context) {
        TileEntity te = context.getWorld()
            .getTileEntity(context.getX(), context.getY(), context.getZ());
        if (te == null) return false;

        NBTTagCompound nbt = new NBTTagCompound();
        te.writeToNBT(nbt);

        if (!nbt.hasKey(key)) return false;

        // Basic support for numeric values.
        // Note: For 1.7.10, NBT handling is a bit primitive but work with numbers.
        double actualValue = nbt.getDouble(key); // getDouble works even if it's Int/Float

        switch (op) {
            case GREATER_THAN:
                return actualValue > value;
            case GREATER_OR_EQUAL:
                return actualValue >= value;
            case LESS_THAN:
                return actualValue < value;
            case LESS_OR_EQUAL:
                return actualValue <= value;
            case EQUAL:
                return actualValue == value;
            default:
                return false;
        }
    }

    @Override
    public String getDescription() {
        return StatCollector.translateToLocalFormatted("omoshiroikamo.condition.tile_nbt", key, op.symbol, value);
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("type", "tile_nbt");
        json.addProperty("key", key);
        json.addProperty(
            "op",
            op.name()
                .toLowerCase());
        json.addProperty("value", value);
    }

    public static ICondition fromJson(JsonObject json) {
        String key = json.get("key")
            .getAsString();
        ComparisonOp op = ComparisonOp.valueOf(
            json.get("op")
                .getAsString()
                .toUpperCase());
        double value = json.get("value")
            .getAsDouble();
        return new TileNbtCondition(key, op, value);
    }

    public enum ComparisonOp {

        GREATER_THAN(">"),
        GREATER_OR_EQUAL(">="),
        LESS_THAN("<"),
        LESS_OR_EQUAL("<="),
        EQUAL("==");

        public final String symbol;

        ComparisonOp(String symbol) {
            this.symbol = symbol;
        }
    }
}
