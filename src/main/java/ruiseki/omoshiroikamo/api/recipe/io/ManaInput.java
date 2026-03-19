package ruiseki.omoshiroikamo.api.recipe.io;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import vazkii.botania.api.mana.IManaPool;

public class ManaInput extends AbstractRecipeInput {

    private int amount;
    private boolean perTick;

    public ManaInput(int amount, boolean perTick) {
        this.amount = amount;
        this.perTick = perTick;
    }

    public ManaInput(int amount) {
        this(amount, false);
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
    public long getRequiredAmount() {
        return (long) amount;
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.MANA && port instanceof IManaPool;
    }

    @Override
    protected long consume(IModularPort port, long remaining, boolean simulate) {
        IManaPool manaPort = (IManaPool) port;
        int stored = manaPort.getCurrentMana();
        if (stored > 0) {
            int extract = (int) Math.min((long) stored, remaining);
            if (!simulate) {
                manaPort.recieveMana(-extract);
            }
            return (long) extract;
        }
        return 0;
    }

    @Override
    public void read(JsonObject json) {
        if (json.has("consume")) {
            this.consume = json.get("consume")
                .getAsBoolean();
        }

        this.amount = json.get("mana")
            .getAsInt();
        this.perTick = false;
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
        if (!consume) json.addProperty("consume", false);
        json.addProperty("mana", amount);
        json.addProperty("perTick", perTick);
    }

    @Override
    public boolean validate() {
        return amount > 0;
    }

    public static ManaInput fromJson(JsonObject json) {
        ManaInput input = new ManaInput(0, false);
        input.read(json);
        return input.validate() ? input : null;
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }
}
