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
    private final double defaultValue;
    private final char symbol;

    public NbtExpression(String nbtKey, double defaultValue, char symbol) {
        this.nbtKey = nbtKey;
        this.defaultValue = defaultValue;
        this.symbol = symbol;
    }

    public NbtExpression(String nbtKey, double defaultValue) {
        this(nbtKey, defaultValue, '\0');
    }

    @Override
    public double evaluate(ConditionContext context) {
        if (context == null || context.getWorld() == null) return defaultValue;

        ChunkCoordinates pos = null;
        if (symbol != '\0' && context.getRecipeContext() != null) {
            List<ChunkCoordinates> positions = context.getRecipeContext()
                .getSymbolPositions(symbol);
            if (positions != null && !positions.isEmpty()) {
                pos = positions.get(0); // Take first position for symbol
            }
        } else {
            pos = new ChunkCoordinates(context.getX(), context.getY(), context.getZ());
        }

        if (pos == null) return defaultValue;

        TileEntity te = context.getWorld()
            .getTileEntity(pos.posX, pos.posY, pos.posZ);
        if (te != null) {
            NBTTagCompound nbt = new NBTTagCompound();
            te.writeToNBT(nbt);
            if (nbt.hasKey(nbtKey)) {
                // Try to get as double, falling back to float/int if needed
                return nbt.getDouble(nbtKey);
            }
        }
        return defaultValue;
    }

    public static IExpression fromJson(JsonObject json) {
        String key = json.get("key")
            .getAsString();
        double def = json.has("default") ? json.get("default")
            .getAsDouble() : 0.0;
        char sym = json.has("symbol") ? json.get("symbol")
            .getAsString()
            .charAt(0) : '\0';
        return new NbtExpression(key, def, sym);
    }
}
