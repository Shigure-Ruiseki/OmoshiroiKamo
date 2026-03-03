package ruiseki.omoshiroikamo.module.dml.common.writer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ruiseki.omoshiroikamo.core.json.AbstractJsonWriter;

public class MobDataWriter extends AbstractJsonWriter<MobDataWriter.MobData> {

    private final File baseDir;

    public MobDataWriter(File baseDir) {
        super(new File(baseDir, "placeholder.json"));
        this.baseDir = baseDir;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public void dump(MobData data) throws IOException {
        String safeName = data.name.replaceAll("[:\\\\/*?\"<>|]", "_");
        File jsonFile = new File(baseDir, safeName + ".json");
        writeObjectToFile(jsonFile, data);
    }

    public static class MobData {

        public String name;
        public String entityId;
        public double maxHealth;
        public String texturePath;
        public List<DropData> drops;

        public MobData(String name, String entityId, double maxHealth, String texturePath, List<DropData> drops) {
            this.name = name;
            this.entityId = entityId;
            this.maxHealth = maxHealth;
            this.texturePath = texturePath;
            this.drops = drops;
        }
    }

    public static class DropData {

        public String itemId;
        public int metadata;
        public int stackSize;

        public DropData(String itemId, int metadata, int stackSize) {
            this.itemId = itemId;
            this.metadata = metadata;
            this.stackSize = stackSize;
        }
    }
}
