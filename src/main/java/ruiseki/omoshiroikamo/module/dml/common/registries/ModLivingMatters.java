package ruiseki.omoshiroikamo.module.dml.common.registries;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.entity.dml.LivingRegistryItem;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.dml.recipe.DMLLivingMatterReader;

public class ModLivingMatters {

    private int id = 0;
    protected String configFileName;

    public ModLivingMatters() {
        this.configFileName = "living_matter.json";
    }

    public List<LivingRegistryItem> tryRegisterLivings() {
        File configFile = new File("config/" + LibMisc.MOD_ID + "/dml/" + configFileName);
        DMLLivingMatterReader reader = new DMLLivingMatterReader(configFile);

        if (!configFile.exists()) {
            List<LivingRegistryItem> defaultModels = registerLivings();
            // Default writer for LivingMatter (needs implementation or stub)
            // For now, reader.readDefault is suitable for registration + cache
            return reader.readDefault(defaultModels);
        }

        try {
            return reader.read();
        } catch (IOException e) {
            Logger.error("Failed to read {}: {}", configFileName, e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<LivingRegistryItem> registerLivings() {
        List<LivingRegistryItem> allLivings = new ArrayList<>();

        LivingRegistryItem overworldian = addLiving(this.nextID(), "overworldian", "overworldian", 10);
        allLivings.add(overworldian);

        LivingRegistryItem hellish = addLiving(this.nextID(), "hellish", "hellish", 14);
        allLivings.add(hellish);

        LivingRegistryItem extraterrestrial = addLiving(this.nextID(), "extraterrestrial", "extraterrestrial", 20);
        allLivings.add(extraterrestrial);

        return allLivings;
    }

    public LivingRegistryItem addLiving(int id, String displayName, String texture, int xpValue) {
        return new LivingRegistryItem(id, displayName, LibResources.PREFIX_MOD + "dml/living/" + texture, xpValue);
    }

    protected int nextID() {
        return this.id++;
    }

    protected int fixedID(String name) {
        int hash = (LibMisc.MOD_ID + ":" + name).toLowerCase()
            .hashCode();
        return Math.abs(hash % (30000));
    }
}
