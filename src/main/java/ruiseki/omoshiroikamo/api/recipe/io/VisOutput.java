package ruiseki.omoshiroikamo.api.recipe.io;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.module.machinery.common.tile.vis.AbstractVisPortTE;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;

public class VisOutput extends AbstractRecipeOutput {

    private String aspectTag;
    private int amountCentiVis;

    public VisOutput(String aspectTag, int amountCentiVis) {
        this.aspectTag = aspectTag;
        this.amountCentiVis = amountCentiVis;
    }

    public String getAspectTag() {
        return aspectTag;
    }

    public int getAmountCentiVis() {
        return amountCentiVis;
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.VIS;
    }

    @Override
    public void apply(List<IModularPort> ports, int multiplier) {
        Aspect aspect = Aspect.getAspect(aspectTag);
        if (aspect == null) return;

        int remaining = amountCentiVis * multiplier;

        for (IModularPort port : ports) {
            if (port.getPortType() != IPortType.Type.VIS) continue;
            if (port.getPortDirection() != IPortType.Direction.OUTPUT
                && port.getPortDirection() != IPortType.Direction.BOTH) continue;

            if (!(port instanceof IAspectContainer)) continue;

            IAspectContainer visPort = (IAspectContainer) port;
            int accepted = (int) remaining - visPort.addToContainer(aspect, (int) remaining);
            remaining -= accepted;
            if (remaining <= 0) break;
        }
    }

    @Override
    protected boolean isCorrectPort(IModularPort port) {
        return port.getPortType() == IPortType.Type.VIS && port instanceof IAspectContainer;
    }

    @Override
    protected long getPortCapacity(IModularPort port) {
        if (port instanceof AbstractVisPortTE) {
            return ((AbstractVisPortTE) port).getMaxVisPerAspect();
        }
        return 100; // Default capacity for Vis if not specific TE
    }

    @Override
    public long getRequiredAmount() {
        return amountCentiVis;
    }

    @Override
    public void read(JsonObject json) {
        this.aspectTag = json.get("vis")
            .getAsString();
        this.amountCentiVis = json.has("amount") ? json.get("amount")
            .getAsInt() : 100;
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("vis", aspectTag);
        json.addProperty("amount", amountCentiVis);
    }

    @Override
    public boolean validate() {
        return aspectTag != null && !aspectTag.isEmpty() && amountCentiVis > 0;
    }

    public static VisOutput fromJson(JsonObject json) {
        VisOutput output = new VisOutput("", 0);
        output.read(json);
        return output.validate() ? output : null;
    }

    @Override
    public IRecipeOutput copy() {
        return copy(1);
    }

    @Override
    public IRecipeOutput copy(int multiplier) {
        return new VisOutput(aspectTag, amountCentiVis * multiplier);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("id", "vis");
        nbt.setString("aspect", aspectTag);
        nbt.setInteger("amount", amountCentiVis);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.aspectTag = nbt.getString("aspect");
        this.amountCentiVis = nbt.getInteger("amount");
    }

    @Override
    public void accept(IRecipeVisitor visitor) {
        visitor.visit(this);
    }
}
