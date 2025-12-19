package ruiseki.omoshiroikamo.api.entity.model;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import lombok.Getter;
import lombok.Setter;

public class ModelRegistryItem {

    @Getter
    protected final int id;
    @Getter
    protected final String entityName;
    @Getter
    protected ResourceLocation texture;
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
    protected String[] lang;
    @Getter
    @Setter
    protected ItemStack livingMatter;
    @Getter
    @Setter
    protected ItemStack pristineMatter;
    @Getter
    protected List<ItemStack> lootItems;
    @Getter
    protected String[] lootStrings;
    @Getter
    @Setter
    protected boolean enabled;

    public ModelRegistryItem(int id, String entityName, ResourceLocation texture, float numberOfHearts,
        float interfaceScale, int interfaceOffsetX, int interfaceOffsetY, String[] mobTrivia, String[] lang) {
        this.id = id;
        this.entityName = entityName;
        this.texture = texture;
        this.numberOfHearts = numberOfHearts;
        this.interfaceScale = interfaceScale;
        this.interfaceOffsetX = interfaceOffsetX;
        this.interfaceOffsetY = interfaceOffsetY;
        this.mobTrivia = mobTrivia;
        this.lang = lang;
    }

    public String getItemName() {
        return "item.model." + entityName + ".name";
    }

    public String getDisplayName() {
        return "entity." + entityName + ".name";
    }

    public ModelRegistryItem setLootStrings(String[] lootStrings) {
        this.lootStrings = lootStrings;
        return this;
    }

    public ModelRegistryItem setLootItems(List<ItemStack> lootItems) {
        this.lootItems = lootItems;
        return this;
    }
}
