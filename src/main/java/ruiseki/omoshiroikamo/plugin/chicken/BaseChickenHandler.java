package ruiseki.omoshiroikamo.plugin.chicken;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import cpw.mods.fml.common.Loader;
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistry;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.common.util.Logger;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

public abstract class BaseChickenHandler {

    protected String modID;
    protected String modName;
    protected String texturesLocation;

    private int startID = 2000;

    private boolean needsMod = true;

    public BaseChickenHandler(String modID, String modName, String texturesLocation) {
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

    public List<ChickensRegistryItem> tryRegisterChickens(List<ChickensRegistryItem> allChickens) {
        Logger.info("Looking for " + modName + " chickens...");

        if (needsMod && !Loader.isModLoaded(modID)) {
            Logger.info("Skipped " + modName + " chickens → required mod \"" + modID + "\" is not loaded.");
            return allChickens;
        }

        Logger.info("Loading " + modName + " chickens...");

        return allChickens = registerChickens(allChickens);
    }

    public abstract List<ChickensRegistryItem> registerChickens(List<ChickensRegistryItem> allChickens);

    public abstract void RegisterAllParents(List<ChickensRegistryItem> allChickens);

    boolean first = true;

    protected int nextID() {
        return this.startID++;
    }

    protected ChickensRegistryItem addChicken(List<ChickensRegistryItem> chickenList, String chickenName, int chickenID,
        String texture, ItemStack layItem, int bgColor, int fgColor) {
        return addChicken(chickenList, chickenName, chickenID, texture, layItem, bgColor, fgColor, SpawnType.NONE);
    }

    protected ChickensRegistryItem addChicken(List<ChickensRegistryItem> chickenList, String chickenName, int chickenID,
        String texture, ItemStack layItem, int bgColor, int fgColor, SpawnType spawntype) {
        if (layItem == null || layItem.getItem() == null) {
            Logger.error("Error Registering (" + this.modID + ") Chicken: '" + chickenName + "' It's LayItem was null");
            return null;
        }

        Logger.debug(
            "Registering (" + this.modID
                + ") Chicken: '"
                + chickenName
                + "':"
                + chickenID
                + ":"
                + layItem.getDisplayName());

        ChickensRegistryItem chicken = new ChickensRegistryItem(
            chickenID,
            chickenName,
            new ResourceLocation(LibMisc.MOD_ID, this.texturesLocation + texture),
            layItem.copy(),
            bgColor,
            fgColor).setSpawnType(spawntype);

        chickenList.add(chicken);

        ChickenInformation
            .addChickenInformation(chickenID, new ChickenInformation(this.getModID(), "", this.getModName()));

        return chicken;
    }

    protected void setParents(ChickensRegistryItem child, Object parent1, Object parent2) {
        ChickensRegistryItem parentChicken1 = null;
        ChickensRegistryItem parentChicken2 = null;

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
            parentChicken1 = findChickenChickensMod((String) parent1);
        } else if (parent1 instanceof ChickensRegistryItem) {
            parentChicken1 = (ChickensRegistryItem) parent1;
        }

        if (parent2 instanceof String) {
            parentChicken2 = findChickenChickensMod((String) parent2);
        } else if (parent2 instanceof ChickensRegistryItem) {
            parentChicken2 = (ChickensRegistryItem) parent2;
        }

        if (parentChicken1 == null) {
            Logger.error("Could not find Parent 1 for " + child.getEntityName());
            return;
        }

        if (parentChicken2 == null) {
            Logger.error("Could not find Parent 2 for " + child.getEntityName());
            return;
        }

        child.setParentsNew(parentChicken1, parentChicken2);

    }

    public static ChickensRegistryItem findChicken(Collection<ChickensRegistryItem> chickens, String name) {

        for (ChickensRegistryItem chicken : chickens) {
            if (chicken.getEntityName()
                .compareToIgnoreCase(name) == 0) {
                return chicken;
            }
        }

        return findChickenChickensMod(name);
    }

    // Looks for a chicken inside Chickens mod
    public static ChickensRegistryItem findChickenChickensMod(String name) {
        for (ChickensRegistryItem chicken : ChickensRegistry.getItems()) {
            if (chicken.getEntityName()
                .compareToIgnoreCase(name) == 0) {

                return chicken;
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
