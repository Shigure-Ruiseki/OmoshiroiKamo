package ruiseki.omoshiroikamo.api.entity.chicken;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import ruiseki.omoshiroikamo.api.entity.BaseRegistryItem;

/**
 * Represents a single chicken type registered in {@link ChickensRegistry}.
 *
 * <p>
 * Each chicken has:
 * <ul>
 * <li>A unique ID</li>
 * <li>A display/registry name</li>
 * <li>Texture</li>
 * <li>Primary lay item (egg alternative)</li>
 * <li>Optional parents for breeding</li>
 * <li>Tier determined by its parents</li>
 * </ul>
 *
 * <p>
 * Also includes dye-specific helper methods for chickens whose lay item is dye.
 */
public class ChickensRegistryItem extends BaseRegistryItem<ChickensRegistryItem> {

    /**
     * The item this chicken lays periodically.
     */
    private ItemStack layItem;

    private ResourceLocation textureOverlay;

    /**
     * Creates a new chicken registry item.
     *
     * @param id         unique numeric ID
     * @param entityName registry and localization name
     * @param texture    entity texture
     * @param layItem    the item this chicken lays
     * @param bgColor    background color for JEI/GUI
     * @param fgColor    foreground color for JEI/GUI
     */
    public ChickensRegistryItem(int id, String entityName, ResourceLocation texture, ItemStack layItem, int bgColor,
        int fgColor) {
        this(id, entityName, texture, layItem, bgColor, fgColor, null, null);
    }

    private int tintColor = 0xFFFFFF;

    public int getTintColor() {
        return tintColor;
    }

    public ChickensRegistryItem setTintColor(int tintColor) {
        this.tintColor = tintColor;
        return this;
    }

    /**
     * Full constructor with parent definitions.
     *
     * @param parent1 chicken parent 1 (nullable)
     * @param parent2 chicken parent 2 (nullable)
     */
    public ChickensRegistryItem(int id, String entityName, ResourceLocation texture, ItemStack layItem, int bgColor,
        int fgColor, @Nullable ChickensRegistryItem parent1, @Nullable ChickensRegistryItem parent2) {
        super(id, entityName, texture, bgColor, fgColor, parent1, parent2);
        this.layItem = layItem;
    }

    /**
     * Sets the item this chicken lays.
     *
     * @param itemStack item to lay
     * @return this item (for chaining)
     */
    public ChickensRegistryItem setLayItem(ItemStack itemStack) {
        this.layItem = itemStack;
        return this;
    }

    public ChickensRegistryItem setTextureOverlay(ResourceLocation textureOverlay) {
        this.textureOverlay = textureOverlay;
        return this;
    }

    public ResourceLocation getTextureOverlay() {
        return this.textureOverlay;
    }

    private String iconName;
    private String iconOverlayName;

    public ChickensRegistryItem setIconName(String iconName) {
        this.iconName = iconName;
        return this;
    }

    public String getIconName() {
        return this.iconName;
    }

    public ChickensRegistryItem setIconOverlayName(String iconOverlayName) {
        this.iconOverlayName = iconOverlayName;
        return this;
    }

    public String getIconOverlayName() {
        return this.iconOverlayName;
    }

    /**
     * @return drop item (overridden to use lay item as fallback)
     */
    @Override
    public ItemStack createDropItem() {
        return dropItem != null ? dropItem.copy() : createLayItem();
    }

    /**
     * @return a new copy of the primary lay item
     */
    public ItemStack createLayItem() {
        return layItem.copy();
    }

    /**
     * @return metadata value if the lay item is dye
     */
    public int getDyeMetadata() {
        return layItem.getItemDamage();
    }

    /**
     * @return true if the lay item is dye (all colors)
     */
    public boolean isDye() {
        return layItem.getItem() == Items.dye;
    }

    /**
     * Checks if this chicken lays a specific dye color.
     *
     * @param dyeMetadata the dye damage value
     * @return true if this chicken lays that dye color
     */
    public boolean isDye(int dyeMetadata) {
        return layItem.getItem() == Items.dye && layItem.getItemDamage() == dyeMetadata;
    }
}
