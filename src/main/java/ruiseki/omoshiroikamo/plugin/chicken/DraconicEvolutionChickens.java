package ruiseki.omoshiroikamo.plugin.chicken;

import java.util.List;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.common.util.lib.LibMods;

public class DraconicEvolutionChickens extends BaseChickenHandler {

    public static ChickensRegistryItem draconiumChicken = null;
    public static ChickensRegistryItem draconiumAwakenedChicken = null;

    public DraconicEvolutionChickens() {
        super("DraconicEvolution", "Draconic Evolution", "textures/entity/draconic/");
        this.setStartID(900);
    }

    @Override
    public List<ChickensRegistryItem> registerChickens(List<ChickensRegistryItem> allChickens) {
        draconiumChicken = addChicken(
            allChickens,
            "DraconiumChicken",
            this.nextID(),
            "DraconiumChicken.png",
            this.getFirstOreDictionary("ingotDraconium"),
            0x301549,
            0x1a0c27,
            SpawnType.NONE);

        draconiumAwakenedChicken = addChicken(
            allChickens,
            "DraconiumAwakenedChicken",
            this.nextID(),
            "DraconiumAwakenedChicken.png",
            this.getFirstOreDictionary("nuggetDraconiumAwakened"),
            0xcc440c,
            0x9c691a,
            SpawnType.NONE);

        return allChickens;
    }

    @Override
    public void RegisterAllParents(List<ChickensRegistryItem> allChickens) {
        if (LibMods.TConstruct.isLoaded()) {
            setParents(draconiumChicken, TinkersChickens.pigIronChicken, BaseChickens.enderChicken);
        } else {
            setParents(draconiumChicken, BaseChickens.gunpowderChicken, BaseChickens.enderChicken);
        }

        setParents(draconiumAwakenedChicken, draconiumChicken, BaseChickens.enderChicken);

    }
}
