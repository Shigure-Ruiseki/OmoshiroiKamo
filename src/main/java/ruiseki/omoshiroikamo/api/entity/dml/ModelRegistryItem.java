package ruiseki.omoshiroikamo.api.entity.dml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.Getter;
import lombok.Setter;
import ruiseki.omoshiroikamo.core.item.ItemUtils;
import ruiseki.omoshiroikamo.core.json.AbstractJsonMaterial;
import ruiseki.omoshiroikamo.core.json.ItemJson;
import ruiseki.omoshiroikamo.module.dml.common.init.DMLItems;

public class ModelRegistryItem extends AbstractJsonMaterial {

    @Getter
    protected int id;
    @Getter
    protected String displayName;
    @Getter
    @Setter
    protected String texture;
    @Getter
    @Setter
    protected String pristineTexture;

    @Getter
    protected int simulationRFCost;

    @Getter
    protected String entityDisplay;
    @Getter
    protected float numberOfHearts;
    @Getter
    protected float interfaceScale;
    @Getter
    protected int interfaceOffsetX;
    @Getter
    protected int interfaceOffsetY;
    @Getter
    protected String[] mobTrivia;

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

    public ModelRegistryItem() {
        this.enabled = true;
    }

    public ModelRegistryItem(int id, String displayName, String texture, String entityDisplay, float numberOfHearts,
        float interfaceScale, int interfaceOffsetX, int interfaceOffsetY, String[] mobTrivia) {
        this();
        this.id = id;
        this.displayName = displayName;
        this.texture = texture;
        this.entityDisplay = entityDisplay;
        this.numberOfHearts = numberOfHearts;
        this.interfaceScale = interfaceScale;
        this.interfaceOffsetX = interfaceOffsetX;
        this.interfaceOffsetY = interfaceOffsetY;
        this.mobTrivia = mobTrivia;

        if (this.id >= 0) {
            this.pristineMatter = DMLItems.PRISTINE_MATTER.newItemStack(1, this.id);
        }
    }

    @Override
    public void read(JsonObject json) {
        this.id = getInt(json, "id", 0);
        this.displayName = getString(json, "displayName", "Unknown");
        this.enabled = getBoolean(json, "enabled", true);
        this.texture = getString(json, "texture", null);
        this.simulationRFCost = getInt(json, "simulationRFCost", 256);
        this.pristineTexture = getString(json, "pristineTexture", texture + "_pristine");
        this.extraTooltip = getString(json, "extraTooltip", null);
        this.craftingStrings = getStringArray(json, "craftingStrings");
        this.lang = getMap(json, "lang");
        this.pristineLang = getMap(json, "pristineLang");

        // Deep Learner Display
        if (json.has("deepLearnerDisplay") && json.get("deepLearnerDisplay")
            .isJsonObject()) {
            JsonObject display = json.getAsJsonObject("deepLearnerDisplay");
            this.entityDisplay = getString(display, "entityDisplay", displayName);
            this.numberOfHearts = getFloat(display, "numberOfHearts", 10.0f);
            this.interfaceScale = getFloat(display, "interfaceScale", 1.0f);
            this.interfaceOffsetX = getInt(display, "interfaceOffsetX", 0);
            this.interfaceOffsetY = getInt(display, "interfaceOffsetY", 0);
            this.mobTrivia = getStringArray(display, "mobTrivia");
        }

        // Loot Items
        if (json.has("lootItems") && json.get("lootItems")
            .isJsonArray()) {
            JsonArray loot = json.getAsJsonArray("lootItems");
            this.lootItems = new ArrayList<>();
            for (JsonElement e : loot) {
                if (e.isJsonObject()) {
                    ItemStack stack = ItemJson.resolveItemStack(ItemJson.fromJson(e.getAsJsonObject()));
                    if (stack != null) this.lootItems.add(stack);
                }
            }
        }

        // Associated Mobs
        this.associatedMobs = getStringArray(json, "associatedMobs");

        // Living Matter
        if (json.has("livingMatter")) {
            setLivingMatter(
                json.get("livingMatter")
                    .getAsString());
        }

        // Initialize pristine matter stack
        if (this.id >= 0) {
            this.pristineMatter = DMLItems.PRISTINE_MATTER.newItemStack(1, this.id);
        }
    }

