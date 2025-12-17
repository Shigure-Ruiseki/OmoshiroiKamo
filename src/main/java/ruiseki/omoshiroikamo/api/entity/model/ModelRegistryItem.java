package ruiseki.omoshiroikamo.api.entity.model;

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
    @Setter
    protected float numberOfHearts;
    @Getter
    @Setter
    protected float interfaceScale;
    @Getter
    @Setter
    protected int interfaceOffsetX;
    @Getter
    @Setter
    protected int interfaceOffsetY;
    @Getter
    @Setter
    protected String[] mobTrivia;
    @Getter
    @Setter
    protected ItemStack livingMatter;
    @Getter
    @Setter
    protected ItemStack pristineMatter;
    @Getter
    @Setter
    protected String[] lang;
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

    public String getDisplayName() {
        return "item.model." + entityName + ".name";
    }
}
