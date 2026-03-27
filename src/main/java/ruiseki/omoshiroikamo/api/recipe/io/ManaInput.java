package ruiseki.omoshiroikamo.api.recipe.io;

import net.minecraft.nbt.NBTTagCompound;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.core.RecipeTickResult;
import ruiseki.omoshiroikamo.api.recipe.expression.ConstantExpression;
import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionParser;
import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionsParser;
import ruiseki.omoshiroikamo.api.recipe.expression.IExpression;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import vazkii.botania.api.mana.IManaPool;

public class ManaInput extends AbstractModularRecipeInput {

    private int amount;
    private IExpression amountExpr;

    public ManaInput(int amount, boolean perTick) {
        this.amount = amount;
        this.amountExpr = new ConstantExpression(amount);
        this.interval = perTick ? 1 : 0;
    }

    public ManaInput(int amount) {
        this(amount, false);
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean isPerTick() {
        return interval > 0; // Check interval to determine if it's per-tick
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.MANA;
    }

    @Override
    public long getRequiredAmount(ConditionContext context) {
        return amountExpr != null ? (long) amountExpr.evaluate(context) : amount;
    }

    @Override
    public long getRequiredAmount() {
        return (long) amount;
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.MANA && port instanceof IManaPool;
    }

    @Override
    protected long consume(IModularPort port, long remaining, boolean simulate, ConditionContext context) {
        IManaPool manaPort = (IManaPool) port;
        int stored = manaPort.getCurrentMana();
        if (stored > 0) {
            int extract = (int) Math.min((long) stored, remaining);
            if (!simulate) {
                manaPort.recieveMana(-extract);
            }
            return (long) extract;
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

        this.amount = 0;
        if (json.has("mana")) {
            this.amountExpr = ExpressionsParser.parse(json.get("mana"));
            if (amountExpr instanceof ConstantExpression) {
                this.amount = (int) amountExpr.evaluate(null);
            }
        }
    }

    @Override
    public void write(JsonObject json) {
        if (index != -1) json.addProperty("index", index);
        if (!consume) json.addProperty("consume", false);
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

    public static ManaInput fromJson(JsonObject json) {
        ManaInput input = new ManaInput(0, false);
        input.read(json);
        return input.validate() ? input : null;
    }

    @Override
    public IRecipeInput copy() {
        return copy(1);
    }

    @Override
    public IRecipeInput copy(int multiplier) {
        ManaInput result = new ManaInput(amount * multiplier, isPerTick());
        result.interval = this.interval;
        result.consume = this.consume;
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
        nbt.setBoolean("consume", consume);
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
        this.consume = nbt.getBoolean("consume");
        this.index = nbt.hasKey("index") ? nbt.getInteger("index") : -1;
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public RecipeTickResult getFailureResult(boolean perTick) {
        return RecipeTickResult.NO_MANA;
    }
}
