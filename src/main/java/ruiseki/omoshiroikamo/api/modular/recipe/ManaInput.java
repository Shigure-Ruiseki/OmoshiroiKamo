package ruiseki.omoshiroikamo.api.modular.recipe;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.mana.AbstractManaPortTE;

public class ManaInput extends AbstractRecipeInput {

    private int amount;
    private boolean perTick;

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

    @Override
    public void read(JsonObject json) {
        this.amount = json.get("mana")
            .getAsInt();
        this.perTick = true;
        if (json.has("perTick")) {
            this.perTick = json.get("perTick")
                .getAsBoolean();
        } else if (json.has("pertick")) {
            this.perTick = json.get("pertick")
                .getAsBoolean();
        }
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("mana", amount);
        if (!perTick) json.addProperty("perTick", false);
    }

    @Override
    public boolean validate() {
        return amount > 0;
    }

    public static ManaInput fromJson(JsonObject json) {
        ManaInput input = new ManaInput(0, true);
        input.read(json);
        return input.validate() ? input : null;
    }
}
