package ruiseki.omoshiroikamo.api.recipe.io;

import net.minecraft.nbt.NBTTagCompound;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.expression.ConstantExpression;
import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionParser;
import ruiseki.omoshiroikamo.api.recipe.expression.ExpressionsParser;
import ruiseki.omoshiroikamo.api.recipe.expression.IExpression;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;

public class EssentiaInput extends AbstractModularRecipeInput {

    private String aspectTag;
    private int amount;
    private IExpression amountExpr;

    public EssentiaInput(String aspectTag, int amount) {
        this.aspectTag = aspectTag;
        this.amount = amount;
        this.amountExpr = new ConstantExpression(amount);
    }

    public String getAspectTag() {
        return aspectTag;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.ESSENTIA;
    }

    @Override
    public long getRequiredAmount(ConditionContext context) {
        return amountExpr != null ? (long) amountExpr.evaluate(context) : amount;
    }

    @Override
    public long getRequiredAmount() {
        return amount;
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.ESSENTIA && port instanceof IAspectContainer;
    }

    @Override
    protected long consume(IModularPort port, long remaining, boolean simulate, ConditionContext context) {
        Aspect aspect = Aspect.getAspect(aspectTag);
        if (aspect == null) return 0;

        IAspectContainer essentiaPort = (IAspectContainer) port;
        int stored = essentiaPort.containerContains(aspect);
        if (stored > 0) {
            int extract = (int) Math.min((long) stored, remaining);
            if (!simulate) {
                essentiaPort.takeFromContainer(aspect, extract);
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

        this.aspectTag = json.get("essentia")
            .getAsString();
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

        if (aspectTag != null) {
            json.addProperty("essentia", aspectTag);
        }
        if (amountExpr instanceof ConstantExpression) {
            json.addProperty("amount", amount);
        } else {
            json.addProperty("amount", amountExpr.toString());
        }
    }

    @Override
    public boolean validate() {
        return aspectTag != null && !aspectTag.isEmpty() && amount > 0;
    }

    public static EssentiaInput fromJson(JsonObject json) {
        EssentiaInput input = new EssentiaInput("", 0);
        input.read(json);
        return input.validate() ? input : null;
    }

    @Override
    public IRecipeInput copy() {
        return copy(1);
    }

    @Override
    public IRecipeInput copy(int multiplier) {
        EssentiaInput result = new EssentiaInput(aspectTag, amount * multiplier);
        result.consume = this.consume;
        result.interval = this.interval;
        result.index = this.index;
        result.amountExpr = this.amountExpr;
        return result;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "essentia");
        nbt.setInteger("interval", interval);
        nbt.setBoolean("consume", consume);
        nbt.setString("aspect", aspectTag);
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
        this.aspectTag = nbt.getString("aspect");
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
