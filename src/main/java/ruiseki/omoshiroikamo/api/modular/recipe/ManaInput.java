package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.mana.AbstractManaPortTE;

/**
 * Recipe input requirement for Mana.
 */
public class ManaInput implements IRecipeInput {

    private final int amount;

    public ManaInput(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.MANA;
    }

    @Override
    public boolean process(List<IModularPort> ports, boolean simulate) {
        int remaining = amount;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.MANA) continue;
            if (port.getPortDirection() != IPortType.Direction.INPUT) continue;
            if (!(port instanceof AbstractManaPortTE)) continue;

            AbstractManaPortTE manaPort = (AbstractManaPortTE) port;
            int stored = manaPort.getCurrentMana();
            if (stored > 0) {
                int extract = Math.min(stored, remaining);
                if (!simulate) {
                    manaPort.extractMana(extract);
                }
                remaining -= extract;
            }
            if (remaining <= 0) break;
        }

        return remaining <= 0;
    }

    public static ManaInput fromJson(JsonObject json) {
        int amount = json.get("mana")
            .getAsInt();
        return new ManaInput(amount);
    }
}
