package ruiseki.omoshiroikamo.plugin.chicken;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.common.util.lib.LibMods;

public class DraconicEvolutionChickens extends BaseChickenHandler {

    public static ChickensRegistryItem draconiumChicken;
    public static ChickensRegistryItem draconiumAwakenedChicken;

    public DraconicEvolutionChickens() {
        super("DraconicEvolution", "Draconic Evolution", "textures/entity/draconic/");
        this.setStartID(900);
    }

    @Override
    public List<ChickensRegistryItem> registerChickens() {
        List<ChickensRegistryItem> allChickens = new ArrayList<>();

        draconiumChicken = addChicken(
            "DraconiumChicken",
            this.nextID(),
            "DraconiumChicken",
            0x301549,
            0x1a0c27,
            SpawnType.NONE).setLayString("ore:ingotDraconium")
                .setLang("en_US", "Draconium Chicken")
                .setLang("ja_JP", "ドラコニウムのニワトリ");
        allChickens.add(draconiumChicken);

        draconiumAwakenedChicken = addChicken(
            "DraconiumAwakenedChicken",
            this.nextID(),
            "DraconiumAwakenedChicken",
            0xcc440c,
            0x9c691a,
            SpawnType.NONE).setLayString("ore:nuggetDraconiumAwakened")
                .setLang("en_US", "Awakened Draconium Chicken")
                .setLang("ja_JP", "覚醒ドラコニウムのニワトリ");
        allChickens.add(draconiumAwakenedChicken);

        return allChickens;
    }

    @Override
    public void registerAllParents(List<ChickensRegistryItem> allChickens) {
        if (LibMods.TConstruct.isLoaded()) {
            setParents(draconiumChicken, TinkersChickens.pigIronChicken, BaseChickens.enderChicken);
        } else {
            setParents(draconiumChicken, BaseChickens.gunpowderChicken, BaseChickens.enderChicken);
        }

        setParents(draconiumAwakenedChicken, draconiumChicken, BaseChickens.enderChicken);

    }
}
