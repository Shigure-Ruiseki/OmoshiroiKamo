package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.machinery.common.tile.vis.AbstractVisPortTE;
import thaumcraft.api.aspects.Aspect;

public class VisOutput extends AbstractRecipeOutput {

    private final String aspectTag;
    private final int amountCentiVis;

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
    public boolean process(List<IModularPort> ports, boolean simulate) {

        // If not simulating, first check if we CAN output everything by simulating
        if (!simulate) {
            if (!process(ports, true)) return false;
        }

        Aspect aspect = Aspect.getAspect(aspectTag);
        if (aspect == null) return false;

        int remaining = amountCentiVis;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.VIS) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT
                && port.getPortDirection() != IPortType.Direction.BOTH) continue;
            if (!(port instanceof AbstractVisPortTE)) {
                throw new IllegalStateException(
                    "VIS OUTPUT port must be AbstractVisPortTE, got: " + port.getClass()
                        .getName());
            }

            AbstractVisPortTE visPort = (AbstractVisPortTE) port;
            // Check available space
            int currentVis = visPort.getVisAmount(aspect);
            int maxVis = visPort.getMaxVisPerAspect();
            int space = maxVis - currentVis;

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
