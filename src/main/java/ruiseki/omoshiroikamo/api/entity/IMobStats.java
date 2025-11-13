package ruiseki.omoshiroikamo.api.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

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

    List<MobTrait> getTraits();

    // -------------------- Trait utils --------------------

    default boolean hasTrait(MobTrait trait) {
        return trait != null && getTraits().contains(trait);
    }

    default void addTrait(MobTrait trait) {
        if (trait == null) {
            return;
        }
        List<MobTrait> list = getTraits();
        if (list != null && !list.contains(trait)) {
            list.add(trait);
        }
    }

    default void removeTrait(MobTrait trait) {
        if (trait == null) {
            return;
        }
        List<MobTrait> list = getTraits();
        if (list != null) {
            list.remove(trait);
        }
    }

    default int getTraitGainBonus() {
        List<MobTrait> traits = getTraits();
        if (traits == null) {
            return 0;
        }
        return traits.stream()
            .mapToInt(MobTrait::getGainBonus)
            .sum();
    }

    default int getTraitStrengthBonus() {
        List<MobTrait> traits = getTraits();
        if (traits == null) {
            return 0;
        }
        return traits.stream()
            .mapToInt(MobTrait::getStrengthBonus)
            .sum();
    }

    default int getTraitGrowthBonus() {
        List<MobTrait> traits = getTraits();
        if (traits == null) {
            return 0;
        }
        return traits.stream()
            .mapToInt(MobTrait::getGrowthBonus)
            .sum();
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
        List<MobTrait> traits = getTraits();
        if (traits == null || !traits.isEmpty()) {
            return;
        }

        Random rand = ((EntityLiving) this).getRNG();
        if (rand.nextFloat() < 0.1f) {
            int count = 1 + rand.nextInt(2);
            for (int i = 0; i < count; i++) {
                MobTrait randomTrait = MobTraitRegistry.getRandomTrait();
                addTrait(randomTrait);
            }
        }
    }

    default void increaseStats(IMobStats child, IMobStats p1, IMobStats p2, Random rand) {
        int p1Strength = p1.getBaseStrength();
        int p2Strength = p2.getBaseStrength();
        child.setBaseGrowth(calculateNewStat(p1Strength, p2Strength, p1.getBaseGrowth(), p2.getBaseGrowth(), rand));
        child.setBaseGain(calculateNewStat(p1Strength, p2Strength, p2.getBaseGain(), p2.getBaseGain(), rand));
        child.setBaseStrength(calculateNewStat(p1Strength, p2Strength, p1Strength, p2Strength, rand));

        List<MobTrait> parentTraits = new ArrayList<>();
        if (p1.getTraits() != null) {
            parentTraits.addAll(p1.getTraits());
        }
        if (p2.getTraits() != null) {
            parentTraits.addAll(p2.getTraits());
        }

        for (MobTrait trait : parentTraits) {
            if (rand.nextFloat() < 0.25f) {
                child.addTrait(trait);
            }
        }

        if (rand.nextFloat() < 0.10f) {
            MobTrait newTrait = MobTraitRegistry.getRandomTrait();
            if (newTrait != null) {
                child.addTrait(newTrait);
            }
        }
    }

    default int calculateNewStat(int p1Strength, int p2Strength, int stat1, int stat2, Random rand) {
        int mutation = rand.nextInt(2) + 1;
        int newStatValue = (stat1 * p1Strength + stat2 * p2Strength) / (p1Strength + p2Strength) + mutation;
        if (newStatValue <= 1) {
            return 1;
        }
        return Math.min(newStatValue, 10);
    }

    default int setStatsGrowingAge(int age) {
        int childAge = -24000;
        boolean resetToChild = age == childAge;
        if (resetToChild) {
            age = Math.min(-1, (childAge * (10 - getGrowth() + 1)) / 10);
        }

        int loveAge = 6000;
        boolean resetLoveAfterBreeding = age == loveAge;
        if (resetLoveAfterBreeding) {
            age = Math.max(1, (loveAge * (10 - getGrowth() + 1)) / 10);
        }
        return age;
    }

    default void writeStatsNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger(TYPE_NBT, getType());
        tagCompound.setBoolean(ANALYZED_NBT, getStatsAnalyzed());
        tagCompound.setInteger(GROWTH_NBT, getBaseGrowth());
        tagCompound.setInteger(GAIN_NBT, getBaseGain());
        tagCompound.setInteger(STRENGTH_NBT, getBaseStrength());

        List<MobTrait> traits = getTraits();
        if (traits != null) {
            NBTTagList traitsList = new NBTTagList();
            for (MobTrait trait : getTraits()) {
                traitsList.appendTag(new NBTTagString(trait.getId()));
            }
            tagCompound.setTag(TRAITS_NBT, traitsList);
        }
    }

    default void readStatsNBT(NBTTagCompound tagCompound) {
        setType(tagCompound.getInteger(TYPE_NBT));
        setStatsAnalyzed(tagCompound.getBoolean(ANALYZED_NBT));
        setBaseGrowth(getStatusValue(tagCompound, GROWTH_NBT));
        setBaseGain(getStatusValue(tagCompound, GAIN_NBT));
        setBaseStrength(getStatusValue(tagCompound, STRENGTH_NBT));

        if (tagCompound.hasKey(TRAITS_NBT)) {
            NBTTagList traitsList = tagCompound.getTagList(TRAITS_NBT, 8);
            for (int i = 0; i < traitsList.tagCount(); i++) {
                String id = traitsList.getStringTagAt(i);
                MobTrait trait = MobTraitRegistry.getTraitById(id);
                if (trait != null) {
                    addTrait(trait);
                }
            }
        }
    }

    default int getStatusValue(NBTTagCompound compound, String statusName) {
        return compound.hasKey(statusName) ? compound.getInteger(statusName) : 1;
    }
}
