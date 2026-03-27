package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
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
import ruiseki.omoshiroikamo.api.recipe.expression.IExpression;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.common.util.Logger;

public class FluidOutput extends AbstractModularRecipeOutput {

    private String fluidName;
    private int amount;
    private IExpression amountExpr;

    public FluidOutput(String fluidName, int amount) {
        this.fluidName = fluidName;
        this.amount = amount;
    }

    public String getFluidName() {
        return fluidName;
    }

    public int getAmount() {
        return amount;
    }

    public FluidStack getOutput() {
        return FluidRegistry.getFluidStack(fluidName, amount);
    }

    public FluidStack getFluid() {
        return getOutput();
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.FLUID;
    }

    @Override
    public void apply(List<IModularPort> ports, int multiplier, ConditionContext context) {
        FluidStack output = FluidRegistry.getFluidStack(fluidName, (int) getRequiredAmount(context) * multiplier);
        if (output == null) return;

        int remaining = output.amount;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.FLUID) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT
                && port.getPortDirection() != IPortType.Direction.BOTH) continue;

            if (getIndex() != -1 && port.getAssignedIndex() != getIndex()) continue;

            if (!(port instanceof IFluidHandler)) continue;

            IFluidHandler fluidPort = (IFluidHandler) port;
            FluidTankInfo[] tankInfo = fluidPort.getTankInfo(ForgeDirection.UNKNOWN);
            if (tankInfo == null || tankInfo.length == 0) continue;

            int tankCapacity = tankInfo[0].capacity;
            FluidStack stored = tankInfo[0].fluid;
            int currentAmount = stored != null ? stored.amount : 0;
            int space = tankCapacity - currentAmount;

            if (stored == null || stored.isFluidEqual(output)) {
                int fillAmount = Math.min(remaining, space);
                if (fillAmount > 0) {
                    FluidStack toFill = output.copy();
                    toFill.amount = fillAmount;
                    fluidPort.fill(ForgeDirection.UNKNOWN, toFill, true);
                    remaining -= fillAmount;
                }
            }
            if (remaining <= 0) break;
        }
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.FLUID && port instanceof IFluidHandler;
    }

    @Override
    protected long getPortCapacity(IModularPort port) {
        if (!(port instanceof IFluidHandler)) return 0;
        IFluidHandler fluidPort = (IFluidHandler) port;
        FluidTankInfo[] tankInfo = fluidPort.getTankInfo(ForgeDirection.UNKNOWN);
        FluidStack output = getOutput();

        if (tankInfo != null && tankInfo.length > 0) {
            long totalAvailable = 0;
            for (FluidTankInfo info : tankInfo) {
                FluidStack stored = info.fluid;
                if (stored == null || output == null || stored.isFluidEqual(output)) {
                    int currentAmount = stored != null ? stored.amount : 0;
                    int space = info.capacity - currentAmount;
                    if (space > 0) totalAvailable += space;
                }
            }
            return totalAvailable;
        }
        return 0;
    }

    @Override
    public long getRequiredAmount(ConditionContext context) {
        if (amountExpr != null && context != null) {
            return (long) amountExpr.evaluate(context);
        }
        return amount;
    }

    @Override
    public long getRequiredAmount() {
        return getRequiredAmount(null);
    }

    @Override
    public void read(JsonObject json) {
        readPerTick(json, 0);
        if (json.has("index")) this.index = json.get("index")
            .getAsInt();
        this.fluidName = json.get("fluid")
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
    }

    @Override
    public void write(JsonObject json) {
        if (index != -1) json.addProperty("index", index);
        json.addProperty("fluid", fluidName);
        if (amountExpr != null) {
            json.addProperty("amount", amountExpr.toString());
        } else {
            json.addProperty("amount", amount);
        }
        if (interval > 0) json.addProperty("pertick", interval);
    }

    @Override
    public boolean validate() {
        return fluidName != null && !fluidName.isEmpty() && amount > 0;
    }

    public static FluidOutput fromJson(JsonObject json) {
        FluidOutput output = new FluidOutput("", 0);
        output.read(json);
        return output;
    }

    @Override
    public IRecipeOutput copy() {
        return copy(1);
    }

    @Override
    public IRecipeOutput copy(int multiplier) {
        FluidOutput result = new FluidOutput(fluidName, amount * multiplier);
        result.amountExpr = this.amountExpr;
        result.interval = this.interval;
        result.index = this.index;
        return result;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "fluid");
        nbt.setInteger("interval", interval);
        nbt.setString("fluid", fluidName);
        nbt.setInteger("amount", amount);
        nbt.setInteger("index", index);
        if (amountExpr != null) nbt.setString("amountExpr", amountExpr.toString());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.interval = nbt.getInteger("interval");
        this.fluidName = nbt.getString("fluid");
        this.amount = nbt.getInteger("amount");
        this.index = nbt.hasKey("index") ? nbt.getInteger("index") : -1;
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
        return RecipeTickResult.OUTPUT_FULL;
    }
}
