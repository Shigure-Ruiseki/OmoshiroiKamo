package ruiseki.omoshiroikamo.api.entity;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a single registry entry used by chickens/cows/etc.
 * Each registry item has:
 * <ul>
 * <li>Unique ID</li>
 * <li>Internal entity name</li>
 * <li>Drop item</li>
 * <li>Color and texture data</li>
 * <li>Optional parents for breeding</li>
 * <li>Spawn type and enabling flags</li>
 * <li>Timers influenced by tier and coefficients</li>
 * </ul>
 *
 * @param <T> The item type extending BaseRegistryItem (self-referential generic).
 */
public abstract class BaseRegistryItem<T extends BaseRegistryItem<T>> {

    protected final int id;
    protected final String entityName;
    protected String dropString;
    protected ItemStack dropItem;
    protected final int bgColor;
    protected final int fgColor;
    protected ResourceLocation texture;

    protected T parent1;
    protected T parent2;

    protected SpawnType spawnType;
    protected boolean isEnabled = true;
    protected float coefficient = 1.0f;

    protected Map<String, String> lang;

    /**
     * Creates a new registry item.
     *
     * @param id         Unique numeric identifier.
     * @param entityName Internal name used for localization.
     * @param texture    The texture reference for the item.
     * @param bgColor    Background color displayed on UI.
     * @param fgColor    Foreground color displayed on UI.
     * @param parent1    First parent, or null if this is a base tier item.
     * @param parent2    Second parent, or null if this is a base tier item.
     */
    public BaseRegistryItem(int id, String entityName, ResourceLocation texture, int bgColor, int fgColor,
        @Nullable T parent1, @Nullable T parent2) {
        this.id = id;
        this.entityName = entityName;
        this.bgColor = bgColor;
        this.fgColor = fgColor;
        this.texture = texture;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.spawnType = SpawnType.NORMAL;
    }

    /**
     * Sets the spawn type (Normal, Snow, Hell, etc.)
     *
     * @param type Spawn type.
     * @return this for chaining.
     */
    @SuppressWarnings("unchecked")
    public T setSpawnType(SpawnType type) {
        this.spawnType = type;
        return (T) this;
    }

    /**
     * Sets the coefficient used for calculating minimum/maximum timer values.
     *
     * @param coef Multiplier applied when generating lay/milk time.
     * @return this for chaining.
     */
    @SuppressWarnings("unchecked")
    public T setCoefficient(float coef) {
        this.coefficient = coef;
        return (T) this;
    }

    /**
     * Sets the ItemStack that this entity should drop.
     *
     * @param stack Item to drop.
     * @return this for chaining.
     */
    @SuppressWarnings("unchecked")
    public T setDropItem(ItemStack stack) {
        this.dropItem = stack;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setDropString(String dropString) {
        this.dropString = dropString;
        return (T) this;
    }

    /**
     * Defines this registry item's parents, allowing it to become breedable.
     *
     * @param parent1 First parent.
     * @param parent2 Second parent.
     * @return this for chaining.
     */
    @SuppressWarnings("unchecked")
    public T setParents(T parent1, T parent2) {
        this.parent1 = parent1;
        this.parent2 = parent2;
        return (T) this;
    }

    /**
     * Removes both parents, making this item a tier-1 base entity.
     */
    public void setNoParents() {
        parent1 = null;
        parent2 = null;
    }

    /**
     * @return The first parent, or null.
     */
    @Nullable
    public T getParent1() {
        return parent1;
    }

    /**
     * @return The second parent, or null.
     */
    @Nullable
    public T getParent2() {
        return parent2;
    }

    /**
     * Enables or disables this registry item.
     * Disabled items cannot spawn or be used in breeding chains.
     *
     * @param enabled true to enable, false to disable.
     */
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    /**
     * @return A safe copy of the drop ItemStack, or null.
     */
    public ItemStack createDropItem() {
        return dropItem != null ? dropItem.copy() : null;
    }

    /**
     * @return Unique numeric ID.
     */
    public int getId() {
        return id;
    }

    /**
     * @return Internal entity name (unlocalized).
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @return Localized display key (e.g. "entity.chickenIron.name").
     */
    public String getDisplayName() {
        return "entity." + entityName + ".name";
    }

    /**
     * Computes the tier of this entity.
     * Tier rules:
     * <ul>
     * <li>If no parents → tier 1</li>
     * <li>Otherwise → max(parent1.tier, parent2.tier) + 1</li>
     * </ul>
     *
     * @return Tier value ≥ 1.
     */
    public int getTier() {
        if (parent1 == null || parent2 == null) {
            return 1;
        }
        return Math.max(parent1.getTier(), parent2.getTier()) + 1;
    }

    /**
     * Returns minimal generation time (lay/milk), scaled by tier and coefficient.
     *
     * @return Minimum time in ticks.
     */
    public int getMinTime() {
        return (int) Math.max(6000 * getTier() * coefficient, 1.0f);
    }

    /**
     * @return Maximum time, always double the minimum.
     */
    public int getMaxTime() {
        return getMinTime() * 2;
    }

    /**
     * @return Spawn type of this entity.
     */
    public SpawnType getSpawnType() {
        return spawnType;
    }

    /**
     * @return Background color used for GUI.
     */
    public int getBgColor() {
        return bgColor;
    }

    /**
     * @return Foreground color used for GUI.
     */
    public int getFgColor() {
        return fgColor;
    }

    /**
     * @return The texture reference.
     */
    public ResourceLocation getTexture() {
        return texture;
    }

    /**
     * Sets the texture reference.
     *
     * @param texture The new texture.
     */
    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }

    /**
     * @return true if this entry is enabled AND both its parents (if any) are
     *         enabled.
     */
    public boolean isEnabled() {
        return !(!isEnabled || (parent1 != null && !parent1.isEnabled()) || (parent2 != null && !parent2.isEnabled()));
    }

    /**
     * Checks whether this item is a child of the given parents (order doesn't matter).
     *
     * @param parent1 Parent candidate 1.
     * @param parent2 Parent candidate 2.
     * @return true if they match.
     */
    public boolean isChildOf(T parent1, T parent2) {
        return (this.parent1 == parent1 && this.parent2 == parent2)
            || (this.parent1 == parent2 && this.parent2 == parent1);
    }

    /**
     * @return true if this item has parents and can be bred.
     */
    public boolean isBreedable() {
        return parent1 != null && parent2 != null;
    }

    /**
     * @return true if immune to fire (HELL-spawn type).
     */
    public boolean isImmuneToFire() {
        return spawnType == SpawnType.HELL;
    }

    /**
     * Determines whether this item can naturally spawn in world gen.
     * Only tier-1 items can spawn.
     *
     * @return true if spawnable.
     */
    public boolean canSpawn() {
        return getTier() == 1 && spawnType != SpawnType.NONE;
    }

    public float getCoefficient() {
        return coefficient;
    }

    public ItemStack getDropItem() {
        return dropItem;
    }

    public String getDropString() {
        return dropString;
    }

    public Map<String, String> getLang() {
        return lang;
    }

    // Not used
    public T setLang(String langCode, String value) {
        if (this.lang == null) {
            this.lang = new HashMap<>();
        }

        if (langCode != null && !langCode.isEmpty() && value != null && !value.isEmpty()) {
            this.lang.put(langCode, value);
        }

        return (T) this;
    }
}
