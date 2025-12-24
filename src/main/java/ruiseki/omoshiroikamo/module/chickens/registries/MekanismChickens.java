package ruiseki.omoshiroikamo.module.chickens.registries;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;

public class MekanismChickens extends BaseChickenHandler {

    public static ChickensRegistryItem osmiumChicken;

    public MekanismChickens() {
        super("Mekanism", "Mekanism", "textures/entity/chicken/mekanism/");
        this.setStartID(700);
    }

    @Override
    public List<ChickensRegistryItem> registerChickens() {
        List<ChickensRegistryItem> allChickens = new ArrayList<>();

        osmiumChicken = addChicken("OsmiumChicken", this.nextID(), "OsmiumChicken", 0x989585, 0xd1ccb6, SpawnType.NONE)
            .setLang("en_US", "Osmium Chicken")
            .setLang("ja_JP", "オスミウムのニワトリ")
            .setLayString("ore:ingotOsmium");
        allChickens.add(osmiumChicken);

        return allChickens;
    }

    @Override
    public void registerAllParents(List<ChickensRegistryItem> allChickens) {
        setParents(osmiumChicken, BaseChickens.ironChicken, BaseChickens.quartzChicken);
    }
}
