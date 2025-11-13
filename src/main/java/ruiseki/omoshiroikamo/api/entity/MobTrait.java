package ruiseki.omoshiroikamo.api.entity;

public class MobTrait {

    private final String id;
    private final String name;
    private final int gainBonus;
    private final int strengthBonus;
    private final int growthBonus;
    private final int rarity;

    public MobTrait(String id, String name, int gainBonus, int strengthBonus, int growthBonus, int rarity) {
        this.id = id;
        this.name = name;
        this.gainBonus = gainBonus;
        this.strengthBonus = strengthBonus;
        this.growthBonus = growthBonus;
        this.rarity = rarity;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getGainBonus() {
        return gainBonus;
    }

    public int getStrengthBonus() {
        return strengthBonus;
    }

    public int getGrowthBonus() {
        return growthBonus;
    }

    public int getRarity() {
        return rarity;
    }
}
