package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.module.machinery.common.tile.essentia.AbstractEssentiaPortTE;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;

public class EssentiaOutput extends AbstractModularRecipeOutput {

    private String aspectTag;
    private int amount;

    public EssentiaOutput(String aspectTag, int amount) {
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
    public void apply(List<IModularPort> ports, int multiplier) {
        Aspect aspect = Aspect.getAspect(aspectTag);
        if (aspect == null) return;

        int remaining = amount * multiplier;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.ESSENTIA) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT) continue;
            if (!(port instanceof IAspectContainer)) continue;

            IAspectContainer essentiaPort = (IAspectContainer) port;
            int accepted = (int) remaining - essentiaPort.addToContainer(aspect, (int) remaining);
            remaining -= accepted;
            if (remaining <= 0) break;
        }
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.ESSENTIA && port instanceof IAspectContainer;
    }

    @Override
    protected long getPortCapacity(IModularPort port) {
        if (port instanceof IAspectContainer) {
            IAspectContainer container = (IAspectContainer) port;
            Aspect aspect = Aspect.getAspect(aspectTag);
            if (aspect == null) return 0;

            // Get current amount and max capacity
            int currentAmount = container.containerContains(aspect);
            int maxCapacity = 0;

            if (port instanceof AbstractEssentiaPortTE) {
                maxCapacity = ((AbstractEssentiaPortTE) port).getMaxCapacityPerAspect();
            } else {
                maxCapacity = 64; // Fallback for external containers (Standard Jar size)
            }

            // Return available space only
            return Math.max(0, maxCapacity - currentAmount);
        }
        return 0;
    }

    @Override
    public long getRequiredAmount() {
        return amount;
    }

    @Override
    public void read(JsonObject json) {
        readPerTick(json, 0);
        this.aspectTag = json.get("essentia")
            .getAsString();
        this.amount = json.has("amount") ? json.get("amount")
            .getAsInt() : 1;
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("essentia", aspectTag);
        json.addProperty("amount", amount);
        if (interval > 0) json.addProperty("pertick", interval);
    }

    @Override
    public boolean validate() {
        return aspectTag != null && !aspectTag.isEmpty() && amount > 0;
    }

    public static EssentiaOutput fromJson(JsonObject json) {
        EssentiaOutput output = new EssentiaOutput("", 0);
        output.read(json);
        return output.validate() ? output : null;
    }

    @Override
    public IRecipeOutput copy() {
        return copy(1);
    }

    @Override
    public IRecipeOutput copy(int multiplier) {
        EssentiaOutput result = new EssentiaOutput(aspectTag, amount * multiplier);
        result.interval = this.interval;
        return result;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "essentia");
        nbt.setInteger("interval", interval);
        nbt.setString("aspect", aspectTag);
        nbt.setInteger("amount", amount);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.interval = nbt.getInteger("interval");
        this.aspectTag = nbt.getString("aspect");
        this.amount = nbt.getInteger("amount");
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }
}
