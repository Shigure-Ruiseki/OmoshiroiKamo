package ruiseki.omoshiroikamo.api.entity.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import lombok.Getter;
import lombok.Setter;

public class ModelRegistryItem {

    @Getter
    protected final int id;
    @Getter
    protected final String displayName;
    @Getter
    protected final ResourceLocation texture;

    @Getter
    @Setter
    protected int simulationRFCost;

    @Getter
    protected final String entityDisplay;
    @Getter
    protected final float numberOfHearts;
    @Getter
    protected final float interfaceScale;
    @Getter
    protected final int interfaceOffsetX;
    @Getter
    protected final int interfaceOffsetY;
    @Getter
    protected final String[] mobTrivia;

    @Getter
    protected Map<String, String> lang;

    @Getter
    @Setter
    protected String extraTooltip;

    @Getter
    @Setter
    protected ItemStack livingMatter;
    @Getter
    @Setter
    protected ItemStack pristineMatter;

    @Getter
    protected String[] associatedMobs;
    @Getter
    @Setter
    private List<Class<? extends Entity>> associatedEntityClasses;

    @Getter
    protected List<ItemStack> lootItems;
    @Getter
    protected String[] lootStrings;

    @Getter
    @Setter
    protected boolean enabled;

    public ModelRegistryItem(int id, String displayName, ResourceLocation texture, String entityDisplay,
        float numberOfHearts, float interfaceScale, int interfaceOffsetX, int interfaceOffsetY, String[] mobTrivia) {
        this.id = id;
        this.displayName = displayName;
        this.texture = texture;
        this.entityDisplay = entityDisplay;
        this.numberOfHearts = numberOfHearts;
        this.interfaceScale = interfaceScale;
        this.interfaceOffsetX = interfaceOffsetX;
        this.interfaceOffsetY = interfaceOffsetY;
        this.mobTrivia = mobTrivia;
    }

    public String getItemName() {
        return "item.model." + displayName + ".name";
    }

    public ModelRegistryItem setLootStrings(String[] lootStrings) {
        this.lootStrings = lootStrings;
        return this;
    }

    public ModelRegistryItem setLootItems(List<ItemStack> lootItems) {
        this.lootItems = lootItems;
        return this;
    }

    public ModelRegistryItem setAssociatedMobs(String[] associatedMobs) {
        this.associatedMobs = associatedMobs;
        return this;
    }

    public ModelRegistryItem setAssociatedMobsClasses(List<Class<? extends Entity>> associatedMobs) {
        this.associatedEntityClasses = associatedMobs;
        return this;
    }

    public ModelRegistryItem setLang(String langCode, String value) {
        if (this.lang == null) {
            this.lang = new HashMap<>();
        }

        if (langCode != null && !langCode.isEmpty() && value != null && !value.isEmpty()) {
            this.lang.put(langCode, value);
        }

        return this;
    }
}
