package ruiseki.omoshiroikamo.module.machinery.common.tile.vis;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.core.tileentity.AbstractTE;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;

/**
 * Abstract base class for Vis ports.
 * Stores Vis as AspectList with unified 16-tier system.
 */
public abstract class AbstractVisPortTE extends AbstractTE implements IModularPort, IAspectContainer {

    @NBTPersist
    protected int tier = 0; // 0-15 (display: 1-16)

    @NBTPersist
    protected final EnumIO[] sides = new EnumIO[6];
    protected AspectList visStored = new AspectList();

    @NBTPersist
    protected int maxVisPerAspect;
    protected boolean registeredAsSource = false;

    public AbstractVisPortTE() {
        // Capacity will be set by setTier() in onBlockPlacedBy or readCommon
        this.maxVisPerAspect = 100;
        for (int i = 0; i < 6; i++) {
            sides[i] = getIOLimit();
        }
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public void setTier(int tier) {
        if (this.tier != tier) {
            this.tier = tier;
            updateVisCapacity();
            markDirty();
        }
    }

    /**
     * Update Vis capacity based on current tier.
     * Called when tier changes or after NBT load.
     */
    protected void updateVisCapacity() {
        int newCapacity = MachineryConfig.getVisPortCapacity(tier + 1);
        this.maxVisPerAspect = newCapacity;
    }

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

    @Override
    public AspectList getAspects() {
        return visStored;
    }

    @Override
    public void setAspects(AspectList aspects) {
        this.visStored = aspects;
        markDirty();
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return isPrimalAspect(tag);
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        return addVis(tag, amount);
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        int drained = drainVis(tag, amount);
        return drained >= amount;
    }

    @Override
    public boolean takeFromContainer(AspectList ot) {
        for (Aspect aspect : ot.getAspects()) {
            if (aspect != null && visStored.getAmount(aspect) < ot.getAmount(aspect)) {
                return false;
            }
        }
        for (Aspect aspect : ot.getAspects()) {
            if (aspect != null) {
                drainVis(aspect, ot.getAmount(aspect));
            }
        }
        return true;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return visStored.getAmount(tag) >= amount;
    }

    @Override
    public boolean doesContainerContain(AspectList ot) {
        for (Aspect aspect : ot.getAspects()) {
            if (aspect != null && visStored.getAmount(aspect) < ot.getAmount(aspect)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int containerContains(Aspect tag) {
        return visStored.getAmount(tag);
    }

    @Override
    public void accept(IRecipeVisitor visitor) {}

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
    public Type getPortType() {
        return Type.VIS;
    }

    @Override
    public abstract Direction getPortDirection();

    @Override
    public String getLocalizedName() {
        String unlocalizedName = getUnlocalizedName() + ".name";
        String format = StatCollector.translateToLocal(unlocalizedName);
        return String.format(format, getTier() + 1);
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);
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
    public void readCommon(NBTTagCompound root) {
        if (root.hasKey("tier")) {
            this.tier = root.getInteger("tier");
            updateVisCapacity();
        }
        super.readCommon(root);

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

        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public int getAssignedIndex() {
        return assignedIndex;
    }

    @Override
    public void setAssignedIndex(int index) {
        this.assignedIndex = index;
    }
}
