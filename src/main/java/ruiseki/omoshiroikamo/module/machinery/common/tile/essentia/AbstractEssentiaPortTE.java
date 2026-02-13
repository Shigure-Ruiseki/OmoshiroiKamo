package ruiseki.omoshiroikamo.module.machinery.common.tile.essentia;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractTE;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;

/**
 * Stores multiple Aspects using AspectList.
 *
 * TODO: Use sides array in canInputFrom/canOutputTo
 * TODO: Add tiered blocks/TEs
 * TODO: Implement TEEssentiaOutputPortME
 * TODO: Add essence filter
 */
public abstract class AbstractEssentiaPortTE extends AbstractTE implements IModularPort, IAspectContainer {

    protected final EnumIO[] sides = new EnumIO[6];
    protected AspectList aspects = new AspectList();
    protected int maxCapacityPerAspect;

    public AbstractEssentiaPortTE(int maxCapacityPerAspect) {
        this.maxCapacityPerAspect = maxCapacityPerAspect;
        for (int i = 0; i < 6; i++) {
            sides[i] = getIOLimit();
        }
    }

    public abstract int getTier();

    public abstract EnumIO getIOLimit();

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

        return amount - toAdd;
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
        for (Aspect aspect : ot.getAspects()) {
            if (aspect != null && aspects.getAmount(aspect) < ot.getAmount(aspect)) {
                return false;
            }
        }
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

    @Override
    public EnumIO getSideIO(ForgeDirection side) {
        return sides[side.ordinal()];
    }

    @Override
    public void setSideIO(ForgeDirection side, EnumIO state) {
        // Disabled for Essentia ports
    }

    @Override
    public boolean isActive() {
        return getTotalEssentiaAmount() > 0;
    }

    @Override
    public IPortType.Type getPortType() {
        return IPortType.Type.ESSENTIA;
    }

    @Override
    public abstract IPortType.Direction getPortDirection();

    @Override
    public String getLocalizedName() {
        return LibMisc.LANG.localize(getUnlocalizedName() + ".tier_" + getTier() + ".name");
    }

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

    /**
     * Overlay icon used for client-side TESR rendering.
     * Default null means no overlay.
     */
    public IIcon getOverlayIcon(ForgeDirection side) {
        return null;
    }

    @Override
    public void writeCommon(NBTTagCompound root) {
        super.writeCommon(root);
        root.setInteger("maxCapacity", maxCapacityPerAspect);

        int[] sideData = new int[6];
        for (int i = 0; i < 6; i++) {
            sideData[i] = sides[i].ordinal();
        }
        root.setIntArray("sideIO", sideData);

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

        if (root.hasKey("sideIO")) {
            int[] sideData = root.getIntArray("sideIO");
            for (int i = 0; i < 6 && i < sideData.length; i++) {
                sides[i] = EnumIO.values()[sideData[i]];
            }
        }

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
