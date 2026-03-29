package ruiseki.omoshiroikamo.api.recipe.expression;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;

/**
 * An expression that retrieves a value from the TileEntity's NBT data.
 */
public class NbtExpression implements IExpression {

    private final String nbtKey;
    private final EvaluationValue defaultValue;
    private final char symbol;

    public NbtExpression(String nbtKey, EvaluationValue defaultValue, char symbol) {
        this.nbtKey = nbtKey;
        this.defaultValue = defaultValue;
        this.symbol = symbol;
    }

    public NbtExpression(String nbtKey, double defaultValue) {
        this(nbtKey, new EvaluationValue(defaultValue), '\0');
    }

    @Override
    public EvaluationValue evaluate(ConditionContext context) {
        if (context == null || context.getWorld() == null) return defaultValue;

        ChunkCoordinates pos = null;
        if (symbol != '\0' && context.getRecipeContext() != null) {
            List<ChunkCoordinates> positions = context.getRecipeContext()
                .getSymbolPositions(symbol);
            if (positions != null && !positions.isEmpty()) {
                pos = positions.get(0);
            }
        } else {
            pos = new ChunkCoordinates(context.getX(), context.getY(), context.getZ());
        }

        if (pos == null) return defaultValue;

        String teCacheKey = "te_nbt_" + pos.posX + "_" + pos.posY + "_" + pos.posZ;
        EvaluationValue teNbtValue = context.getCachedValue(teCacheKey);
        NBTTagCompound nbt;

        if (teNbtValue != null && teNbtValue.isNbt() && teNbtValue.asNbt() instanceof NBTTagCompound nbttagcompound) {
            nbt = nbttagcompound;
        } else {
            TileEntity te = context.getWorld()
                .getTileEntity(pos.posX, pos.posY, pos.posZ);
            if (te == null) return defaultValue;
            nbt = new NBTTagCompound();
            te.writeToNBT(nbt);
            context.setCachedValue(teCacheKey, new EvaluationValue(nbt));
        }

        if (nbt.hasKey(nbtKey)) {
            return new EvaluationValue(nbt.getTag(nbtKey));
        }

        return defaultValue;
    }

    @Override
    public String toString() {
        return (symbol != '\0' ? symbol + "." : "") + nbtKey;
    }

    public String getNbtKey() {
        return nbtKey;
    }

    public char getSymbol() {
        return symbol;
    }

    public static IExpression fromJson(JsonObject json) {
        String key = json.get("key")
            .getAsString();
        EvaluationValue def = json.has("default") ? new EvaluationValue(
            json.get("default")
                .getAsDouble())
            : EvaluationValue.ZERO;
        char sym = json.has("symbol") ? json.get("symbol")
            .getAsString()
            .charAt(0) : '\0';
        return new NbtExpression(key, def, sym);
    }
}
