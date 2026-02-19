package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.mana.AbstractManaPortTE;

public class ManaOutput extends AbstractRecipeOutput {

    private final int amount;
    private final boolean perTick;

    public ManaOutput(int amount, boolean perTick) {
        this.amount = amount;
        this.perTick = perTick;
    }

    public ManaOutput(int amount) {
        this(amount, true);
    }

    public int getAmount() {
        return amount;
    }

    public boolean isPerTick() {
        return perTick;
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.MANA;
    }

    @Override
    public boolean process(List<IModularPort> ports, boolean simulate) {

        // If not simulating, first check if we CAN output everything by simulating
        if (!simulate) {
            if (!process(ports, true)) return false;
        }

        int remaining = amount;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.MANA) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT) continue;
            if (!(port instanceof AbstractManaPortTE)) {
                throw new IllegalStateException(
                    "MANA OUTPUT port must be AbstractManaPortTE, got: " + port.getClass()
                        .getName());
            }

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

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.MANA && port instanceof AbstractManaPortTE;
    }

    @Override
    protected long getPortCapacity(IModularPort port) {
        AbstractManaPortTE manaPort = (AbstractManaPortTE) port;
        // Total capacity = current mana + available space
        return (long) manaPort.getCurrentMana() + manaPort.getAvailableSpaceForMana();
    }

    @Override
    protected long getRequiredAmount() {
        return amount;
    }

    public static ManaOutput fromJson(JsonObject json) {
        int amount = json.get("mana")
            .getAsInt();
        boolean perTick = true;
        if (json.has("perTick")) {
            perTick = json.get("perTick")
                .getAsBoolean();
        } else if (json.has("pertick")) {
            perTick = json.get("pertick")
                .getAsBoolean();
        }
        return new ManaOutput(amount, perTick);
    }
}
