package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.List;

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
import ruiseki.omoshiroikamo.module.machinery.common.tile.vis.AbstractVisPortTE;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;

public class VisOutput extends AbstractModularRecipeOutput {

    private String aspectTag;
    private int amountCentiVis;
    private IExpression amountExpr;

    public VisOutput(String aspectTag, int amountCentiVis) {
        this.aspectTag = aspectTag;
        this.amountCentiVis = amountCentiVis;
        this.amountExpr = new ConstantExpression(amountCentiVis);
    }

    public String getAspectTag() {
        return aspectTag;
    }

    public int getAmountCentiVis() {
        return amountCentiVis;
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.VIS;
    }

    @Override
    public void apply(List<IModularPort> ports, int multiplier, ConditionContext context) {
        Aspect aspect = Aspect.getAspect(aspectTag);
        if (aspect == null) return;

        int req = amountExpr != null ? (int) amountExpr.evaluate(context) : amountCentiVis;
        int remaining = req * multiplier;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.VIS) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT
                && port.getPortDirection() != IPortType.Direction.BOTH) continue;

            if (getIndex() != -1 && port.getAssignedIndex() != getIndex()) continue;

            if (!(port instanceof IAspectContainer)) continue;

            IAspectContainer visPort = (IAspectContainer) port;
            int accepted = (int) remaining - visPort.addToContainer(aspect, (int) remaining);
            remaining -= accepted;
            if (remaining <= 0) break;
        }
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.VIS && port instanceof IAspectContainer;
    }

    @Override
    protected long getPortCapacity(IModularPort port) {
        if (port instanceof IAspectContainer) {
            IAspectContainer container = (IAspectContainer) port;
            Aspect aspect = Aspect.getAspect(aspectTag);
            if (aspect == null) return 0;

            // Get current amount and max capacity
            int currentAmount = container.containerContains(aspect);
            int maxCapacity = 0;

            if (port instanceof AbstractVisPortTE) {
                maxCapacity = ((AbstractVisPortTE) port).getMaxVisPerAspect();
            } else {
                maxCapacity = 100; // Default capacity for Vis if not specific TE
            }

            // Return available space only
            return Math.max(0, maxCapacity - currentAmount);
        }
        return 0;
    }

    @Override
    public long getRequiredAmount(ConditionContext context) {
        return amountExpr != null ? (long) amountExpr.evaluate(context) : amountCentiVis;
    }

    @Override
    public long getRequiredAmount() {
        return amountCentiVis;
    }

    @Override
    public void read(JsonObject json) {
        readPerTick(json, 0);
        if (json.has("index")) this.index = json.get("index")
            .getAsInt();
        this.aspectTag = json.get("vis")
            .getAsString();
        if (json.has("amount")) {
            this.amountExpr = ExpressionsParser.parse(json.get("amount"));
            if (amountExpr instanceof ConstantExpression) {
                this.amountCentiVis = (int) amountExpr.evaluate(null);
            }
        } else {
            this.amountCentiVis = 100;
            this.amountExpr = new ConstantExpression(100);
        }
    }

    @Override
    public void write(JsonObject json) {
        if (index != -1) json.addProperty("index", index);
        json.addProperty("vis", aspectTag);
        if (amountExpr instanceof ConstantExpression) {
            json.addProperty("amount", amountCentiVis);
        } else {
            json.addProperty("amount", amountExpr.toString());
        }
        if (interval > 0) json.addProperty("pertick", interval);
    }

    @Override
    public boolean validate() {
        return aspectTag != null && !aspectTag.isEmpty() && amountCentiVis > 0;
    }

    public static VisOutput fromJson(JsonObject json) {
        VisOutput output = new VisOutput("", 0);
        output.read(json);
        return output.validate() ? output : null;
    }

    @Override
    public IRecipeOutput copy() {
        return copy(1);
    }

    @Override
    public IRecipeOutput copy(int multiplier) {
        VisOutput result = new VisOutput(aspectTag, (int) (amountCentiVis * multiplier));
        result.interval = this.interval;
        result.index = this.index;
        result.amountExpr = this.amountExpr;
        return result;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "vis");
        nbt.setInteger("interval", interval);
        nbt.setString("aspect", aspectTag);
        nbt.setInteger("amount", amountCentiVis);
        if (!(amountExpr instanceof ConstantExpression)) {
            nbt.setString("amountExpr", amountExpr.toString());
        }
        nbt.setInteger("index", index);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.interval = nbt.getInteger("interval");
        this.aspectTag = nbt.getString("aspect");
        this.amountCentiVis = nbt.getInteger("amount");
        if (nbt.hasKey("amountExpr")) {
            this.amountExpr = ExpressionParser.parseExpression(nbt.getString("amountExpr"));
        } else {
            this.amountExpr = new ConstantExpression(amountCentiVis);
        }
        this.index = nbt.hasKey("index") ? nbt.getInteger("index") : -1;
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }
}
