package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.mana.AbstractManaPortTE;

/**
 * Recipe output for Mana.
 */
public class ManaOutput implements IRecipeOutput {

    private final int amount;

    public ManaOutput(int amount) {
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
            if (port.getPortDirection() != IPortType.Direction.OUTPUT) continue;
            if (!(port instanceof AbstractManaPortTE)) continue;

            AbstractManaPortTE manaPort = (AbstractManaPortTE) port;
            int space = manaPort.getAvailableSpaceForMana();
            if (space > 0) {
                int toAdd = Math.min(remaining, space);
                if (!simulate) {
                    manaPort.recieveMana(toAdd);
                }
                remaining -= toAdd;
            }
            if (remaining <= 0) break;
        }

        return remaining <= 0;
    }

    public static ManaOutput fromJson(JsonObject json) {
        int amount = json.get("mana")
            .getAsInt();
        return new ManaOutput(amount);
    }
}
