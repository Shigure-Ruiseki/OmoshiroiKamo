package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.core.RecipeTickResult;
import ruiseki.omoshiroikamo.api.recipe.expression.ConstantExpression;
import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionParser;
import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionsParser;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.spark.ISparkAttachable;

public class ManaOutput extends AbstractModularRecipeOutput {

    public ManaOutput(int amount, boolean perTick) {
        this.amount = amount;
        this.amountExpr = new ConstantExpression(amount);
        this.interval = perTick ? 1 : 0;
    }

    public ManaOutput(int amount) {
        this(amount, false);
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
        return IPortType.Type.MANA;
    }

    @Override
    public void apply(List<IModularPort> ports, int multiplier, ConditionContext context) {
        long req = amountExpr != null ? (long) amountExpr.evaluateDouble(context) : amount;
        long remaining = req * multiplier;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.MANA) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT
                && port.getPortDirection() != IPortType.Direction.BOTH) continue;

            if (getIndex() != -1 && port.getAssignedIndex() != getIndex()) continue;
            if (!(port instanceof IManaPool)) continue;

            IManaPool manaPort = (IManaPool) port;
            int space = 0;
            if (port instanceof ISparkAttachable) {
                space = ((ISparkAttachable) port).getAvailableSpaceForMana();
            }

            if (space > 0) {
                int toAdd = (int) Math.min(remaining, (long) space);
                manaPort.recieveMana(toAdd);
                remaining -= toAdd;
            }
            if (remaining <= 0) break;
        }
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.MANA && port instanceof IManaPool;
    }

    @Override
    protected long getPortCapacity(IModularPort port) {
        if (port instanceof IManaPool && port instanceof ISparkAttachable) {
            ISparkAttachable sparkPort = (ISparkAttachable) port;
            // For output capacity check, return only available space, not total capacity
            return (long) sparkPort.getAvailableSpaceForMana();
        }
        return 0;
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
    public void read(JsonObject json) {
        readPerTick(json, 0);
        if (json.has("index")) this.index = json.get("index")
            .getAsInt();
        this.amount = 0;
        if (json.has("mana")) {
            this.amountExpr = ExpressionsParser.parse(json.get("mana"));
            if (amountExpr instanceof ConstantExpression) {
                this.amount = (int) amountExpr.evaluateDouble(null);
            }
        }
    }

    @Override
    public void write(JsonObject json) {
        if (index != -1) json.addProperty("index", index);
        if (amountExpr instanceof ConstantExpression) {
            json.addProperty("mana", amount);
        } else {
            json.addProperty("mana", amountExpr.toString());
        }
        if (interval != 0) {
            json.addProperty("pertick", interval);
        }
    }

    @Override
    public boolean validate() {
        return amount > 0;
    }

    public static ManaOutput fromJson(JsonObject json) {
        ManaOutput output = new ManaOutput(0, true);
        output.read(json);
        return output.validate() ? output : null;
    }

    @Override
    public IRecipeOutput copy() {
        return copy(1);
    }

    @Override
    public IRecipeOutput copy(int multiplier) {
        ManaOutput result = new ManaOutput(amount * multiplier, isPerTick());
        result.interval = this.interval;
        result.index = this.index;
        result.amountExpr = this.amountExpr;
        return result;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "mana");
        nbt.setInteger("amount", amount);
        if (!(amountExpr instanceof ConstantExpression)) {
            nbt.setString("amountExpr", amountExpr.toString());
        }
        nbt.setInteger("interval", interval);
        nbt.setInteger("index", index);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.amount = nbt.getInteger("amount");
        if (nbt.hasKey("amountExpr")) {
            this.amountExpr = ExpressionParser.parseExpression(nbt.getString("amountExpr"));
        } else {
            this.amountExpr = new ConstantExpression(amount);
        }
        this.interval = nbt.getInteger("interval");
        this.index = nbt.hasKey("index") ? nbt.getInteger("index") : -1;
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
