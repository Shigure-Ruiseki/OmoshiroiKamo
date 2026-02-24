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

        osmiumChicken = addChicken("OsmiumChicken", this.nextID(), "OsmiumChicken", 0x0d1bb7, 0xadb4d6, SpawnType.NONE)
            .setLayString("ore:ingotOsmium");
        allChickens.add(osmiumChicken);

        return allChickens;
    }

    @Override
    public void registerAllParents(List<ChickensRegistryItem> allChickens) {
        setParents(osmiumChicken, BaseChickens.ironChicken, BaseChickens.quartzChicken);
    }
}
