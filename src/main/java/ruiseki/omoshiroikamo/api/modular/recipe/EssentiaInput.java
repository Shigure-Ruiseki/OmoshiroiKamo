package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.essentia.AbstractEssentiaPortTE;
import thaumcraft.api.aspects.Aspect;

/**
 * Recipe input requirement for Essentia.
 */
public class EssentiaInput implements IRecipeInput {

    private final String aspectTag;
    private final int amount;

    public EssentiaInput(String aspectTag, int amount) {
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
            if (port.getPortDirection() != IPortType.Direction.INPUT) continue;
            if (!(port instanceof AbstractEssentiaPortTE)) continue;

            AbstractEssentiaPortTE essentiaPort = (AbstractEssentiaPortTE) port;
            int stored = essentiaPort.containerContains(aspect);
            if (stored > 0) {
                int extract = Math.min(stored, remaining);
                if (!simulate) {
                    essentiaPort.takeFromContainer(aspect, extract);
                }
                remaining -= extract;
            }
            if (remaining <= 0) break;
        }

        return remaining <= 0;
    }

    public static EssentiaInput fromJson(JsonObject json) {
        String aspectTag = json.get("essentia")
            .getAsString();
        int amount = json.get("amount")
            .getAsInt();
        return new EssentiaInput(aspectTag, amount);
    }
}
