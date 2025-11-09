package ruiseki.omoshiroikamo.plugin.cow;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.Loader;
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistry;
import ruiseki.omoshiroikamo.api.entity.cow.CowsRegistryItem;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.plugin.ModCompatInformation;

public abstract class BaseCowHandler {

    protected String modID;
    protected String modName;
    protected String texturesLocation;

    private int startID = 2000;

    private boolean needsMod = true;

    public BaseCowHandler(String modID, String modName, String texturesLocation) {
        this.modID = modID;
        this.modName = modName;
        this.texturesLocation = texturesLocation;
    }

    public String getModID() {
        return this.modID;
    }

    public String getModName() {
        return this.modName;
    }

    public void setNeedsModPresent(boolean bool) {
        this.needsMod = bool;
    }

    public void setStartID(int startID) {
        this.startID = startID;
    }

    public List<CowsRegistryItem> tryRegisterChickens(List<CowsRegistryItem> allCows) {
        Logger.info("Looking for " + modName + " cows...");

        if (needsMod && !Loader.isModLoaded(modID)) {
            Logger.info("Skipped " + modName + " cows → required mod \"" + modID + "\" is not loaded.");
            return allCows;
        }

        Logger.info("Loading " + modName + " cows...");

        return allCows = registerCows(allCows);
    }

    public abstract List<CowsRegistryItem> registerCows(List<CowsRegistryItem> allCows);

    public abstract void registerAllParents(List<CowsRegistryItem> allCows);

    boolean first = true;

    protected int nextID() {
        return this.startID++;
    }

    protected CowsRegistryItem addCow(List<CowsRegistryItem> cowList, String cowName, int cowID, FluidStack fluid,
        int bgColor, int fgColor, SpawnType spawntype) {
        if (fluid == null || fluid.getFluid() == null) {
            Logger.error("Error Registering (" + this.modID + ") Cow: '" + cowName + "' It's fluid was null");
            return null;
        }

        Logger
            .debug("Registering (" + this.modID + ") Cow: '" + cowName + "':" + cowID + ":" + fluid.getLocalizedName());

        CowsRegistryItem cow = new CowsRegistryItem(
            cowID,
            cowName,
            new ResourceLocation("textures/entity/cow/cow.png"),
            fluid.copy(),
            bgColor,
            fgColor).setSpawnType(spawntype);

        cowList.add(cow);

        ModCompatInformation.addInformation(cowID, new ModCompatInformation(this.getModID(), "", this.getModName()));

        return cow;
    }

    protected void setParents(CowsRegistryItem child, Object parent1, Object parent2) {
        CowsRegistryItem parentCow1 = null;
        CowsRegistryItem parentCow2 = null;

        if (child == null || parent1 == null || parent2 == null) {
            String msg = "Setting Parents ";
            if (child == null) {
                msg += ": Child Missing";
            } else {
                msg += ": " + child.getEntityName();
            }
            if (parent1 == null) {
                msg += ": Parent 1 Missing ";
            }
            if (parent2 == null) {
                msg += ": Parent 2 Missing";
            }

            Logger.debug(msg);
            return;
        }

        if (parent1 instanceof String) {
            parentCow1 = findCowCowsMod((String) parent1);
        } else if (parent1 instanceof CowsRegistryItem) {
            parentCow1 = (CowsRegistryItem) parent1;
        }

        if (parent2 instanceof String) {
            parentCow2 = findCowCowsMod((String) parent2);
        } else if (parent2 instanceof CowsRegistryItem) {
            parentCow2 = (CowsRegistryItem) parent2;
        }

        if (parentCow1 == null) {
            Logger.error("Could not find Parent 1 for " + child.getEntityName());
            return;
        }

        if (parentCow2 == null) {
            Logger.error("Could not find Parent 2 for " + child.getEntityName());
            return;
        }

        child.setParents(parentCow1, parentCow2);

    }

    public static CowsRegistryItem findCow(Collection<CowsRegistryItem> cows, String name) {

        for (CowsRegistryItem cow : cows) {
            if (cow.getEntityName()
                .compareToIgnoreCase(name) == 0) {
                return cow;
            }
        }

        return findCowCowsMod(name);
    }

    public static CowsRegistryItem findCowCowsMod(String name) {
        for (CowsRegistryItem cow : CowsRegistry.INSTANCE.getItems()) {
            if (cow.getEntityName()
                .compareToIgnoreCase(name) == 0) {

                return cow;
            }
        }

        return null;
    }

    @Nullable
    public ItemStack getFirstOreDictionary(String oreID) {
        List<ItemStack> itemstacks = OreDictionary.getOres(oreID);
        return !itemstacks.isEmpty() ? itemstacks.get(0) : null;
    }
}
