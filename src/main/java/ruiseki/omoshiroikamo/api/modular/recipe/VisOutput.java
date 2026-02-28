package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.vis.AbstractVisPortTE;
import thaumcraft.api.aspects.Aspect;

public class VisOutput extends AbstractRecipeOutput {

    private String aspectTag;
    private int amountCentiVis;

    public VisOutput(String aspectTag, int amountCentiVis) {
        this.aspectTag = aspectTag;
        this.amountCentiVis = amountCentiVis;
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
    public void apply(List<IModularPort> ports) {
        Aspect aspect = Aspect.getAspect(aspectTag);
        if (aspect == null) return;

        int remaining = amountCentiVis;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.VIS) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT
                && port.getPortDirection() != IPortType.Direction.BOTH) continue;

            if (!(port instanceof AbstractVisPortTE)) continue;

            AbstractVisPortTE visPort = (AbstractVisPortTE) port;
            int currentVis = visPort.getVisAmount(aspect);
            int maxVis = visPort.getMaxVisPerAspect();
            int space = maxVis - currentVis;

            if (space > 0) {
                int toAdd = Math.min(remaining, space);
                visPort.addVis(aspect, toAdd);
                remaining -= toAdd;
            }
            if (remaining <= 0) break;
        }
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.VIS && port instanceof AbstractVisPortTE;
    }

    @Override
    protected long getPortCapacity(IModularPort port) {
        AbstractVisPortTE visPort = (AbstractVisPortTE) port;
        return visPort.getMaxVisPerAspect();
    }

    @Override
    protected long getRequiredAmount() {
        return amountCentiVis;
    }

    @Override
    public void read(JsonObject json) {
        this.aspectTag = json.get("vis")
            .getAsString();
        this.amountCentiVis = json.has("amount") ? json.get("amount")
            .getAsInt() : 100;
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("vis", aspectTag);
        json.addProperty("amount", amountCentiVis);
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
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }
}
