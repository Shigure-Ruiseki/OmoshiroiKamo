package ruiseki.omoshiroikamo.plugin.chicken;

import java.util.List;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;

public class BotaniaChickens extends BaseChickenHandler {

    public static ChickensRegistryItem elementiumChicken = null;
    public static ChickensRegistryItem manasteelChicken = null;
    public static ChickensRegistryItem terrasteelChicken = null;

    public BotaniaChickens() {
        super("Botania", "Botania", "textures/entity/chicken/botania/");
        this.setStartID(100);
    }

    @Override
    public List<ChickensRegistryItem> registerChickens(List<ChickensRegistryItem> allChickens) {
        terrasteelChicken = addChicken(
            allChickens,
            "TerrasteelChicken",
            this.nextID(),
            "TerrasteelChicken.png",
            this.getFirstOreDictionary("ingotTerrasteel"),
            0x3ff123,
            0xf5fcf1,
            SpawnType.NONE);

        manasteelChicken = addChicken(
            allChickens,
            "ManasteelChicken",
            this.nextID(),
            "ManasteelChicken.png",
            this.getFirstOreDictionary("ingotManasteel"),
            0x69d7ff,
            0x002c4b,
            SpawnType.NONE);

        elementiumChicken = addChicken(
            allChickens,
            "ElementiumChicken",
            this.nextID(),
            "ElementiumChicken.png",
            this.getFirstOreDictionary("nuggetElvenElementium"),
            0xf655f3,
            0xb407b7,
            SpawnType.NONE);

        return allChickens;
    }

    @Override
    public void RegisterAllParents(List<ChickensRegistryItem> allChickens) {
        setParents(manasteelChicken, BaseChickens.ironChicken, BaseChickens.ghastChicken);
        setParents(terrasteelChicken, BaseChickens.enderChicken, manasteelChicken);
        setParents(elementiumChicken, manasteelChicken, terrasteelChicken);
    }
}
