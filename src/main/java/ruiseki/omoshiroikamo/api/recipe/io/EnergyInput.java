package ruiseki.omoshiroikamo.api.recipe.io;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.core.RecipeTickResult;
import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionParser;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.energy.IOKEnergySource;

/**
 * perTick=true: Energy consumed every tick during processing.
 * perTick=false: Energy consumed once at recipe start.
 */
public class EnergyInput extends AbstractModularRecipeInput {

    public EnergyInput(int amount, boolean perTick) {
        this.amount = amount;
        this.interval = perTick ? 1 : 0;
    }

    public EnergyInput(int amount) {
        this(amount, true);
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean isPerTick() {
        return interval > 0;
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.ENERGY;
    }

    @Override
    public long getRequiredAmount(ConditionContext context) {
        return evaluateAmount(context);
    }

    @Override
    public long getRequiredAmount() {
        return getRequiredAmount(null);
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port instanceof IOKEnergySource;
    }

    @Override
    protected long consume(IModularPort port, long remaining, boolean simulate, ConditionContext context) {
        IOKEnergySource energyPort = (IOKEnergySource) port;
        int stored = energyPort.getEnergyStored();
        if (stored > 0) {
            int extract = (int) Math.min((long) stored, remaining);
            if (!simulate) {
                energyPort.extractEnergy(ForgeDirection.UNKNOWN, extract, false);
            }
            return (long) extract;
        }
        return 0;
    }

    @Override
    public void read(JsonObject json) {
        readPerTick(json, 1);
        if (json.has("index")) this.index = json.get("index")
            .getAsInt();

        if (json.has("consume")) {
            this.consume = json.get("consume")
                .getAsBoolean();
        }

        if (json.has("energy")) {
            JsonElement energyElement = json.get("energy");
            if (energyElement.isJsonPrimitive() && energyElement.getAsJsonPrimitive()
                .isString()) {
                this.amountExpr = ExpressionParser.parseExpression(energyElement.getAsString());
                this.amount = 0; // Fallback
            } else {
                this.amount = energyElement.getAsInt();
                this.amountExpr = null;
            }
        }
    }

    @Override
    public void write(JsonObject json) {
        if (index != -1) json.addProperty("index", index);
        if (!consume) json.addProperty("consume", false);
        if (amountExpr != null) {
            json.addProperty("energy", amountExpr.toString());
        } else {
            json.addProperty("energy", amount);
        }
        if (interval != 1) {
            if (isPerTick()) {
                json.addProperty("pertick", interval);
            } else {
                json.addProperty("pertick", false);
            }
        }
    }

    @Override
    public boolean validate() {
        return amount > 0;
    }

    public static EnergyInput fromJson(JsonObject json) {
        EnergyInput input = new EnergyInput(0, false);
        input.read(json);
        return input.validate() ? input : null;
    }

    @Override
    public IRecipeInput copy() {
        return copy(1);
    }

    @Override
    public IRecipeInput copy(int multiplier) {
        EnergyInput result = new EnergyInput(amount * multiplier, isPerTick());
        result.amountExpr = this.amountExpr;
        result.interval = this.interval;
        result.consume = this.consume;
        result.index = this.index;
        return result;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "energy");
        nbt.setInteger("amount", amount);
        nbt.setInteger("interval", interval);
        nbt.setBoolean("consume", consume);
        nbt.setInteger("index", index);
        if (amountExpr != null) nbt.setString("amountExpr", amountExpr.toString());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.amount = nbt.getInteger("amount");
        this.interval = nbt.getInteger("interval");
        this.consume = nbt.getBoolean("consume");
        this.index = nbt.hasKey("index") ? nbt.getInteger("index") : -1;
        if (nbt.hasKey("amountExpr")) {
            try {
                this.amountExpr = ExpressionParser.parseExpression(nbt.getString("amountExpr"));
            } catch (Exception e) {
                Logger.error("Failed to restore energy amount expression: " + e.getMessage());
            }
        }
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public RecipeTickResult getFailureResult(boolean perTick) {
        return RecipeTickResult.NO_ENERGY;
    }
}
