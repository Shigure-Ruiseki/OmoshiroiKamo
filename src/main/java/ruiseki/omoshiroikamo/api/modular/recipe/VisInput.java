package ruiseki.omoshiroikamo.api.modular.recipe;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.vis.AbstractVisPortTE;
import thaumcraft.api.aspects.Aspect;

public class VisInput extends AbstractRecipeInput {

    private final String aspectTag;
    private final int amountCentiVis;

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
    protected long getRequiredAmount() {
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

    public static VisInput fromJson(JsonObject json) {
        String aspectTag = json.get("vis")
            .getAsString();
        int centiVis = json.get("amount")
            .getAsInt();
        return new VisInput(aspectTag, centiVis);
    }
}
