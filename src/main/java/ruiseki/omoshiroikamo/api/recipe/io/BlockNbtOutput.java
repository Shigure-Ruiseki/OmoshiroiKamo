package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.context.IRecipeContext;
import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionsParser;
import ruiseki.omoshiroikamo.api.recipe.expression.IExpression;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.json.AbstractJsonMaterial;

/**
 * Recipe output that modifies TileEntity NBT data at structure positions.
 * Supports set, add, and sub operations on numeric values.
 */
public class BlockNbtOutput extends AbstractJsonMaterial implements IRecipeOutput {

    private final char symbol;
    private final String key;
    private final String operation; // "set", "add", "sub"
    private final IExpression valueExpr;
    private int multiplier;
    private double bakedValue = Double.NaN; // For NBT persistence
    private final boolean optional;

    public BlockNbtOutput(char symbol, String key, String operation, IExpression valueExpr, int multiplier,
        boolean optional) {
        this.symbol = symbol;
        this.key = key;
        this.operation = operation;
        this.valueExpr = valueExpr;
        this.multiplier = multiplier;
        this.optional = optional;
    }

    public BlockNbtOutput(char symbol, String key, String operation, IExpression valueExpr, boolean optional) {
        this(symbol, key, operation, valueExpr, 1, optional);
    }

    public BlockNbtOutput(char symbol, String key, String operation, IExpression valueExpr) {
        this(symbol, key, operation, valueExpr, 1, false);
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.BLOCK;
    }

    @Override
    public boolean checkCapacity(List<IModularPort> ports, int multiplier) {
        if (optional) return true;
        IRecipeContext context = findRecipeContext(ports);
        if (context == null) return false;
        List<ChunkCoordinates> positions = context.getSymbolPositions(symbol);
        if (positions == null || positions.isEmpty()) return false;

        // Also check if key exists if not optional?
        World world = context.getWorld();
        for (ChunkCoordinates pos : positions) {
            TileEntity te = world.getTileEntity(pos.posX, pos.posY, pos.posZ);
            if (te == null) return false;

            // Strict NBT key check if not optional
            NBTTagCompound nbt = new NBTTagCompound();
            te.writeToNBT(nbt);
            if (!nbt.hasKey(key)) return false;
        }
        return true;
    }

    @Override
    public void apply(List<IModularPort> ports, int multiplier) {
        IRecipeContext context = findRecipeContext(ports);
        if (context == null) return;

        World world = context.getWorld();
        List<ChunkCoordinates> positions = context.getSymbolPositions(symbol);
        if (positions == null) return;

        double value;
        if (!Double.isNaN(bakedValue)) {
            value = bakedValue;
        } else {
            value = valueExpr.evaluate(context.getConditionContext()) * this.multiplier;
        }

        // Final multiplier (usually 1 from ProcessAgent)
        value *= multiplier;

        for (ChunkCoordinates pos : positions) {
            TileEntity te = world.getTileEntity(pos.posX, pos.posY, pos.posZ);
            if (te != null) {
                NBTTagCompound nbt = new NBTTagCompound();
                te.writeToNBT(nbt);

                double current = nbt.getDouble(key);
                double result;
                if ("add".equals(operation)) {
                    result = current + value;
                } else if ("sub".equals(operation)) {
                    result = current - value;
                } else {
                    result = value;
                }

                nbt.setDouble(key, result);

                // Ensure coordinates are preserved
                nbt.setInteger("x", pos.posX);
                nbt.setInteger("y", pos.posY);
                nbt.setInteger("z", pos.posZ);

                te.readFromNBT(nbt);
                te.markDirty();
                world.markBlockForUpdate(pos.posX, pos.posY, pos.posZ);
            }
        }
    }

    private IRecipeContext findRecipeContext(List<IModularPort> ports) {
        for (IModularPort port : ports) {
            if (port instanceof IRecipeContext) {
                return (IRecipeContext) port;
            }
        }
        return null;
    }

    @Override
    public IRecipeOutput copy() {
        return copy(1);
    }

    @Override
    public IRecipeOutput copy(int multiplier) {
        return new BlockNbtOutput(symbol, key, operation, valueExpr, this.multiplier * multiplier, optional);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "block_nbt");
        nbt.setString("symbol", String.valueOf(symbol));
        nbt.setString("key", key);
        nbt.setString("operation", operation);
        nbt.setInteger("multiplier", multiplier);
        nbt.setBoolean("optional", optional);
        if (!Double.isNaN(bakedValue)) {
            nbt.setDouble("bakedValue", bakedValue);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.multiplier = nbt.getInteger("multiplier");
        if (nbt.hasKey("bakedValue")) {
            this.bakedValue = nbt.getDouble("bakedValue");
        }
    }

    @Override
    public long getRequiredAmount() {
        return 1;
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public void read(JsonObject json) {
        // Immutable
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("type", "block_nbt");
        json.addProperty("symbol", String.valueOf(symbol));
        json.addProperty("key", key);
        json.addProperty("operation", operation);
        if (optional) json.addProperty("optional", true);
    }

    public static BlockNbtOutput fromJson(JsonObject json) {
        char symbol = json.get("symbol")
            .getAsString()
            .charAt(0);
        String key = json.get("key")
            .getAsString();
        String op = json.has("operation") ? json.get("operation")
            .getAsString() : "set";
        IExpression val = ExpressionsParser.parse(json.get("value"));
        boolean optional = json.has("optional") && json.get("optional")
            .getAsBoolean();
        return new BlockNbtOutput(symbol, key, op, val, optional);
    }
}
