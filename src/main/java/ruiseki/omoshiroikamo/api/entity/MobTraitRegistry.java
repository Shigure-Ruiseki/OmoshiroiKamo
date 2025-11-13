package ruiseki.omoshiroikamo.api.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MobTraitRegistry {

    private static final Map<String, MobTrait> TRAITS = new HashMap<>();
    static Random rand = new Random();

    public static final MobTrait FAST_GROWTH = register(new MobTrait("fast_growth", "Fast Growth", 0, 0, 3, 1));
    public static final MobTrait STRONG_GENE = register(new MobTrait("strong_gene", "Strong Gene", 0, 3, 0, 2));
    public static final MobTrait HIGH_GAIN = register(new MobTrait("high_gain", "High Gain", 3, 0, 0, 3));

    private static MobTrait register(MobTrait trait) {
        TRAITS.put(trait.getId(), trait);
        return trait;
    }

    public static MobTrait getTraitById(String id) {
        return TRAITS.get(id);
    }

    public static Collection<MobTrait> getAllTraits() {
        return TRAITS.values();
    }

    public static MobTrait getRandomTrait() {
        if (TRAITS.isEmpty()) {
            return null;
        }

        List<MobTrait> list = new ArrayList<>(TRAITS.values());

        int totalWeight = 0;
        Map<MobTrait, Integer> weights = new HashMap<>();
        for (MobTrait trait : list) {
            int weight = 4 - trait.getRarity();
            weights.put(trait, weight);
            totalWeight += weight;
        }

        int random = rand.nextInt(totalWeight);

        int sum = 0;
        for (MobTrait trait : list) {
            sum += weights.get(trait);
            if (random < sum) {
                return trait;
            }
        }
        return list.get(rand.nextInt(list.size()));
    }

}
