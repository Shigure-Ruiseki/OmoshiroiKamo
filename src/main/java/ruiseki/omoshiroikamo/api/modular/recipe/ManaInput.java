package ruiseki.omoshiroikamo.api.modular.recipe;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.mana.AbstractManaPortTE;

public class ManaInput extends AbstractRecipeInput {

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
    protected long getRequiredAmount() {
        return amount;
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port instanceof AbstractManaPortTE;
    }

    @Override
    protected long consume(IModularPort port, long remaining, boolean simulate) {
        AbstractManaPortTE manaPort = (AbstractManaPortTE) port;
        int stored = manaPort.getCurrentMana();
        if (stored > 0) {
            int extract = (int) Math.min(stored, remaining);
            if (!simulate) {
                manaPort.extractMana(extract);
            }
            return extract;
        }
        return 0;
    }

    public static ManaInput fromJson(JsonObject json) {
        int amount = json.get("mana")
            .getAsInt();
        return new ManaInput(amount);
    }
}
