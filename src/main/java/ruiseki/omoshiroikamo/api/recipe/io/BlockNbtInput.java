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
 * Recipe input that reads and optionally modifies NBT data of a TileEntity at a
 * symbol position.
 * Supports "set", "add", and "sub" (subtract/consume) operations.
 */
public class BlockNbtInput extends AbstractJsonMaterial implements IRecipeInput {

    private final char symbol;
    private final String key;
    private final String operation; // "set", "add", "sub"
    private final IExpression valueExpr;
    private final boolean consume;
    private final boolean optional;

    public BlockNbtInput(char symbol, String key, String operation, IExpression valueExpr, boolean consume,
        boolean optional) {
        this.symbol = symbol;
        this.key = key;
        this.operation = operation;
        this.valueExpr = valueExpr;
        this.consume = consume;
        this.optional = optional;
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.BLOCK;
    }

    @Override
    public boolean process(List<IModularPort> ports, int multiplier, boolean simulate) {
        IRecipeContext context = findRecipeContext(ports);
        if (context == null) return false;

        World world = context.getWorld();
        List<ChunkCoordinates> positions = context.getSymbolPositions(symbol);

        // If optional, we can proceed even if symbol or positions are missing.
        // However, if it's NOT optional, we must have positions.
        if (positions == null || positions.isEmpty()) return optional;

        if (optional && simulate) return true;

        double requiredValue = valueExpr.evaluate(context.getConditionContext()) * multiplier;
        boolean allSatisfied = true;

        for (ChunkCoordinates pos : positions) {
            TileEntity te = world.getTileEntity(pos.posX, pos.posY, pos.posZ);
            if (te == null) {
                if (!optional) allSatisfied = false;
                continue;
            }

            NBTTagCompound nbt = new NBTTagCompound();
            te.writeToNBT(nbt);

            boolean hasKey = nbt.hasKey(key);
            if (!hasKey && !optional) {
                allSatisfied = false;
                continue;
            }

            double current = nbt.getDouble(key);

            if ("sub".equals(operation)) {
                if (current < requiredValue) {
                    if (!optional) allSatisfied = false;
                } else if (!simulate && consume) {
                    nbt.setDouble(key, current - requiredValue);
                    applyNbt(te, nbt, pos);
                }
            } else if (!simulate && consume) {
                // set or add
                double result;
                if ("add".equals(operation)) {
                    result = current + requiredValue;
                } else {
                    result = requiredValue;
                }
                nbt.setDouble(key, result);
                applyNbt(te, nbt, pos);
            }
        }

        return allSatisfied;
    }

    private void applyNbt(TileEntity te, NBTTagCompound nbt, ChunkCoordinates pos) {
        nbt.setInteger("x", pos.posX);
        nbt.setInteger("y", pos.posY);
        nbt.setInteger("z", pos.posZ);
        te.readFromNBT(nbt);
        te.markDirty();
        te.getWorldObj()
            .markBlockForUpdate(pos.posX, pos.posY, pos.posZ);
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
    public long getRequiredAmount() {
        return 1; // Logic handled in process via valueExpr
    }

    @Override
    public boolean isConsume() {
        return consume;
    }

    @Override
    public void read(JsonObject json) {}

    @Override
    public void write(JsonObject json) {
        json.addProperty("type", "block_nbt");
        json.addProperty("symbol", String.valueOf(symbol));
        json.addProperty("key", key);
        json.addProperty("operation", operation);
        if (consume) json.addProperty("consume", true);
    }

    public static BlockNbtInput fromJson(JsonObject json) {
        char symbol = json.get("symbol")
            .getAsString()
            .charAt(0);
        String key = json.get("key")
            .getAsString();
        String op = json.has("operation") ? json.get("operation")
            .getAsString() : "sub";
        IExpression val = ExpressionsParser.parse(json.get("value"));
        boolean consume = !json.has("consume") || json.get("consume")
            .getAsBoolean();
        boolean optional = json.has("optional") && json.get("optional")
            .getAsBoolean();
        return new BlockNbtInput(symbol, key, op, val, consume, optional);
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }
}
