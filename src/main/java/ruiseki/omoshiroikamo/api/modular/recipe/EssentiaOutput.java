package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.machinery.common.tile.essentia.AbstractEssentiaPortTE;
import thaumcraft.api.aspects.Aspect;

public class EssentiaOutput implements IRecipeOutput {

    private final String aspectTag;
    private final int amount;

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
    public boolean process(List<IModularPort> ports, boolean simulate) {
        Aspect aspect = Aspect.getAspect(aspectTag);
        if (aspect == null) return false;

        int remaining = amount;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.ESSENTIA) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT) continue;
            if (!(port instanceof AbstractEssentiaPortTE)) continue;

            AbstractEssentiaPortTE essentiaPort = (AbstractEssentiaPortTE) port;
            int space = essentiaPort.getMaxCapacityPerAspect() - essentiaPort.containerContains(aspect);
            if (space > 0) {
                int toAdd = Math.min(remaining, space);
                if (!simulate) {
                    essentiaPort.addToContainer(aspect, toAdd);
                }
                remaining -= toAdd;
            }
            if (remaining <= 0) break;
        }

        return remaining <= 0;
    }

    @Override
    public boolean checkCapacity(List<IModularPort> ports) {
        long totalCapacity = 0;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.ESSENTIA) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT) continue;
            if (!(port instanceof AbstractEssentiaPortTE)) continue;

            AbstractEssentiaPortTE essentiaPort = (AbstractEssentiaPortTE) port;
            totalCapacity += essentiaPort.getMaxCapacityPerAspect();
        }

        return totalCapacity >= amount;
    }

    public static EssentiaOutput fromJson(JsonObject json) {
        String aspectTag = json.get("essentia")
            .getAsString();
        int amount = json.has("amount") ? json.get("amount")
            .getAsInt() : 1;
        Aspect aspect = Aspect.getAspect(aspectTag);
        if (aspect == null) {
            Logger.warn("Unknown aspect in recipe: {}", aspectTag);
            return null;
        }
        return new EssentiaOutput(aspectTag, amount);
    }
}
