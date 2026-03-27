package ruiseki.omoshiroikamo.api.recipe.io;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.gson.JsonObject;

import mekanism.api.gas.GasStack;
import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.expression.ConstantExpression;
import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionParser;
import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionsParser;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.gas.IGasHandler;

public class GasInput extends AbstractModularRecipeInput {

    private String gasName;

    public GasInput(String gasName, int amount) {
        this.gasName = gasName;
        this.amount = amount;
        this.amountExpr = new ConstantExpression(amount);
    }

    public String getGasName() {
        return gasName;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.GAS;
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
        return port.getPortType() == IPortType.Type.GAS && port instanceof IGasHandler;
    }

    @Override
    protected long consume(IModularPort port, long remaining, boolean simulate, ConditionContext context) {
        IGasHandler gasPort = (IGasHandler) port;
        GasStack drawn = gasPort.drawGas(ForgeDirection.UNKNOWN, (int) remaining, false);
        if (drawn != null && drawn.amount > 0) {
            if (gasName == null || gasName.isEmpty()
                || drawn.getGas()
                    .getName()
                    .equals(gasName)) {
                if (!simulate) {
                    gasPort.drawGas(ForgeDirection.UNKNOWN, (int) drawn.amount, true);
                }
                return drawn.amount;
            }
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

        this.gasName = json.has("gas") ? json.get("gas")
            .getAsString() : null;
        if (json.has("amount")) {
            this.amountExpr = ExpressionsParser.parse(json.get("amount"));
            if (amountExpr instanceof ConstantExpression) {
                this.amount = (int) amountExpr.evaluate(null);
            }
        }
    }

    @Override
    public void write(JsonObject json) {
        if (index != -1) json.addProperty("index", index);
        if (!consume) json.addProperty("consume", false);
        if (interval > 0) json.addProperty("pertick", interval);

        if (gasName != null) {
            json.addProperty("gas", gasName);
        }
        if (amountExpr instanceof ConstantExpression) {
            json.addProperty("amount", amount);
        } else {
            json.addProperty("amount", amountExpr.toString());
        }
    }

    @Override
    public boolean validate() {
        return amount > 0;
    }

    public static GasInput fromJson(JsonObject json) {
        GasInput input = new GasInput(null, 0);
        input.read(json);
        return input.validate() ? input : null;
    }

    @Override
    public IRecipeInput copy() {
        return copy(1);
    }

    @Override
    public IRecipeInput copy(int multiplier) {
        GasInput result = new GasInput(gasName, (int) (amount * multiplier));
        result.consume = this.consume;
        result.interval = this.interval;
        result.index = this.index;
        result.amountExpr = this.amountExpr; // Reuse same expression
        return result;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "gas");
        nbt.setInteger("interval", interval);
        nbt.setBoolean("consume", consume);
        nbt.setString("gas", gasName);
        nbt.setInteger("amount", amount);
        if (!(amountExpr instanceof ConstantExpression)) {
            nbt.setString("amountExpr", amountExpr.toString());
        }
        nbt.setInteger("index", index);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.interval = nbt.getInteger("interval");
        this.consume = nbt.getBoolean("consume");
        this.gasName = nbt.getString("gas");
        this.amount = nbt.getInteger("amount");
        if (nbt.hasKey("amountExpr")) {
            this.amountExpr = ExpressionParser.parseExpression(nbt.getString("amountExpr"));
        } else {
            this.amountExpr = new ConstantExpression(amount);
        }
        this.index = nbt.hasKey("index") ? nbt.getInteger("index") : -1;
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }
}
