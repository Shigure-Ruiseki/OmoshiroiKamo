package ruiseki.omoshiroikamo.api.modular.recipe;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.module.machinery.common.tile.essentia.AbstractEssentiaPortTE;
import thaumcraft.api.aspects.Aspect;

public class EssentiaInput extends AbstractRecipeInput {

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
    protected long getRequiredAmount() {
        return amount;
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port instanceof AbstractEssentiaPortTE;
    }

    @Override
    protected long consume(IModularPort port, long remaining, boolean simulate) {
        Aspect aspect = Aspect.getAspect(aspectTag);
        if (aspect == null) return 0;

        AbstractEssentiaPortTE essentiaPort = (AbstractEssentiaPortTE) port;
        int stored = essentiaPort.containerContains(aspect);
        if (stored > 0) {
            int extract = (int) Math.min(stored, remaining);
            if (!simulate) {
                essentiaPort.takeFromContainer(aspect, extract);
            }
            return extract;
        }
        return 0;
    }

    @Override
    public void read(JsonObject json) {
        this.aspectTag = json.get("essentia")
            .getAsString();
        this.amount = json.get("amount")
            .getAsInt();
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("essentia", aspectTag);
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
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }
}
