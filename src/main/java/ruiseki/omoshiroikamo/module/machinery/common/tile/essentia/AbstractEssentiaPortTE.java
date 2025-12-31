package ruiseki.omoshiroikamo.module.machinery.common.tile.essentia;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractTE;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;

/**
 * Abstract base class for Essentia ports.
 * Stores multiple Aspects using AspectList.
 */
public abstract class AbstractEssentiaPortTE extends AbstractTE implements IModularPort, IAspectContainer {

    protected AspectList aspects = new AspectList();
    protected int maxCapacityPerAspect;

    public AbstractEssentiaPortTE(int maxCapacityPerAspect) {
        this.maxCapacityPerAspect = maxCapacityPerAspect;
    }

    // ========== IAspectContainer Implementation ==========

    @Override
    public AspectList getAspects() {
        return aspects;
    }

    @Override
    public void setAspects(AspectList aspects) {
        this.aspects = aspects;
        markDirty();
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        int current = aspects.getAmount(tag);
        int space = maxCapacityPerAspect - current;
        int toAdd = Math.min(amount, space);

        if (toAdd > 0) {
            aspects.add(tag, toAdd);
            markDirty();
        }

        return amount - toAdd; // Return leftover
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        if (aspects.getAmount(tag) >= amount) {
            aspects.reduce(tag, amount);
            markDirty();
            return true;
        }
        return false;
    }

    @Override
    @Deprecated
    public boolean takeFromContainer(AspectList ot) {
        // Check if all aspects are available
        for (Aspect aspect : ot.getAspects()) {
            if (aspect != null && aspects.getAmount(aspect) < ot.getAmount(aspect)) {
                return false;
            }
        }
        // Remove all
        for (Aspect aspect : ot.getAspects()) {
            if (aspect != null) {
                aspects.reduce(aspect, ot.getAmount(aspect));
            }
        }
        markDirty();
        return true;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return aspects.getAmount(tag) >= amount;
    }

    @Override
    @Deprecated
    public boolean doesContainerContain(AspectList ot) {
        for (Aspect aspect : ot.getAspects()) {
            if (aspect != null && aspects.getAmount(aspect) < ot.getAmount(aspect)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int containerContains(Aspect tag) {
        return aspects.getAmount(tag);
    }

    // ========== IModularPort Implementation ==========

    @Override
    public IO getSideIO(ForgeDirection side) {
        return IO.BOTH;
    }

    @Override
    public void setSideIO(ForgeDirection side, IO state) {}

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.ESSENTIA;
    }

    @Override
    public abstract IPortType.Direction getPortDirection();

    // ========== Utility Methods ==========

    public int getTotalEssentiaAmount() {
        int total = 0;
        for (Aspect aspect : aspects.getAspects()) {
            if (aspect != null) {
                total += aspects.getAmount(aspect);
            }
        }
        return total;
    }

    public int getMaxCapacityPerAspect() {
        return maxCapacityPerAspect;
    }

    @Override
    public String getLocalizedName() {
        return LibMisc.LANG.localize(getUnlocalizedName() + ".name");
    }

    // ========== NBT Persistence ==========

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);
        root.setInteger("maxCapacity", maxCapacityPerAspect);

        NBTTagList aspectList = new NBTTagList();
        for (Aspect aspect : aspects.getAspects()) {
            if (aspect != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("aspect", aspect.getTag());
                tag.setInteger("amount", aspects.getAmount(aspect));
                aspectList.appendTag(tag);
            }
        }
        root.setTag("aspects", aspectList);
    }

    @Override
    public void readCommon(NBTTagCompound root) {
        super.readCommon(root);
        maxCapacityPerAspect = root.getInteger("maxCapacity");

        aspects = new AspectList();
        NBTTagList aspectList = root.getTagList("aspects", 10);
        for (int i = 0; i < aspectList.tagCount(); i++) {
            NBTTagCompound tag = aspectList.getCompoundTagAt(i);
            Aspect aspect = Aspect.getAspect(tag.getString("aspect"));
            int amount = tag.getInteger("amount");
            if (aspect != null && amount > 0) {
                aspects.add(aspect, amount);
            }
        }
    }
}
