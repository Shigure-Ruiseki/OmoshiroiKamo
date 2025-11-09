package ruiseki.omoshiroikamo.api.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.world.biome.BiomeGenBase;

/**
 * A generic registry storing {@link BaseRegistryItem} objects by ID.
 * Provides utilities for lookup, validation, spawning logic, and
 * parent–child breeding selection.
 *
 * @param <T> Type extending {@link BaseRegistryItem}
 */
public abstract class BaseRegistry<T extends BaseRegistryItem<T>> {

    /**
     * All registered items mapped by their ID.
     */
    protected final Map<Integer, T> items = new HashMap<>();

    /**
     * Random instance reused for breeding and spawn roll calculations.
     */
    protected final Random rand = new Random();

    /**
     * Registers a new item into the registry.
     * Validates for duplicate ID or duplicate name.
     *
     * @param entity The item to register.
     * @throws RuntimeException if ID or name already exists.
     */
    public void register(T entity) {
        validate(entity);
        items.put(entity.getId(), entity);
    }

    /**
     * Validates that the given entity does not duplicate an ID or name.
     *
     * @param entity The entity to validate.
     * @throws RuntimeException If ID or name is duplicated.
     */
    protected void validate(T entity) {
        for (T item : items.values()) {
            if (entity.getId() == item.getId()) {
                throw new RuntimeException(
                    "Duplicated ID [" + entity
                        .getId() + "] of [" + entity.getEntityName() + "] with [" + item.getEntityName() + "]!");
            }
            if (entity.getEntityName()
                .equalsIgnoreCase(item.getEntityName())) {
                throw new RuntimeException("Duplicated name [" + entity.getEntityName() + "]!");
            }
        }
    }

    /**
     * Gets a registered item by its ID.
     *
     * @param id The item type ID.
     * @return The item, or null if not found.
     */
    public T getByType(int id) {
        return items.get(id);
    }

    /**
     * Gets all enabled items in this registry.
     *
     * @return Collection of enabled items.
     */
    public Collection<T> getItems() {
        List<T> result = new ArrayList<>();
        for (T t : items.values()) {
            if (t.isEnabled()) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * Returns all possible "children" of two parents,
     * including the parents themselves if enabled.
     *
     * @param parent1 First parent.
     * @param parent2 Second parent.
     * @return List of items that can be a child of parent1 and parent2.
     */
    public List<T> getChildren(T parent1, T parent2) {
        List<T> result = new ArrayList<>();
        if (parent1.isEnabled()) {
            result.add(parent1);
        }
        if (parent2.isEnabled()) {
            result.add(parent2);
        }

        for (T item : items.values()) {
            if (item.isEnabled() && item.isChildOf(parent1, parent2)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Rolls a random child from two parents, based on the tier chance system.
     *
     * @param parent1 First parent.
     * @param parent2 Second parent.
     * @return A randomly chosen child, or null if none exists.
     */
    @Nullable
    public T getRandomChild(T parent1, T parent2) {
        List<T> possibleChildren = getChildren(parent1, parent2);
        if (possibleChildren.isEmpty()) {
            return null;
        }

        int maxChance = getMaxChance(possibleChildren);
        int maxDiceValue = getMaxDiceValue(possibleChildren, maxChance);
        int diceValue = rand.nextInt(maxDiceValue);

        return getChickenToBeBorn(possibleChildren, maxChance, diceValue);
    }

    /**
     * Returns a list of all items that are allowed to spawn
     * in the given biome spawn type.
     *
     * @param spawnType Type of spawn environment.
     * @return List of items allowed to spawn.
     */
    public List<T> getPossibleToSpawn(SpawnType spawnType) {
        List<T> result = new ArrayList<>();
        for (T t : items.values()) {
            if (t.canSpawn() && t.getSpawnType() == spawnType && t.isEnabled()) {
                result.add(t);
            }
        }
        return result;
    }

    /**
     * Gets spawn type from a biome.
     *
     * @param biome The biome.
     * @return The corresponding spawn type.
     */
    public static SpawnType getSpawnType(BiomeGenBase biome) {
        if (biome == BiomeGenBase.hell) {
            return SpawnType.HELL;
        }

        boolean isSnowy = biome.getEnableSnow() || biome.temperature < 0.2F;
        if (biome == BiomeGenBase.extremeHills || isSnowy) {
            return SpawnType.SNOW;
        }

        return SpawnType.NORMAL;
    }

    /**
     * Computes the chance that the given child will be selected from its parents.
     *
     * @param child The child to check.
     * @return Percentage chance (0–100).
     */
    public float getChildChance(T child) {
        if (child.getTier() <= 1) {
            return 0;
        }

        List<T> possibleChildren = getChildren(child.getParent1(), child.getParent2());
        int maxChance = getMaxChance(possibleChildren);
        int maxDiceValue = getMaxDiceValue(possibleChildren, maxChance);

        return ((maxChance - child.getTier()) * 100.0f) / maxDiceValue;
    }

    /**
     * Internal logic: selects the actual child based on RNG roll.
     */
    @Nullable
    private T getChickenToBeBorn(List<T> possibleChildren, int maxChance, int diceValue) {
        int currentValue = 0;
        for (T child : possibleChildren) {
            currentValue += maxChance - child.getTier();
            if (diceValue < currentValue) {
                return child;
            }
        }
        return null;
    }

    /**
     * Calculates dice roll range for the given children list.
     */
    private int getMaxDiceValue(List<T> possibleChildren, int maxChance) {
        int maxDiceValue = 0;
        for (T child : possibleChildren) {
            maxDiceValue += maxChance - child.getTier();
        }
        return maxDiceValue;
    }

    /**
     * Gets highest chance weight from the children list.
     */
    private int getMaxChance(List<T> possibleChildren) {
        int maxChance = 0;
        for (T child : possibleChildren) {
            maxChance = Math.max(maxChance, child.getTier());
        }
        return maxChance + 1;
    }

    /**
     * Returns true if at least one enabled item can spawn in this spawn type.
     *
     * @param spawnType The spawn type.
     * @return whether any item qualifies.
     */
    public boolean isAnyIn(SpawnType spawnType) {
        for (T t : items.values()) {
            if (t.canSpawn() && t.isEnabled() && t.getSpawnType() == spawnType) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns items that are explicitly disabled.
     *
     * @return a list of disabled registry items.
     */
    public Collection<T> getDisabledItems() {
        List<T> result = new ArrayList<>();
        for (T t : items.values()) {
            if (!t.isEnabled()) {
                result.add(t);
            }
        }
        return result;
    }
}
