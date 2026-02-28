package ruiseki.omoshiroikamo.api.modular.recipe;

import java.util.List;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.mana.AbstractManaPortTE;

public class ManaOutput extends AbstractRecipeOutput {

    private int amount;
    private boolean perTick;

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
    public void apply(List<IModularPort> ports) {
        int remaining = amount;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.MANA) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT) continue;
            if (!(port instanceof AbstractManaPortTE)) continue;

            AbstractManaPortTE manaPort = (AbstractManaPortTE) port;
            int space = manaPort.getAvailableSpaceForMana();

            if (space > 0) {
                int toAdd = Math.min(remaining, space);
                manaPort.recieveMana(toAdd);
                remaining -= toAdd;
            }
            if (remaining <= 0) break;
        }
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.MANA && port instanceof AbstractManaPortTE;
    }

    @Override
    protected long getPortCapacity(IModularPort port) {
        AbstractManaPortTE manaPort = (AbstractManaPortTE) port;
        return (long) manaPort.getCurrentMana() + manaPort.getAvailableSpaceForMana();
    }

    @Override
    protected long getRequiredAmount() {
        return amount;
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

    public static ManaOutput fromJson(JsonObject json) {
        ManaOutput output = new ManaOutput(0, true);
        output.read(json);
        return output.validate() ? output : null;
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }
}
