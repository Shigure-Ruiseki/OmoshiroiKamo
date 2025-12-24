package ruiseki.omoshiroikamo.api.entity.dml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import lombok.Getter;
import lombok.Setter;
import ruiseki.omoshiroikamo.api.item.ItemUtils;
import ruiseki.omoshiroikamo.common.init.ModItems;

public class ModelRegistryItem {

    @Getter
    protected final int id;
    @Getter
    protected final String displayName;
    @Getter
    protected final String texture;
    @Getter
    @Setter
    protected String pristineTexture;

    @Getter
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
    protected Map<String, String> pristineLang;

    @Getter
    @Setter
    protected String extraTooltip;

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
    protected String[] craftingStrings;

    @Getter
    @Setter
    protected ItemStack pristineMatter;
    @Getter
    protected ItemStack livingMatter;

    @Getter
    @Setter
    protected boolean enabled;

    public ModelRegistryItem(int id, String displayName, String texture, String entityDisplay, float numberOfHearts,
        float interfaceScale, int interfaceOffsetX, int interfaceOffsetY, String[] mobTrivia) {
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

    public String getPristineName() {
        return "item.pristine." + displayName + ".name";
    }

    public ModelRegistryItem setLootStrings(String[] lootStrings) {
        this.lootStrings = lootStrings;
        return this;
    }

    public ModelRegistryItem setLootItems(List<ItemStack> lootItems) {
        this.lootItems = lootItems;
        return this;
    }

    public ModelRegistryItem setCraftingStrings(String[] craftingStrings) {
        this.craftingStrings = craftingStrings;
        return this;
    }

    public ModelRegistryItem setSimulationRFCost(int simulationRFCost) {
        this.simulationRFCost = simulationRFCost;
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

    public ModelRegistryItem setPristineLang(String langCode, String value) {
        if (this.pristineLang == null) {
            this.pristineLang = new HashMap<>();
        }

        if (langCode != null && !langCode.isEmpty() && value != null && !value.isEmpty()) {
            this.pristineLang.put(langCode, value);
        }

        return this;
    }

    public ModelRegistryItem setLivingMatter(LivingRegistryItem livingMatter) {
        this.livingMatter = ModItems.LIVING_MATTER.newItemStack(1, livingMatter.getId());
        return this;
    }

    public ModelRegistryItem setLivingMatter(String key) {
        LivingRegistryItem livingMatter = LivingRegistry.INSTANCE.getByName(key);
        setLivingMatter(livingMatter);
        return this;
    }

    public boolean hasLootItem(ItemStack stack) {
        if (stack == null) {
            return false;
        }

        if (lootItems != null) {
            for (ItemStack loot : lootItems) {
                if (loot == null) continue;

                if (ItemUtils.areStacksEqual(loot, stack)) {
                    return true;
                }
            }
        }

        return false;
    }

    public int getLootItemIndex(ItemStack stack) {
        if (stack == null || lootItems == null || lootItems.isEmpty()) {
            return -1;
        }

        for (int i = 0; i < lootItems.size(); i++) {
            ItemStack loot = lootItems.get(i);
            if (loot == null) continue;

            if (ItemUtils.areStacksEqual(loot, stack)) {
                return i;
            }
        }

        return -1;
    }
}
