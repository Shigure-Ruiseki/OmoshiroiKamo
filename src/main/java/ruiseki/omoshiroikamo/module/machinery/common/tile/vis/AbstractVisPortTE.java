package ruiseki.omoshiroikamo.module.machinery.common.tile.vis;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractTE;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

/**
 * Abstract base class for Vis ports.
 * Stores Vis as AspectList.
 * TODO: Add tiered blocks/TEs
 * TODO: Add vis filter
 */
public abstract class AbstractVisPortTE extends AbstractTE implements IModularPort {

    @NBTPersist
    protected final EnumIO[] sides = new EnumIO[6];
    protected AspectList visStored = new AspectList();
    protected int maxVisPerAspect;
    protected boolean registeredAsSource = false;

    public AbstractVisPortTE(int maxVisPerAspect) {
        this.maxVisPerAspect = maxVisPerAspect;
        for (int i = 0; i < 6; i++) {
            sides[i] = getIOLimit();
        }
    }

    public abstract int getTier();

    public abstract EnumIO getIOLimit();

    public int addVis(Aspect aspect, int amount) {
        if (!isPrimalAspect(aspect)) return amount;

        int current = visStored.getAmount(aspect);
        int space = maxVisPerAspect - current;
        int toAdd = Math.min(amount, space);

        if (toAdd > 0) {
            visStored.add(aspect, toAdd);
            markDirty();
        }
        return amount - toAdd;
    }

    public int drainVis(Aspect aspect, int amount) {
        int available = visStored.getAmount(aspect);
        int toDrain = Math.min(amount, available);

        if (toDrain > 0) {
            visStored.reduce(aspect, toDrain);
            markDirty();
        }
        return toDrain;
    }

    public int getVisAmount(Aspect aspect) {
        return visStored.getAmount(aspect);
    }

    public int getMaxVisPerAspect() {
        return maxVisPerAspect;
    }

    public AspectList getAllVis() {
        return visStored;
    }

    public int getTotalVis() {
        int total = 0;
        for (Aspect aspect : visStored.getAspects()) {
            if (aspect != null) {
                total += visStored.getAmount(aspect);
            }
        }
        return total;
    }

    private boolean isPrimalAspect(Aspect aspect) {
        return aspect == Aspect.AIR || aspect == Aspect.WATER
            || aspect == Aspect.FIRE
            || aspect == Aspect.EARTH
            || aspect == Aspect.ORDER
            || aspect == Aspect.ENTROPY;
    }

    protected void registerAsVisSource() {
        registeredAsSource = true;
    }

    protected void unregisterAsVisSource() {
        registeredAsSource = false;
    }

    @Override
    public EnumIO getSideIO(ForgeDirection side) {
        if (side == ForgeDirection.UNKNOWN || side.ordinal() >= 6) {
            return EnumIO.NONE;
        }
        return sides[side.ordinal()];
    }

    @Override
    public void setSideIO(ForgeDirection side, EnumIO state) {
        // Disabled for Vis ports
    }

    @Override
    public boolean isActive() {
        return getTotalVis() > 0;
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.VIS;
    }

    @Override
    public abstract IPortType.Direction getPortDirection();

    @Override
    public String getLocalizedName() {
        return LibMisc.LANG.localize(getUnlocalizedName() + ".tier_" + getTier() + ".name");
    }

    @Override
    public void writeToNBT(NBTTagCompound root) {
        super.writeToNBT(root);
        root.setInteger("maxVis", maxVisPerAspect);

        NBTTagList visList = new NBTTagList();
        for (Aspect aspect : visStored.getAspects()) {
            if (aspect != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("aspect", aspect.getTag());
                tag.setInteger("amount", visStored.getAmount(aspect));
                visList.appendTag(tag);
            }
        }
        root.setTag("visStored", visList);
    }

    @Override
    public void readFromNBT(NBTTagCompound root) {
        super.readFromNBT(root);
        // Only load if saved, otherwise keep constructor default
        if (root.hasKey("maxVis")) {
            int savedMax = root.getInteger("maxVis");
            if (savedMax > 0) {
                maxVisPerAspect = savedMax;
            }
        }

        visStored = new AspectList();
        NBTTagList visList = root.getTagList("visStored", 10);
        for (int i = 0; i < visList.tagCount(); i++) {
            NBTTagCompound tag = visList.getCompoundTagAt(i);
            Aspect aspect = Aspect.getAspect(tag.getString("aspect"));
            int amount = tag.getInteger("amount");
            if (aspect != null && amount > 0) {
                visStored.add(aspect, amount);
            }
        }
    }
}
