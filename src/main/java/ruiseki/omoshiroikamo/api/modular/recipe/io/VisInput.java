package ruiseki.omoshiroikamo.api.modular.recipe.io;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.modular.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.module.machinery.common.tile.vis.AbstractVisPortTE;
import thaumcraft.api.aspects.Aspect;

public class VisInput extends AbstractRecipeInput {

    private String aspectTag;
    private int amountCentiVis;

    public VisInput(String aspectTag, int amountCentiVis) {
        this.aspectTag = aspectTag;
        this.amountCentiVis = amountCentiVis;
    }

    public String getAspectTag() {
        return aspectTag;
    }

    public int getAmount() {
        return amountCentiVis;
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.VIS;
    }

    @Override
    public long getRequiredAmount() {
        return amountCentiVis;
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port instanceof AbstractVisPortTE;
    }

    @Override
    protected long consume(IModularPort port, long remaining, boolean simulate) {
        Aspect aspect = Aspect.getAspect(aspectTag);
        if (aspect == null) return 0;

        AbstractVisPortTE visPort = (AbstractVisPortTE) port;
        int stored = visPort.getVisAmount(aspect);
        if (stored > 0) {
            int extract = (int) Math.min(stored, remaining);
            if (!simulate) {
                visPort.drainVis(aspect, extract);
            }
            return extract;
        }
        return 0;
    }

    @Override
    public void read(JsonObject json) {
        this.aspectTag = json.get("vis")
            .getAsString();
        this.amountCentiVis = json.get("amount")
            .getAsInt();
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

    public static VisInput fromJson(JsonObject json) {
        VisInput input = new VisInput("", 0);
        input.read(json);
        return input.validate() ? input : null;
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }
}
