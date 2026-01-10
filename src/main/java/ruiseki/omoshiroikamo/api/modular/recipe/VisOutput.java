package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.machinery.common.tile.vis.AbstractVisPortTE;
import thaumcraft.api.aspects.Aspect;

/**
 * Recipe output for Vis.
 * JSON specifies Vis units, internally stored as centiVis (100 centivis = 1 vis).
 */
public class VisOutput implements IRecipeOutput {

    private final String aspectTag;
    // Amount in centiVis
    private final int amountCentiVis;

    public VisOutput(String aspectTag, int amountCentiVis) {
        this.aspectTag = aspectTag;
        this.amountCentiVis = amountCentiVis;
    }

    public String getAspectTag() {
        return aspectTag;
    }

    // Amount in centiVis
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
            if (port.getPortDirection() != IPortType.Direction.OUTPUT) continue;
            if (!(port instanceof AbstractVisPortTE)) continue;

            AbstractVisPortTE visPort = (AbstractVisPortTE) port;
            // Check available space
            int currentAmount = visPort.getVisAmount(aspect);
            int maxCapacity = visPort.getMaxVisPerAspect();
            int space = maxCapacity - currentAmount;

            if (space > 0) {
                int toAdd = Math.min(remaining, space);
                if (!simulate) {
                    visPort.addVis(aspect, toAdd);
                }
                remaining -= toAdd;
            }
            if (remaining <= 0) break;
        }

        return remaining <= 0;
    }

    /**
     * Create VisOutput from JSON.
     * JSON "amount" is in centiVis.
     */
    public static VisOutput fromJson(JsonObject json) {
        String aspectTag = json.get("vis")
            .getAsString();
        int centiVis = json.has("amount") ? json.get("amount")
            .getAsInt() : 100;
        Aspect aspect = Aspect.getAspect(aspectTag);
        if (aspect == null) {
            Logger.warn("Unknown aspect in recipe: {}", aspectTag);
            return null;
        }
        return new VisOutput(aspectTag, centiVis);
    }
}
