package ruiseki.omoshiroikamo.api.entity;

import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;

public interface IMobStats {

    String TYPE_NBT = "Type";
    String STATS_ANALYZED_NBT = "Analyzed";
    String GROWTH_NBT = "Growth";
    String GAIN_NBT = "Gain";
    String STRENGTH_NBT = "Strength";

    int getType();

    void setType(int type);

    boolean getStatsAnalyzed();

    void setStatsAnalyzed(boolean val);

    int getGrowth();

    void setGrowth(int growth);

    int getGain();

    void setGain(int gain);

    int getStrength();

    void setStrength(int strength);

    default void increaseStats(IMobStats child, IMobStats p1, IMobStats p2, Random rand) {
        int p1Strength = p1.getStrength();
        int p2Strength = p2.getStrength();
        child.setGrowth(calculateNewStat(p1Strength, p2Strength, p1.getGrowth(), p2.getGrowth(), rand));
        child.setGain(calculateNewStat(p1Strength, p2Strength, p2.getGain(), p2.getGain(), rand));
        child.setStrength(calculateNewStat(p1Strength, p2Strength, p1Strength, p2Strength, rand));
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
        tagCompound.setBoolean(STATS_ANALYZED_NBT, getStatsAnalyzed());
        tagCompound.setInteger(GROWTH_NBT, getGrowth());
        tagCompound.setInteger(GAIN_NBT, getGain());
        tagCompound.setInteger(STRENGTH_NBT, getStrength());
    }

    default void readStatsNBT(NBTTagCompound tagCompound) {
        setType(tagCompound.getInteger(TYPE_NBT));
        setStatsAnalyzed(tagCompound.getBoolean(STATS_ANALYZED_NBT));
        setGrowth(getStatusValue(tagCompound, GROWTH_NBT));
        setGain(getStatusValue(tagCompound, GAIN_NBT));
        setStrength(getStatusValue(tagCompound, STRENGTH_NBT));
    }

    default int getStatusValue(NBTTagCompound compound, String statusName) {
        return compound.hasKey(statusName) ? compound.getInteger(statusName) : 1;
    }
}
