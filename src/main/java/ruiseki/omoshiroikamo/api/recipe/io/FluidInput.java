package ruiseki.omoshiroikamo.api.recipe.io;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.core.RecipeTickResult;
import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionParser;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.json.FluidJson;

public class FluidInput extends AbstractModularRecipeInput {

    private FluidStack required;

    public FluidInput(FluidStack required) {
        this.required = required != null ? required.copy() : null;
        if (this.required != null) this.amount = this.required.amount;
    }

    public FluidStack getRequired() {
        return required != null ? required.copy() : null;
    }

    public FluidStack getFluid() {
        return getRequired();
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.FLUID;
    }

    @Override
    public long getRequiredAmount(ConditionContext context) {
        if (amountExpr != null && context != null) {
            return (long) amountExpr.evaluate(context);
        }
        return required != null ? required.amount : amount;
    }

    @Override
    public long getRequiredAmount() {
        return getRequiredAmount(null);
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.FLUID && port instanceof IFluidHandler;
    }

    @Override
    protected long consume(IModularPort port, long remaining, boolean simulate, ConditionContext context) {
        IFluidHandler fluidPort = (IFluidHandler) port;
        FluidStack stored = null;

        FluidTankInfo[] infos = fluidPort.getTankInfo(ForgeDirection.UNKNOWN);
        if (infos != null && infos.length > 0) {
            stored = infos[0].fluid;
        }

        if (stored != null && stored.isFluidEqual(required)) {
            int drainAmount = (int) Math.min(stored.amount, remaining);
            FluidStack drained = fluidPort
                .drain(ForgeDirection.UNKNOWN, new FluidStack(required, drainAmount), !simulate);
            return drained != null ? drained.amount : 0;
        }
        return 0;
    }

    @Override
    public void read(JsonObject json) {
        readPerTick(json, 0);
        if (json.has("index")) this.index = json.get("index")
            .getAsInt();

        if (json.has("consume")) {
            this.consume = json.get("consume")
                .getAsBoolean();
        }

        FluidJson fluidJson = new FluidJson();
        fluidJson.name = json.get("fluid")
            .getAsString();

        if (json.has("amount")) {
            JsonElement amountElement = json.get("amount");
            if (amountElement.isJsonPrimitive() && amountElement.getAsJsonPrimitive()
                .isString()) {
                this.amountExpr = ExpressionParser.parseExpression(amountElement.getAsString());
                this.amount = 1000; // Fallback
            } else {
                this.amount = amountElement.getAsInt();
                this.amountExpr = null;
            }
        } else {
            this.amount = 1000;
            this.amountExpr = null;
        }

        fluidJson.amount = this.amount;
        this.required = FluidJson.resolveFluidStack(fluidJson);
    }

    @Override
    public void write(JsonObject json) {
        if (index != -1) json.addProperty("index", index);
        if (!consume) json.addProperty("consume", false);
        if (interval > 0) json.addProperty("pertick", interval);

        if (required != null && required.getFluid() != null) {
            json.addProperty(
                "fluid",
                required.getFluid()
                    .getName());
            if (amountExpr != null) {
                json.addProperty("amount", amountExpr.toString());
            } else {
                json.addProperty("amount", required.amount);
            }
        }
    }

    @Override
    public boolean validate() {
        return required != null;
    }

    public static FluidInput fromJson(JsonObject json) {
        FluidInput input = new FluidInput(null);
        input.read(json);
        return input;
    }

    @Override
    public IRecipeInput copy() {
        return copy(1);
    }

    @Override
    public IRecipeInput copy(int multiplier) {
        FluidStack copyStack = required != null ? required.copy() : null;
        if (copyStack != null) copyStack.amount *= multiplier;
        FluidInput result = new FluidInput(copyStack);
        result.amount = this.amount * multiplier;
        result.amountExpr = this.amountExpr;
        result.consume = this.consume;
        result.interval = this.interval;
        result.index = this.index;
        return result;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "fluid");
        nbt.setInteger("interval", interval);
        nbt.setBoolean("consume", consume);
        nbt.setInteger("amount", amount);
        nbt.setInteger("index", index);
        if (required != null) {
            NBTTagCompound stackTag = new NBTTagCompound();
            required.writeToNBT(stackTag);
            nbt.setTag("required", stackTag);
        }
        if (amountExpr != null) nbt.setString("amountExpr", amountExpr.toString());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.interval = nbt.getInteger("interval");
        this.consume = nbt.getBoolean("consume");
        this.amount = nbt.getInteger("amount");
        this.index = nbt.hasKey("index") ? nbt.getInteger("index") : -1;
        if (nbt.hasKey("required")) {
            this.required = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("required"));
        }
        if (nbt.hasKey("amountExpr")) {
            try {
                this.amountExpr = ExpressionParser.parseExpression(nbt.getString("amountExpr"));
            } catch (Exception e) {
                Logger.error("Failed to restore fluid amount expression: " + e.getMessage());
            }
        }
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public RecipeTickResult getFailureResult(boolean perTick) {
        return RecipeTickResult.NO_INPUT;
    }
}
