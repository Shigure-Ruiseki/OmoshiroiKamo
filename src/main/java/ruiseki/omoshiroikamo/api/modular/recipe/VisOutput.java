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
 */
public class VisOutput implements IRecipeOutput {

    private final String aspectTag;
    private final int amount;

    public VisOutput(String aspectTag, int amount) {
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
        return IPortType.Type.VIS;
    }

    @Override
    public boolean process(List<IModularPort> ports, boolean simulate) {
        Aspect aspect = Aspect.getAspect(aspectTag);
        if (aspect == null) return false;

        int remaining = amount;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.VIS) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT) continue;
            if (!(port instanceof AbstractVisPortTE)) continue;

            AbstractVisPortTE visPort = (AbstractVisPortTE) port;
            // addVis returns the amount that could NOT be added
            int notAdded = visPort.addVis(aspect, remaining);
            int added = remaining - notAdded;
            remaining = notAdded;
            if (remaining <= 0) break;
        }

        return remaining <= 0;
    }

    public static VisOutput fromJson(JsonObject json) {
        String aspectTag = json.get("vis")
            .getAsString();
        int amount = json.has("amount") ? json.get("amount")
            .getAsInt() : 1;
        Aspect aspect = Aspect.getAspect(aspectTag);
        if (aspect == null) {
            Logger.warn("Unknown aspect in recipe: {}", aspectTag);
            return null;
        }
        return new VisOutput(aspectTag, amount);
    }
}
