package ruiseki.omoshiroikamo.module.chickens.registries;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;

public class BigReactorsChickens extends BaseChickenHandler {

    public static ChickensRegistryItem yelloriumChicken;
    public static ChickensRegistryItem cyaniteChicken;
    public static ChickensRegistryItem blutoniumChicken;
    public static ChickensRegistryItem ludicriteChicken;
    public static ChickensRegistryItem graphiteChicken;

    public BigReactorsChickens() {
        super("BigReactors", "Big Reactors", "textures/entity/chicken/bigreactors/");
        setStartID(800);
    }

    @Override
    public List<ChickensRegistryItem> registerChickens() {
        List<ChickensRegistryItem> allChickens = new ArrayList<>();

        yelloriumChicken = addChicken(
            "YelloriumChicken",
            this.nextID(),
            "YelloriumChicken",
            0xA5B700,
            0xD7EF00,
            SpawnType.NONE).setLayString("ore:ingotYellorium")
                .setLang("en_US", "Yellorium Chicken")
                .setLang("ja_JP", "イエロリウムのニワトリ");
        allChickens.add(yelloriumChicken);

        graphiteChicken = addChicken(
            "GraphiteChicken",
            this.nextID(),
            "GraphiteChicken",
            0x41453F,
            0x595959,
            SpawnType.NONE).setLayString("ore:ingotGraphite")
                .setLang("en_US", "Graphite Chicken")
                .setLang("ja_JP", "グラファイトのニワトリ");
        allChickens.add(graphiteChicken);

        cyaniteChicken = addChicken(
            "CyaniteChicken",
            this.nextID(),
            "CyaniteChicken",
            0x0068B4,
            0x5CAFDB,
            SpawnType.NONE).setLayString("ore:ingotCyanite")
                .setLang("en_US", "Cyanite Chicken")
                .setLang("ja_JP", "シアナイトのニワトリ");
        allChickens.add(cyaniteChicken);

        blutoniumChicken = addChicken(
            "BlutoniumChicken",
            this.nextID(),
            "BlutoniumChicken",
            0x4642D6,
            0xf5fcf1,
            SpawnType.NONE).setLayString("ore:ingotBlutonium")
                .setLang("en_US", "Blutonium Chicken")
                .setLang("ja_JP", "ブルトニウムのニワトリ");
        allChickens.add(blutoniumChicken);

        ludicriteChicken = addChicken(
            "LudicriteChicken",
            this.nextID(),
            "LudicriteChicken",
            0xC63BE5,
            0xF27CFF,
            SpawnType.NONE).setLayString("ore:ingotLudicrite")
                .setLang("en_US", "Ludicrite Chicken")
                .setLang("ja_JP", "ルディクライトのニワトリ");
        allChickens.add(ludicriteChicken);

        return allChickens;
    }

    @Override
    public void registerAllParents(List<ChickensRegistryItem> allChickens) {
        setParents(yelloriumChicken, BaseChickens.glowstoneChicken, BaseChickens.enderChicken);

        setParents(graphiteChicken, BaseChickens.coalChicken, BaseChickens.blackChicken);

        setParents(cyaniteChicken, yelloriumChicken, BaseChickens.sandChicken);

        setParents(blutoniumChicken, cyaniteChicken, BaseChickens.waterChicken);

        setParents(ludicriteChicken, blutoniumChicken, BaseChickens.magmaChicken);

    }
}
