package ruiseki.omoshiroikamo.api.entity;

import java.util.Map;
import java.util.Random;

import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;

public interface IMobStats {

    String TYPE_NBT = "Type";
    String ANALYZED_NBT = "Analyzed";
    String GROWTH_NBT = "Growth";
    String GAIN_NBT = "Gain";
    String STRENGTH_NBT = "Strength";
    String TRAITS_NBT = "Traits";

    int getType();

    void setType(int type);

    boolean getStatsAnalyzed();

    void setStatsAnalyzed(boolean val);

    int getBaseGrowth();

    void setBaseGrowth(int growth);

    int getBaseGain();

    void setBaseGain(int gain);

    int getBaseStrength();

    void setBaseStrength(int strength);

    Map<MobTrait, Integer> getTraits();

    default int getMaxGrowthStat() {
        return 10;
    }

    default int getMaxGainStat() {
        return 10;
    }

    default int getMaxStrengthStat() {
        return 10;
    }

    // -------------------- Trait utils --------------------

    default boolean hasTrait(MobTrait trait) {
        return TraitUtils.hasTrait(getTraits(), trait);
    }

    default void addTrait(MobTrait trait) {
        TraitUtils.addTrait(getTraits(), trait);
    }

    default void addTrait(MobTrait trait, int level) {
        TraitUtils.addTraits(getTraits(), trait, level);
    }

    default void removeTrait(MobTrait trait) {
        TraitUtils.removeTrait(getTraits(), trait);
    }

    default int getTraitGainBonus() {
        return TraitUtils.getTotalGainBonus(getTraits());
    }

    default int getTraitStrengthBonus() {
        return TraitUtils.getTotalStrengthBonus(getTraits());
    }

    default int getTraitGrowthBonus() {
        return TraitUtils.getTotalGrowthBonus(getTraits());
    }

    // -------------------- Stat getter --------------------

    default int getGrowth() {
        return getBaseGrowth() + getTraitGrowthBonus();
    }

    default int getGain() {
        return getBaseGain() + getTraitGainBonus();
    }

    default int getStrength() {
        return getBaseStrength() + getTraitStrengthBonus();
    }

    default void addRandomTraits() {
        Map<MobTrait, Integer> traits = getTraits();
        if (!traits.isEmpty()) {
            return;
        }

        Random rand = ((EntityLiving) this).getRNG();

        if (rand.nextFloat() < 0.1f) {
            int count = 1 + rand.nextInt(2);
            for (int i = 0; i < count; i++) {
                MobTrait t = MobTraitRegistry.getRandomTrait();
                addTrait(t, 1);
            }
        }
    }

    default void increaseStats(IMobStats child, IMobStats p1, IMobStats p2, Random rand) {
        int p1Strength = p1.getBaseStrength();
        int p2Strength = p2.getBaseStrength();
        child.setBaseGrowth(
                calculateNewStat(p1Strength, p2Strength, p1.getBaseGrowth(), p2.getBaseGrowth(), rand,
                        child.getMaxGrowthStat()));
        child.setBaseGain(
                calculateNewStat(p1Strength, p2Strength, p1.getBaseGain(), p2.getBaseGain(), rand,
                        child.getMaxGainStat()));
        child.setBaseStrength(
                calculateNewStat(p1Strength, p2Strength, p1Strength, p2Strength, rand, child.getMaxStrengthStat()));
    }

    default void mutationTrait(IMobStats child, IMobStats p1, IMobStats p2, Random rand) {

        for (Map.Entry<MobTrait, Integer> entry : p1.getTraits()
                .entrySet()) {
            if (rand.nextFloat() < 0.25f) {
                child.addTrait(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<MobTrait, Integer> entry : p2.getTraits()
                .entrySet()) {
            if (rand.nextFloat() < 0.25f) {
                child.addTrait(entry.getKey(), entry.getValue());
            }
        }

        // random mutation
        if (rand.nextFloat() < 0.10f) {
            MobTrait t = MobTraitRegistry.getRandomTrait();
            child.addTrait(t, 1);
        }
    }

    default int calculateNewStat(int p1Strength, int p2Strength, int stat1, int stat2, Random rand, int maxStatValue) {
        int denominator = Math.max(1, p1Strength + p2Strength);
        int weightedAverage = (stat1 * p1Strength + stat2 * p2Strength) / denominator;
        int mutation = rand.nextInt(2) + 1;

        int targetValue = weightedAverage + mutation;
        int currentStat = Math.max(stat1, stat2);
        int maxValue = Math.max(1, maxStatValue);

        if (targetValue > currentStat) {
            int desiredIncrease = targetValue - currentStat;
            float modifier = getDiminishingReturnsModifier(currentStat, desiredIncrease, maxValue);

            int adjustedIncrease = Math.max(1, Math.round(desiredIncrease * modifier));
            if (rand.nextFloat() <= modifier) {
                targetValue = currentStat + adjustedIncrease;
            } else {
                targetValue = currentStat;
            }
        }

        if (targetValue <= 1) {
            return 1;
        }

        return Math.min(targetValue, maxValue);
    }

    default float getDiminishingReturnsModifier(int currentStat, int desiredIncrease, int maxStatValue) {
        int current = Math.max(1, currentStat);
        int cappedIncrease = Math.max(0, desiredIncrease);
        int effectiveStat = Math.max(1,
                Math.min(current + cappedIncrease, Math.max(1, maxStatValue)));
        float modifier = 1.0f;
        long threshold = 10;

        while (effectiveStat > threshold) {
            modifier *= 0.8f;
            if (threshold >= Integer.MAX_VALUE / 2) {
                break;
            }
            threshold *= 2;
        }

        return Math.max(0.0f, modifier);
    }

    default int setStatsGrowingAge(int age) {
        int childAge = -24000;
        boolean resetToChild = age == childAge;
        int maxGrowth = Math.max(1, getMaxGrowthStat());
        int clampedGrowth = Math.max(1, Math.min(getGrowth(), maxGrowth));
        int invertedGrowth = maxGrowth - clampedGrowth + 1;
        if (resetToChild) {
            age = Math.min(-1, (childAge * invertedGrowth) / maxGrowth);
        }

        int loveAge = 6000;
        boolean resetLoveAfterBreeding = age == loveAge;
        if (resetLoveAfterBreeding) {
            age = Math.max(1, (loveAge * invertedGrowth) / maxGrowth);
        }
        return age;
    }

    default void writeStatsNBT(NBTTagCompound tag) {

        tag.setInteger(TYPE_NBT, getType());
        tag.setBoolean(ANALYZED_NBT, getStatsAnalyzed());
        tag.setInteger(GROWTH_NBT, getBaseGrowth());
        tag.setInteger(GAIN_NBT, getBaseGain());
        tag.setInteger(STRENGTH_NBT, getBaseStrength());

        TraitUtils.writeTraitsNBT(getTraits(), tag);
    }

    default void readStatsNBT(NBTTagCompound tag) {

        setType(tag.getInteger(TYPE_NBT));
        setStatsAnalyzed(tag.getBoolean(ANALYZED_NBT));
        setBaseGrowth(getStatusValue(tag, GROWTH_NBT));
        setBaseGain(getStatusValue(tag, GAIN_NBT));
        setBaseStrength(getStatusValue(tag, STRENGTH_NBT));

        TraitUtils.readTraitsFromNBT(getTraits(), tag);
    }

    default int getStatusValue(NBTTagCompound compound, String statusName) {
        return compound.hasKey(statusName) ? compound.getInteger(statusName) : 1;
    }
}
