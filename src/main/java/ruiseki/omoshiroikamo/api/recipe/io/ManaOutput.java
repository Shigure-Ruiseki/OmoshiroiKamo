package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import vazkii.botania.api.mana.IManaPool;
import vazkii.botania.api.mana.spark.ISparkAttachable;

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
    public void apply(List<IModularPort> ports, int multiplier) {
        long remaining = amount * multiplier;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.MANA) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT) continue;
            if (!(port instanceof IManaPool)) continue;

            IManaPool manaPort = (IManaPool) port;
            int space = 0;
            if (port instanceof ISparkAttachable) {
                space = ((ISparkAttachable) port).getAvailableSpaceForMana();
            }

            if (space > 0) {
                int toAdd = (int) Math.min(remaining, (long) space);
                manaPort.recieveMana(toAdd);
                remaining -= toAdd;
            }
            if (remaining <= 0) break;
        }
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.MANA && port instanceof IManaPool;
    }

    @Override
    protected long getPortCapacity(IModularPort port) {
        if (port instanceof IManaPool && port instanceof ISparkAttachable) {
            IManaPool manaPort = (IManaPool) port;
            ISparkAttachable sparkPort = (ISparkAttachable) port;
            return (long) manaPort.getCurrentMana() + (long) sparkPort.getAvailableSpaceForMana();
        }
        return 0;
    }

    @Override
    public long getRequiredAmount() {
        return (long) amount;
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
    public IRecipeOutput copy() {
        return copy(1);
    }

    @Override
    public IRecipeOutput copy(int multiplier) {
        return new ManaOutput(amount * multiplier, perTick);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "mana");
        nbt.setInteger("amount", amount);
        nbt.setBoolean("perTick", perTick);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.amount = nbt.getInteger("amount");
        this.perTick = nbt.getBoolean("perTick");
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }
}
