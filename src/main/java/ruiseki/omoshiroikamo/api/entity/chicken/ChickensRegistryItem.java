package ruiseki.omoshiroikamo.api.entity.chicken;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import ruiseki.omoshiroikamo.api.entity.BaseRegistryItem;
import ruiseki.omoshiroikamo.config.backport.ChickenConfig;

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
    private String layString;

    private List<ChickenRecipe> recipes = new ArrayList<>();

    private int tintColor = 0xFFFFFF;
    private ResourceLocation textureOverlay;

    private String iconName;
    private String iconOverlayName;

    private float mutationChance = -1.0f;

    /**
     * Creates a new chicken registry item.
     *
     * @param id         unique numeric ID
     * @param entityName registry and localization name
     * @param texture    entity texture
     * @param bgColor    background color for JEI/GUI
     * @param fgColor    foreground color for JEI/GUI
     */
    public ChickensRegistryItem(int id, String entityName, ResourceLocation texture, int bgColor, int fgColor) {
        this(id, entityName, texture, bgColor, fgColor, null, null);
    }

    /**
     * Full constructor with parent definitions.
     *
     * @param parent1 chicken parent 1 (nullable)
     * @param parent2 chicken parent 2 (nullable)
     */
    public ChickensRegistryItem(int id, String entityName, ResourceLocation texture, int bgColor, int fgColor,
        @Nullable ChickensRegistryItem parent1, @Nullable ChickensRegistryItem parent2) {
        super(id, entityName, texture, bgColor, fgColor, parent1, parent2);
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

    public ChickensRegistryItem setLayString(String layString) {
        this.layString = layString;
        return this;
    }

    public ChickensRegistryItem addRecipe(ChickenRecipe recipe) {
        this.recipes.add(recipe);
        return this;
    }

    public ChickensRegistryItem addRecipe(ItemStack input, ItemStack output) {
        return addRecipe(new ChickenRecipe(input, output));
    }

    public List<ChickenRecipe> getRecipes() {
        return recipes;
    }

    public boolean isFood(ItemStack stack) {
        for (ChickenRecipe recipe : recipes) {
            if (recipe.matches(stack)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public ItemStack getOutputFromFood(ItemStack food) {
        for (ChickenRecipe recipe : recipes) {
            if (recipe.matches(food)) {
                ItemStack output = recipe.getOutput();
                return output != null ? output.copy() : null;
            }
        }
        return null;
    }

    public ChickensRegistryItem setTextureOverlay(ResourceLocation textureOverlay) {
        this.textureOverlay = textureOverlay;
        return this;
    }

    public ResourceLocation getTextureOverlay() {
        return this.textureOverlay;
    }

    public int getTintColor() {
        return tintColor;
    }

    public ChickensRegistryItem setTintColor(int tintColor) {
        this.tintColor = tintColor;
        return this;
    }

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

    public ItemStack getLayItem() {
        return layItem;
    }

    public String getLayString() {
        return layString;
    }

    public float getMutationChance() {
        return mutationChance < 0 ? ChickenConfig.getMutationChance()
            : mutationChance;
    }

    public ChickensRegistryItem setMutationChance(float mutationChance) {
        this.mutationChance = mutationChance;
        return this;
    }
}
