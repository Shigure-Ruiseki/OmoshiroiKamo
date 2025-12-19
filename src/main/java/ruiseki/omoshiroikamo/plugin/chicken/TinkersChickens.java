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

        arditeChicken = addChicken(
            "ArditeChicken",
            this.nextID(),
            "ArditeChicken.png",
            0xdc3e00,
            0xffb20b,
            SpawnType.NONE,
            new String[] { "en_US:Ardite Chicken", "ja_JP:アルダイトのニワトリ" }).setLayString("ore:ingotArdite");
        allChickens.add(arditeChicken);

        colbaltChicken = addChicken(
            "CobaltChicken",
            this.nextID(),
            "CobaltChicken.png",
            0x0c5abe,
            0x03d94f1,
            SpawnType.NONE,
            new String[] { "en_US:Cobalt Chicken", "ja_JP:コバルトのニワトリ" }).setLayString("ore:ingotCobalt");
        allChickens.add(colbaltChicken);

        manyullynChicken = addChicken(
            "ManyullynChicken",
            this.nextID(),
            "ManyullynChicken.png",
            0x652e87,
            0xbc8fe7,
            SpawnType.NONE,
            new String[] { "en_US:Manyullyn Chicken", "ja_JP:マニュリンのニワトリ" }).setLayString("ore:ingotManyullyn");
        allChickens.add(manyullynChicken);

        pigIronChicken = addChicken(
            "PigIronChicken",
            this.nextID(),
            "PigIronChicken.png",
            0xe6b8b8,
            0xdba9a9,
            SpawnType.NONE,
            new String[] { "en_US:Pig Iron Chicken", "ja_JP:ピッグアイアンのニワトリ" }).setLayString("ore:ingotPigIron");
        allChickens.add(pigIronChicken);

        bloodSlimeChicken = addChicken(
            "BloodSlimeChicken",
            this.nextID(),
            "BloodSlimeChicken.png",
            0xc50616,
            0xee0316,
            SpawnType.NONE,
            new String[] { "en_US:Blood Slime Chicken", "ja_JP:ブラッドスライムのニワトリ" })
                .setLayItem(new ItemStack(TinkerWorld.strangeFood, 1, 1));
        allChickens.add(bloodSlimeChicken);

        blueSlimeChicken = addChicken(
            "BlueSlimeChicken",
            this.nextID(),
            "BlueSlimeChicken.png",
            0x67b4c4,
            0x30717f,
            SpawnType.NONE,
            new String[] { "en_US:Blue Slime Chicken", "ja_JP:ブルースライムのニワトリ" })
                .setLayItem(new ItemStack(TinkerWorld.strangeFood, 1, 0));
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
