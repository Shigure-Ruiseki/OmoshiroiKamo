package ruiseki.omoshiroikamo.api.recipe.io;

import net.minecraft.nbt.NBTTagCompound;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;

public class EssentiaInput extends AbstractModularRecipeInput {

    private String aspectTag;
    private int amount;

    public EssentiaInput(String aspectTag, int amount) {
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
    public long getRequiredAmount() {
        return amount;
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.ESSENTIA && port instanceof IAspectContainer;
    }

    @Override
    protected long consume(IModularPort port, long remaining, boolean simulate) {
        Aspect aspect = Aspect.getAspect(aspectTag);
        if (aspect == null) return 0;

        IAspectContainer essentiaPort = (IAspectContainer) port;
        int stored = essentiaPort.containerContains(aspect);
        if (stored > 0) {
            int extract = (int) Math.min((long) stored, remaining);
            if (!simulate) {
                essentiaPort.takeFromContainer(aspect, extract);
            }
            return (long) extract;
        }
        return 0;
    }

    @Override
    public void read(JsonObject json) {
        readPerTick(json, 0);
        if (json.has("index")) this.index = json.get("index")
            .getAsInt();

        if (json.has("consume")) {
            this.consume = json.get("consume")
                .getAsBoolean();
        }

        this.aspectTag = json.get("essentia")
            .getAsString();
        this.amount = json.get("amount")
            .getAsInt();
    }

    @Override
    public void write(JsonObject json) {
        if (index != -1) json.addProperty("index", index);
        if (!consume) json.addProperty("consume", false);
        if (interval > 0) json.addProperty("pertick", interval);

        if (aspectTag != null) {
            json.addProperty("essentia", aspectTag);
        }
        json.addProperty("amount", amount);
    }

    @Override
    public boolean validate() {
        return aspectTag != null && !aspectTag.isEmpty() && amount > 0;
    }

    public static EssentiaInput fromJson(JsonObject json) {
        EssentiaInput input = new EssentiaInput("", 0);
        input.read(json);
        return input.validate() ? input : null;
    }

    @Override
    public IRecipeInput copy() {
        return copy(1);
    }

    @Override
    public IRecipeInput copy(int multiplier) {
        EssentiaInput result = new EssentiaInput(aspectTag, amount * multiplier);
        result.consume = this.consume;
        result.interval = this.interval;
        result.index = this.index;
        return result;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "essentia");
        nbt.setInteger("interval", interval);
        nbt.setBoolean("consume", consume);
        nbt.setString("aspect", aspectTag);
        nbt.setInteger("amount", amount);
        nbt.setInteger("index", index);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.interval = nbt.getInteger("interval");
        this.consume = nbt.getBoolean("consume");
        this.aspectTag = nbt.getString("aspect");
        this.amount = nbt.getInteger("amount");
        this.index = nbt.hasKey("index") ? nbt.getInteger("index") : -1;
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }
}
