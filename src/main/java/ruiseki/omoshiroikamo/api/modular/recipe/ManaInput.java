package ruiseki.omoshiroikamo.api.modular.recipe;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.mana.AbstractManaPortTE;

public class ManaInput extends AbstractRecipeInput {

    private final int amount;
    private final boolean perTick;

    public ManaInput(int amount, boolean perTick) {
        this.amount = amount;
        this.perTick = perTick;
    }

    public ManaInput(int amount) {
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
        boolean perTick = true;
        if (json.has("perTick")) {
            perTick = json.get("perTick")
                .getAsBoolean();
        } else if (json.has("pertick")) {
            perTick = json.get("pertick")
                .getAsBoolean();
        }
        return new ManaInput(amount, perTick);
    }
}
