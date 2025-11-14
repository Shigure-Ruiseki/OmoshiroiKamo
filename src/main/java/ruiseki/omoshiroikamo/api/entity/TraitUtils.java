package ruiseki.omoshiroikamo.api.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TraitUtils {

    private static final int MAX_LEVEL = 10;

    // Add / Merge
    public static void addTrait(Map<MobTrait, Integer> traits, MobTrait trait) {
        addTraits(traits, trait, 1);
    }

    public static void addTraits(Map<MobTrait, Integer> traits, MobTrait trait, int newLevel) {
        if (trait == null) {
            return;
        }

        Integer oldLevel = traits.get(trait);

        if (oldLevel == null) {
            traits.put(trait, Math.min(newLevel, MAX_LEVEL));
            return;
        }

        if (oldLevel.equals(newLevel)) {
            traits.put(trait, Math.min(oldLevel + 1, MAX_LEVEL));
        } else {
            traits.put(trait, Math.min(Math.max(oldLevel, newLevel), MAX_LEVEL));
        }
    }

    // Remove / Check
    public static void removeTrait(Map<MobTrait, Integer> traits, MobTrait trait) {
        if (trait != null) {
            traits.remove(trait);
        }
    }

    public static boolean hasTrait(Map<MobTrait, Integer> traits, MobTrait trait) {
        return trait != null && traits.containsKey(trait);
    }

    public static int getTraitLevel(Map<MobTrait, Integer> traits, MobTrait trait) {
        return traits.getOrDefault(trait, 0);
    }

    // Get Stats
    public static int getTotalGainBonus(Map<MobTrait, Integer> traits) {
        return traits.entrySet()
            .stream()
            .mapToInt(
                e -> e.getKey()
                    .getGainBonus() * e.getValue())
            .sum();
    }

    public static int getTotalStrengthBonus(Map<MobTrait, Integer> traits) {
        return traits.entrySet()
            .stream()
            .mapToInt(
                e -> e.getKey()
                    .getStrengthBonus() * e.getValue())
            .sum();
    }

    public static int getTotalGrowthBonus(Map<MobTrait, Integer> traits) {
        return traits.entrySet()
            .stream()
            .mapToInt(
                e -> e.getKey()
                    .getGrowthBonus() * e.getValue())
            .sum();
    }

    // Merge
    public static void mergeTraits(Map<MobTrait, Integer> target, Map<MobTrait, Integer> source) {
        for (Map.Entry<MobTrait, Integer> entry : source.entrySet()) {
            addTraits(target, entry.getKey(), entry.getValue());
        }
    }

    // Display
    public static String formatTrait(Map<MobTrait, Integer> traits, MobTrait trait) {
        int level = getTraitLevel(traits, trait);
        return trait.getName() + " Lv" + level;
    }

    public static List<String> formatAllTraits(Map<MobTrait, Integer> traits) {
        List<String> result = new ArrayList<>();
        for (MobTrait trait : traits.keySet()) {
            result.add(formatTrait(traits, trait));
        }
        return result;
    }
}
