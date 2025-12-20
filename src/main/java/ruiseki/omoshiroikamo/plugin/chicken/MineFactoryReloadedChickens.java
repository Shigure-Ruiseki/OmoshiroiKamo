package ruiseki.omoshiroikamo.plugin.chicken;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;

public class MineFactoryReloadedChickens extends BaseChickenHandler {

    public static ChickensRegistryItem pinkSlimeChicken;

    public MineFactoryReloadedChickens() {
        super("MineFactoryReloaded", "MineFactory Reloaded", "textures/entity/chicken/minefactoryreloaded/");
        this.setStartID(600);
    }

    @Override
    public List<ChickensRegistryItem> registerChickens() {
        List<ChickensRegistryItem> allChickens = new ArrayList<>();

        pinkSlimeChicken = addChicken(
            "PinkSlimeChicken",
            this.nextID(),
            "PinkSlimeChicken",
            0xC8738A,
            0x804954,
            SpawnType.NONE).setLang("en_US", "Pink Slime Chicken")
                .setLang("ja_JP", "ピンクスライムのニワトリ")
                .setLayString("ore:slimeballPink");
        allChickens.add(pinkSlimeChicken);

        return allChickens;
    }

    @Override
    public void registerAllParents(List<ChickensRegistryItem> allChickens) {
        setParents(pinkSlimeChicken, BaseChickens.pinkChicken, BaseChickens.slimeChicken);
    }
}
