package ruiseki.omoshiroikamo.module.dml.common.registries;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.Loader;
import lombok.Getter;
import ruiseki.omoshiroikamo.api.entity.dml.ModelRegistryItem;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.dml.recipe.DMLBaseModelReader;
import ruiseki.omoshiroikamo.module.dml.recipe.DMLBaseModelWriter;

// Refactor base on OriginalChicken by Chlorine0808
public abstract class BaseModelHandler {

    @Getter
    protected String modID;
    @Getter
    protected String modName;
    protected String texturesLocation;

    private int startID = 0;
    private int id = 0;
    protected String configFileName;

    private boolean needsMod = true;

    public BaseModelHandler(String modID, String modName, String texturesLocation) {
        this.modID = modID;
        this.modName = modName;
        this.texturesLocation = texturesLocation;
        this.configFileName = modID.toLowerCase() + "_models.json";
    }

    public void setStartID(int startID) {
        this.startID = startID;
        this.id = startID;
    }

    public void setNeedsModPresent(boolean bool) {
        this.needsMod = bool;
    }

    public List<ModelRegistryItem> tryRegisterModels() {
        Logger.info("Looking for {} models...", modName);
        if (needsMod && !Loader.isModLoaded(modID)) {
            Logger.info("Skipped {} models → required mod \"{}\" is not loaded.", modName, modID);
            return new ArrayList<>();
        }
        Logger.info("Loading {} models...", modName);

        File configFile = new File("config/" + LibMisc.MOD_ID + "/dml/" + configFileName);
        DMLBaseModelReader reader = new DMLBaseModelReader(configFile);

        if (!configFile.exists()) {
            List<ModelRegistryItem> defaultModels = registerModels();
            try {
                new DMLBaseModelWriter(configFile).write(defaultModels);
            } catch (IOException e) {
                Logger.error("Failed to write default config {}: {}", configFileName, e.getMessage());
            }
        }

        try {
            return reader.read();
        } catch (IOException e) {
            Logger.error("Failed to read {}: {}", configFileName, e.getMessage());
            return new ArrayList<>();
        }
    }

    public abstract List<ModelRegistryItem> registerModels();

    protected int nextID() {
        return this.id++;
    }

    protected int fixedID(String name) {
        int hash = (modID + ":" + name).toLowerCase()
            .hashCode();
        return startID + Math.abs(hash % (30000 - startID));
    }

    public ModelRegistryItem addModel(String displayName, int id, String texture, String entityDisplay,
        float numberOfHearts, float interfaceScale, int interfaceOffsetX, int interfaceOffsetY, String[] mobTrivia) {

        int finalID = id >= 0 ? id : fixedID(displayName);

        ModelRegistryItem item = new ModelRegistryItem(
            finalID,
            displayName,
            LibResources.PREFIX_MOD + "dml/model/" + this.texturesLocation + texture,
            entityDisplay,
            numberOfHearts,
            interfaceScale,
            interfaceOffsetX,
            interfaceOffsetY,
            mobTrivia);

        item.setPristineTexture(
            LibResources.PREFIX_MOD + "dml/pristine/" + this.texturesLocation + "pristine_matter_" + texture);

        return item;
    }
}
