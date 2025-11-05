package ruiseki.omoshiroikamo.plugin.chicken;

import java.util.List;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;

public class MekanismChickens extends BaseChickenHandler {

    public static ChickensRegistryItem osmiumChicken = null;

    public MekanismChickens() {
        super("Mekanism", "Mekanism", "textures/entity/chicken/mekanism/");
        this.setStartID(700);
    }

    @Override
    public List<ChickensRegistryItem> registerChickens(List<ChickensRegistryItem> allChickens) {

        osmiumChicken = addChicken(
            allChickens,
            "OsmiumChicken",
            this.nextID(),
            "OsmiumChicken.png",
            this.getFirstOreDictionary("ingotOsmium"),
            0x989585,
            0xd1ccb6,
            SpawnType.NONE);

        return allChickens;
    }

    @Override
    public void RegisterAllParents(List<ChickensRegistryItem> allChickens) {
        setParents(osmiumChicken, BaseChickens.ironChicken, BaseChickens.quartzChicken);
    }
}
