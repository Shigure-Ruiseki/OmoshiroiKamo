package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.essentia.AbstractEssentiaPortTE;
import thaumcraft.api.aspects.Aspect;

public class EssentiaOutput extends AbstractRecipeOutput {

    private String aspectTag;
    private int amount;

    public EssentiaOutput(String aspectTag, int amount) {
        this.aspectTag = aspectTag;
        this.amount = amount;
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
    public void apply(List<IModularPort> ports) {
        Aspect aspect = Aspect.getAspect(aspectTag);
        if (aspect == null) return;

        int remaining = amount;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.ESSENTIA) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT) continue;
            if (!(port instanceof AbstractEssentiaPortTE)) continue;

            AbstractEssentiaPortTE essentiaPort = (AbstractEssentiaPortTE) port;
            int space = essentiaPort.getMaxCapacityPerAspect() - essentiaPort.containerContains(aspect);

            if (space > 0) {
                int toAdd = Math.min(remaining, space);
                essentiaPort.addToContainer(aspect, toAdd);
                remaining -= toAdd;
            }
            if (remaining <= 0) break;
        }
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.ESSENTIA && port instanceof AbstractEssentiaPortTE;
    }

    @Override
    protected long getPortCapacity(IModularPort port) {
        AbstractEssentiaPortTE essentiaPort = (AbstractEssentiaPortTE) port;
        return essentiaPort.getMaxCapacityPerAspect();
    }

    @Override
    protected long getRequiredAmount() {
        return amount;
    }

    @Override
    public void read(JsonObject json) {
        this.aspectTag = json.get("essentia")
            .getAsString();
        this.amount = json.has("amount") ? json.get("amount")
            .getAsInt() : 1;
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("essentia", aspectTag);
        json.addProperty("amount", amount);
    }

    @Override
    public boolean validate() {
        return aspectTag != null && !aspectTag.isEmpty() && amount > 0;
    }

    public static EssentiaOutput fromJson(JsonObject json) {
        EssentiaOutput output = new EssentiaOutput("", 0);
        output.read(json);
        return output.validate() ? output : null;
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }
}
