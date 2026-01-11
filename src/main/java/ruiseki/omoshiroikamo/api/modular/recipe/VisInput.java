package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.vis.AbstractVisPortTE;
import thaumcraft.api.aspects.Aspect;

public class VisInput implements IRecipeInput {

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
    public boolean process(List<IModularPort> ports, boolean simulate) {
        Aspect aspect = Aspect.getAspect(aspectTag);
        if (aspect == null) return false;

        int remaining = amountCentiVis;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.VIS) continue;
            if (port.getPortDirection() != IPortType.Direction.INPUT) continue;
            if (!(port instanceof AbstractVisPortTE)) continue;

            AbstractVisPortTE visPort = (AbstractVisPortTE) port;
            int stored = visPort.getVisAmount(aspect);
            if (stored > 0) {
                int extract = Math.min(stored, remaining);
                if (!simulate) {
                    visPort.drainVis(aspect, extract);
                }
                remaining -= extract;
            }
            if (remaining <= 0) break;
        }

        return remaining <= 0;
    }

    public static VisInput fromJson(JsonObject json) {
        String aspectTag = json.get("vis")
            .getAsString();
        int centiVis = json.get("amount")
            .getAsInt();
        return new VisInput(aspectTag, centiVis);
    }
}
