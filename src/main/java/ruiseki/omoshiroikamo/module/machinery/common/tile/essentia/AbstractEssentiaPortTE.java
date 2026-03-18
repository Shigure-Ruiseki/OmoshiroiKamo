package ruiseki.omoshiroikamo.module.machinery.common.tile.essentia;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType.Direction;
import ruiseki.omoshiroikamo.api.modular.IPortType.Type;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.persist.nbt.NBTPersist;
import ruiseki.omoshiroikamo.core.tileentity.AbstractTE;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;

/**
 * Stores multiple Aspects using AspectList with unified 16-tier system.
 */
public abstract class AbstractEssentiaPortTE extends AbstractTE implements IModularPort, IAspectContainer {

    @NBTPersist
    protected int tier = 0; // 0-15 (display: 1-16)

    @NBTPersist
    protected final EnumIO[] sides = new EnumIO[6];

    protected AspectList aspects = new AspectList();
    @NBTPersist
    protected int maxCapacityPerAspect;

    public AbstractEssentiaPortTE() {
        // Capacity will be set by setTier() in onBlockPlacedBy or readCommon
        this.maxCapacityPerAspect = 64;
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
            updateEssentiaCapacity();
            markDirty();
        }
    }

    /**
     * Update Essentia capacity based on current tier.
     * Called when tier changes or after NBT load.
     */
    protected void updateEssentiaCapacity() {
        int newCapacity = MachineryConfig.getEssentiaPortCapacity(tier + 1);
        this.maxCapacityPerAspect = newCapacity;
    }

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
    public Type getPortType() {
        return Type.ESSENTIA;
    }

    @Override
    public abstract Direction getPortDirection();

    @Override
    public String getLocalizedName() {
        // Use format string from lang file: tile.modularEssentiaInput.name=Essentia Input Port Tier %d
        String unlocalizedName = getUnlocalizedName() + ".name";
        String format = StatCollector.translateToLocal(unlocalizedName);
        return String.format(format, getTier() + 1);
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
        // tier field is loaded by @NBTPersist before this method
        updateEssentiaCapacity();

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

        if (worldObj != null) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public void accept(IRecipeVisitor visitor) {}
}
