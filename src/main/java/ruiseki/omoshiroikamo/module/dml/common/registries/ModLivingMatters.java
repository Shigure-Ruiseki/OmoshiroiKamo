package ruiseki.omoshiroikamo.module.dml.common.registries;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.Reference;
import ruiseki.omoshiroikamo.api.entity.dml.LivingRegistryItem;
import ruiseki.omoshiroikamo.core.util.Logger;
import ruiseki.omoshiroikamo.module.dml.recipe.DMLLivingMatterReader;
import ruiseki.omoshiroikamo.module.dml.recipe.DMLLivingMatterWriter;

public class ModLivingMatters {

    private int id = 0;
    protected String configFileName;

    public ModLivingMatters() {
        this.configFileName = "living_matter.json";
    }

    public List<LivingRegistryItem> tryRegisterLivings() {
        File configFile = new File("config/" + Reference.MOD_ID + "/dml/" + configFileName);
        DMLLivingMatterReader reader = new DMLLivingMatterReader(configFile);

        if (!configFile.exists()) {
            List<LivingRegistryItem> defaultModels = registerLivings();
            try {
                new DMLLivingMatterWriter(configFile).write(defaultModels);
            } catch (IOException e) {
                Logger.error("Failed to write default config {}: {}", configFileName, e.getMessage());
            }
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
        return new LivingRegistryItem(id, displayName, Reference.PREFIX_MOD + "dml/living/" + texture, xpValue);
    }

    protected int nextID() {
        return this.id++;
    }

    protected int fixedID(String name) {
        int hash = (Reference.MOD_ID + ":" + name).toLowerCase()
            .hashCode();
        return Math.abs(hash % (30000));
    }
}
