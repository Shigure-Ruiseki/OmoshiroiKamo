package ruiseki.omoshiroikamo.module.chickens.registries;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;

public class BotaniaChickens extends BaseChickenHandler {

    public static ChickensRegistryItem elementiumChicken;
    public static ChickensRegistryItem manasteelChicken;
    public static ChickensRegistryItem terrasteelChicken;

    public BotaniaChickens() {
        super("Botania", "Botania", "textures/entity/chicken/botania/");
        this.setStartID(100);
    }

    @Override
    public List<ChickensRegistryItem> registerChickens() {
        List<ChickensRegistryItem> allChickens = new ArrayList<>();

        terrasteelChicken = addChicken(
            "TerrasteelChicken",
            this.nextID(),
            "TerrasteelChicken",
            0x3ff123,
            0xf5fcf1,
            SpawnType.NONE).setLayString("ore:ingotTerrasteel");
        allChickens.add(terrasteelChicken);

        manasteelChicken = addChicken(
            "ManasteelChicken",
            this.nextID(),
            "ManasteelChicken",
            0x69d7ff,
            0x002c4b,
            SpawnType.NONE).setLayString("ore:ingotManasteel");
        allChickens.add(manasteelChicken);

        elementiumChicken = addChicken(
            "ElementiumChicken",
            this.nextID(),
            "ElementiumChicken",
            0xf655f3,
            0xb407b7,
            SpawnType.NONE).setLayString("ore:nuggetElvenElementium");
        allChickens.add(elementiumChicken);

        return allChickens;
    }

    @Override
    public void registerAllParents(List<ChickensRegistryItem> allChickens) {
        setParents(manasteelChicken, BaseChickens.ironChicken, BaseChickens.ghastChicken);
        setParents(terrasteelChicken, BaseChickens.enderChicken, manasteelChicken);
        setParents(elementiumChicken, manasteelChicken, terrasteelChicken);
    }
}
