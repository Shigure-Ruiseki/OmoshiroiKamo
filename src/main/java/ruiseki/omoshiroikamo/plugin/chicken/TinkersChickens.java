package ruiseki.omoshiroikamo.plugin.chicken;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import tconstruct.world.TinkerWorld;

public class TinkersChickens extends BaseChickenHandler {

    public static ChickensRegistryItem arditeChicken;
    public static ChickensRegistryItem colbaltChicken;
    public static ChickensRegistryItem manyullynChicken;
    public static ChickensRegistryItem pigIronChicken;
    public static ChickensRegistryItem bloodSlimeChicken;
    public static ChickensRegistryItem blueSlimeChicken;

    public TinkersChickens() {
        super("TConstruct", "Tinkers Construct", "textures/entity/chicken/tinkers/");
        this.setStartID(500);
    }

    @Override
    public List<ChickensRegistryItem> registerChickens() {
        List<ChickensRegistryItem> allChickens = new ArrayList<>();

        arditeChicken = addChicken("ArditeChicken", nextID(), "ArditeChicken", 0xdc3e00, 0xffb20b, SpawnType.NONE)
            .setLayString("ore:ingotArdite")
            .setLang("en_US", "Ardite Chicken")
            .setLang("ja_JP", "アルダイトのニワトリ");
        allChickens.add(arditeChicken);

        colbaltChicken = addChicken("CobaltChicken", nextID(), "CobaltChicken", 0x0c5abe, 0x03d94f1, SpawnType.NONE)
            .setLayString("ore:ingotCobalt")
            .setLang("en_US", "Cobalt Chicken")
            .setLang("ja_JP", "コバルトのニワトリ");
        allChickens.add(colbaltChicken);

        manyullynChicken = addChicken(
            "ManyullynChicken",
            nextID(),
            "ManyullynChicken",
            0x652e87,
            0xbc8fe7,
            SpawnType.NONE).setLayString("ore:ingotManyullyn")
                .setLang("en_US", "Manyullyn Chicken")
                .setLang("ja_JP", "マニュリンのニワトリ");
        allChickens.add(manyullynChicken);

        pigIronChicken = addChicken("PigIronChicken", nextID(), "PigIronChicken", 0xe6b8b8, 0xdba9a9, SpawnType.NONE)
            .setLayString("ore:ingotPigIron")
            .setLang("en_US", "Pig Iron Chicken")
            .setLang("ja_JP", "ピッグアイアンのニワトリ");
        allChickens.add(pigIronChicken);

        bloodSlimeChicken = addChicken(
            "BloodSlimeChicken",
            nextID(),
            "BloodSlimeChicken",
            0xc50616,
            0xee0316,
            SpawnType.NONE).setLayItem(new ItemStack(TinkerWorld.strangeFood, 1, 1))
                .setLang("en_US", "Blood Slime Chicken")
                .setLang("ja_JP", "ブラッドスライムのニワトリ");
        allChickens.add(bloodSlimeChicken);

        blueSlimeChicken = addChicken(
            "BlueSlimeChicken",
            nextID(),
            "BlueSlimeChicken",
            0x67b4c4,
            0x30717f,
            SpawnType.NONE).setLayItem(new ItemStack(TinkerWorld.strangeFood, 1, 0))
                .setLang("en_US", "Blue Slime Chicken")
                .setLang("ja_JP", "ブルースライムのニワトリ");
        allChickens.add(blueSlimeChicken);

        return allChickens;
    }

    @Override
    public void registerAllParents(List<ChickensRegistryItem> allChickens) {
        setParents(arditeChicken, BaseChickens.blazeChicken, BaseChickens.magmaChicken);
        setParents(colbaltChicken, BaseChickens.netherwartChicken, BaseChickens.ghastChicken);
        setParents(manyullynChicken, arditeChicken, colbaltChicken);
        setParents(pigIronChicken, arditeChicken, BaseChickens.ironChicken);
        setParents(bloodSlimeChicken, BaseChickens.slimeChicken, BaseChickens.redChicken);
        setParents(blueSlimeChicken, BaseChickens.slimeChicken, BaseChickens.blueChicken);
    }
}