    @Override
    public void write(JsonObject json) {
        json.addProperty("id", id);
        json.addProperty("displayName", displayName);
        json.addProperty("enabled", enabled);
        json.addProperty("texture", texture);
        json.addProperty("simulationRFCost", simulationRFCost);
        json.addProperty("pristineTexture", pristineTexture);
        if (extraTooltip != null) json.addProperty("extraTooltip", extraTooltip);
        writeStringArray(json, "craftingStrings", craftingStrings);
        writeMap(json, "lang", lang);
        writeMap(json, "pristineLang", pristineLang);

        int livingMatterId = livingMatter != null ? livingMatter.getItemDamage() : 0;
        json.addProperty(
            "livingMatter",
            LivingRegistry.INSTANCE.getByType(livingMatterId) != null
                ? LivingRegistry.INSTANCE.getByType(livingMatterId)
                    .getDisplayName()
                : "overworldian");

        // Deep Learner Display
        JsonObject display = new JsonObject();
        display.addProperty("entityDisplay", entityDisplay);
        display.addProperty("numberOfHearts", numberOfHearts);
        display.addProperty("interfaceScale", interfaceScale);
        display.addProperty("interfaceOffsetX", interfaceOffsetX);
        display.addProperty("interfaceOffsetY", interfaceOffsetY);
        writeStringArray(display, "mobTrivia", mobTrivia);
        json.add("deepLearnerDisplay", display);

        // Loot Items
        if (lootItems != null && !lootItems.isEmpty()) {
            JsonArray loot = new JsonArray();
            for (ItemStack stack : lootItems) {
                ItemJson item = ItemJson.parseItemStack(stack);
                if (item != null) {
                    JsonObject itemObj = new JsonObject();
                    item.write(itemObj);
                    loot.add(itemObj);
                }
            }
            json.add("lootItems", loot);
        }

        // Associated Mobs
        writeStringArray(json, "associatedMobs", associatedMobs);
    }

    @Override
    public boolean validate() {
        if (id < 0) {
            logValidationError("ID must be non-negative");
            return false;
        }
        if (displayName == null || displayName.isEmpty()) {
            logValidationError("Display name cannot be empty");
            return false;
        }
        return true;
    }

    public String getItemName() {
        String name = displayName != null ? displayName : "Unknown";
        return "item.model." + name + ".name";
    }

    public String getPristineName() {
        return "item.pristine." + displayName + ".name";
    }

    public ModelRegistryItem setLootStrings(String[] lootStrings) {
        if (lootStrings == null) {
            this.lootStrings = null;
            this.lootItems = null;
            return this;
        }
        List<String> filtered = new ArrayList<>();
        List<ItemStack> resolvedItems = new ArrayList<>();
        for (String s : lootStrings) {
            ItemJson json = ItemJson.parseItemString(s);
            if (json != null) {
                ItemStack stack = ItemJson.resolveItemStack(json);
                if (stack != null) {
                    filtered.add(s);
                    resolvedItems.add(stack);
                }
            }
        }
        this.lootStrings = filtered.toArray(new String[0]);
        this.lootItems = resolvedItems;
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

    // Not used
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
        this.livingMatter = DMLItems.LIVING_MATTER.newItemStack(1, livingMatter.getId());
        return this;
    }

    public ModelRegistryItem setLivingMatter(String key) {
        LivingRegistryItem livingMatter = LivingRegistry.INSTANCE.getByName(key);
        if (livingMatter != null) {
            this.livingMatter = DMLItems.LIVING_MATTER.newItemStack(1, livingMatter.getId());
        }
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
