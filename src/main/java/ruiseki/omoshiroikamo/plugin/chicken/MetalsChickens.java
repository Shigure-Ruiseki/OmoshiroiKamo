package ruiseki.omoshiroikamo.plugin.chicken;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;

public class MetalsChickens extends BaseChickenHandler {

    public MetalsChickens() {
        super("Metals", "Metals", "textures/entity/chicken/metals/");
        this.setNeedsModPresent(false);
        this.setStartID(200);
    }

    public static ChickensRegistryItem brassChicken;
    public static ChickensRegistryItem cupronickelChicken;
    public static ChickensRegistryItem electrumChicken;
    public static ChickensRegistryItem invarChicken;
    public static ChickensRegistryItem bronzeChicken;
    public static ChickensRegistryItem copperChicken;
    public static ChickensRegistryItem leadChicken;
    public static ChickensRegistryItem nickelChicken;
    public static ChickensRegistryItem platinumChicken;
    public static ChickensRegistryItem silverOreChicken;
    public static ChickensRegistryItem tinChicken;
    public static ChickensRegistryItem zincChicken;
    public static ChickensRegistryItem steelChicken;
    public static ChickensRegistryItem siliconChicken;
    public static ChickensRegistryItem sulfurChicken;
    public static ChickensRegistryItem saltpeterChicken;
    public static ChickensRegistryItem aluminumChicken;
    public static ChickensRegistryItem amberChicken;
    public static ChickensRegistryItem amethystChicken;
    public static ChickensRegistryItem malachiteChicken;
    public static ChickensRegistryItem peridotChicken;
    public static ChickensRegistryItem rubyChicken;
    public static ChickensRegistryItem sapphireChicken;
    public static ChickensRegistryItem tanzaniteChicken;
    public static ChickensRegistryItem topazChicken;
    public static ChickensRegistryItem garnetChicken;
    public static ChickensRegistryItem saltChicken;
    public static ChickensRegistryItem rubberChicken;
    public static ChickensRegistryItem uraniumChicken;

    @Override
    public List<ChickensRegistryItem> registerChickens() {
        List<ChickensRegistryItem> allChickens = new ArrayList<>();

        invarChicken = addChicken(
            "InvarChicken",
            nextID(),
            "InvarChicken.png",
            0x989585,
            0xd1ccb6,
            SpawnType.NONE,
            new String[] { "en_US:Invar Chicken", "ja_JP:インバーのニワトリ" }).setLayString("ore:ingotInvar");
        allChickens.add(invarChicken);

        bronzeChicken = addChicken(
            "BronzeChicken",
            nextID(),
            "BronzeChicken.png",
            0x9a6731,
            0xf6a44e,
            SpawnType.NONE,
            new String[] { "en_US:Bronze Chicken", "ja_JP:ブロンズのニワトリ" }).setLayString("ore:ingotBronze");
        allChickens.add(bronzeChicken);

        zincChicken = addChicken(
            "ZincChicken",
            nextID(),
            "ZincChicken.png",
            0xb7b7b7,
            0x868686,
            SpawnType.NONE,
            new String[] { "en_US:Zinc Chicken", "ja_JP:亜鉛のニワトリ" }).setLayString("ore:ingotZinc");
        allChickens.add(zincChicken);

        tinChicken = addChicken(
            "TinChicken",
            nextID(),
            "TinChicken.png",
            0xfff7ee,
            0xbbb1a7,
            SpawnType.NONE,
            new String[] { "en_US:Tin Chicken", "ja_JP:スズのニワトリ" }).setLayString("ore:ingotTin");
        allChickens.add(tinChicken);

        steelChicken = addChicken(
            "SteelChicken",
            nextID(),
            "SteelChicken.png",
            0xd3e1e3,
            0x8e9799,
            SpawnType.NONE,
            new String[] { "en_US:Steel Chicken", "ja_JP:鋼鉄のニワトリ" }).setLayString("ore:ingotSteel");
        allChickens.add(steelChicken);

        silverOreChicken = addChicken(
            "SilverChicken",
            nextID(),
            "SilverChicken.png",
            0xbebebe,
            0xffffff,
            SpawnType.NONE,
            new String[] { "en_US:Silver Chicken", "ja_JP:銀のニワトリ" }).setLayString("ore:ingotSilver");
        allChickens.add(silverOreChicken);

        platinumChicken = addChicken(
            "PlatinumChicken",
            nextID(),
            "PlatinumChicken.png",
            0xffffff,
            0x8d9a96,
            SpawnType.NONE,
            new String[] { "en_US:Platinum Chicken", "ja_JP:プラチナのニワトリ" }).setLayString("ore:ingotPlatinum");
        allChickens.add(platinumChicken);

        nickelChicken = addChicken(
            "NickelChicken",
            nextID(),
            "NickelChicken.png",
            0xefffec,
            0xa2b69f,
            SpawnType.NONE,
            new String[] { "en_US:Nickel Chicken", "ja_JP:ニッケルのニワトリ" }).setLayString("ore:ingotNickel");
        allChickens.add(nickelChicken);

        leadChicken = addChicken(
            "LeadChicken",
            nextID(),
            "LeadChicken.png",
            0x777777,
            0x383838,
            SpawnType.NONE,
            new String[] { "en_US:Lead Chicken", "ja_JP:鉛のニワトリ" }).setLayString("ore:ingotLead");
        allChickens.add(leadChicken);

        copperChicken = addChicken(
            "CopperChicken",
            nextID(),
            "CopperChicken.png",
            0xc06a48,
            0xff9d76,
            SpawnType.NONE,
            new String[] { "en_US:Copper Chicken", "ja_JP:銅のニワトリ" }).setLayString("ore:ingotCopper");
        allChickens.add(copperChicken);

        brassChicken = addChicken(
            "BrassChicken",
            nextID(),
            "BrassChicken.png",
            0xa99340,
            0xffe377,
            SpawnType.NONE,
            new String[] { "en_US:Brass Chicken", "ja_JP:真鍮のニワトリ" }).setLayString("ore:ingotBrass");
        allChickens.add(brassChicken);

        cupronickelChicken = addChicken(
            "CupronickelChicken",
            nextID(),
            "CupronickelChicken.png",
            0xd8ccb4,
            0x98896c,
            SpawnType.NONE,
            new String[] { "en_US:Cupronickel Chicken", "ja_JP:白銅のニワトリ" }).setLayString("ore:ingotCupronickel");
        allChickens.add(cupronickelChicken);

        electrumChicken = addChicken(
            "ElectrumChicken",
            nextID(),
            "ElectrumChicken.png",
            0xfff2b1,
            0xd4be50,
            SpawnType.NONE,
            new String[] { "en_US:Electrum Chicken", "ja_JP:エレクトラムのニワトリ" }).setLayString("ore:ingotElectrum");
        allChickens.add(electrumChicken);

        siliconChicken = addChicken(
            "SiliconChicken",
            nextID(),
            "SiliconChicken.png",
            0x5f706b,
            0x424242,
            SpawnType.NONE,
            new String[] { "en_US:Silicon Chicken", "ja_JP:シリコンのニワトリ" }).setLayString("ore:itemSilicon");
        allChickens.add(siliconChicken);

        sulfurChicken = addChicken(
            "SulfurChicken",
            nextID(),
            "SulfurChicken.png",
            0xFFE782,
            0xAD9326,
            SpawnType.NONE,
            new String[] { "en_US:Sulfur Chicken", "ja_JP:硫黄のニワトリ" }).setLayString("ore:dustSulfur");
        allChickens.add(sulfurChicken);

        saltpeterChicken = addChicken(
            "SaltpeterChicken",
            nextID(),
            "SaltpeterChicken.png",
            0xDDD6D6,
            0xAC9E9D,
            SpawnType.NONE,
            new String[] { "en_US:Saltpeter Chicken", "ja_JP:硝石のニワトリ" }).setLayString("ore:dustSaltpeter");
        allChickens.add(saltpeterChicken);

        aluminumChicken = addChicken(
            "AluminiumChicken",
            nextID(),
            "AluminiumChicken.png",
            0xd3dddc,
            0xcbd7d6,
            SpawnType.NONE,
            new String[] { "en_US:Aluminium Chicken", "ja_JP:アルミニウムのニワトリ" })
                .setLayString("ore:ingotAluminum|ingotAluminium");
        allChickens.add(aluminumChicken);

        amberChicken = addChicken(
            "AmberChicken",
            nextID(),
            "AmberChicken.png",
            0xFFAD21,
            0x7F5113,
            SpawnType.NONE,
            new String[] { "en_US:Amber Chicken", "ja_JP:琥珀のニワトリ" }).setLayString("ore:gemAmber");
        allChickens.add(amberChicken);

        amethystChicken = addChicken(
            "AmethystChicken",
            nextID(),
            "AmethystChicken.png",
            0xE051ED,
            0x841D8E,
            SpawnType.NONE,
            new String[] { "en_US:Amethyst Chicken", "ja_JP:アメジストのニワトリ" }).setLayString("ore:gemAmethyst");
        allChickens.add(amethystChicken);

        malachiteChicken = addChicken(
            "MalachiteChicken",
            nextID(),
            "MalachiteChicken.png",
            0x29B17F,
            0x085F50,
            SpawnType.NONE,
            new String[] { "en_US:Malachite Chicken", "ja_JP:マラカイトのニワトリ" }).setLayString("ore:gemMalachite");
        allChickens.add(malachiteChicken);

        peridotChicken = addChicken(
            "PeridotChicken",
            nextID(),
            "PeridotChicken.png",
            0x6CA127,
            0x29430B,
            SpawnType.NONE,
            new String[] { "en_US:Peridot Chicken", "ja_JP:ペリドットのニワトリ" }).setLayString("ore:gemPeridot");
        allChickens.add(peridotChicken);

        rubyChicken = addChicken(
            "RubyChicken",
            nextID(),
            "RubyChicken.png",
            0xB7002E,
            0x5A0116,
            SpawnType.NONE,
            new String[] { "en_US:Ruby Chicken", "ja_JP:ルビーのニワトリ" }).setLayString("ore:gemRuby");
        allChickens.add(rubyChicken);

        sapphireChicken = addChicken(
            "SapphireChicken",
            nextID(),
            "SapphireChicken.png",
            0x19689A,
            0x0D4565,
            SpawnType.NONE,
            new String[] { "en_US:Sapphire Chicken", "ja_JP:サファイアのニワトリ" }).setLayString("ore:gemSapphire");
        allChickens.add(sapphireChicken);

        tanzaniteChicken = addChicken(
            "TanzaniteChicken",
            nextID(),
            "TanzaniteChicken.png",
            0x7310C0,
            0x5A007F,
            SpawnType.NONE,
            new String[] { "en_US:Tanzanite Chicken", "ja_JP:タンザナイトのニワトリ" }).setLayString("ore:gemTanzanite");
        allChickens.add(tanzaniteChicken);

        topazChicken = addChicken(
            "TopazChicken",
            nextID(),
            "TopazChicken.png",
            0xD64D00,
            0x7C3400,
            SpawnType.NONE,
            new String[] { "en_US:Topaz Chicken", "ja_JP:トパーズのニワトリ" }).setLayString("ore:gemTopaz");
        allChickens.add(topazChicken);

        garnetChicken = addChicken(
            "GarnetChicken",
            nextID(),
            "GarnetChicken.png",
            0xA45962,
            0x44171A,
            SpawnType.NONE,
            new String[] { "en_US:Garnet Chicken", "ja_JP:ガーネットのニワトリ" }).setLayString("ore:gemGarnet|gemRedGarnet");
        allChickens.add(garnetChicken);

        saltChicken = addChicken(
            "SaltChicken",
            nextID(),
            "SaltChicken.png",
            0xEAE8DA,
            0xDBD9CC,
            SpawnType.NONE,
            new String[] { "en_US:Salt Chicken", "ja_JP:塩のニワトリ" }).setLayString("ore:itemSalt|dustSalt|foodSalt");
        allChickens.add(saltChicken);

        rubberChicken = addChicken(
            "RubberChicken",
            nextID(),
            "RubberChicken.png",
            0x895D02,
            0x4E3209,
            SpawnType.NONE,
            new String[] { "en_US:Rubber Chicken", "ja_JP:ゴムのニワトリ" }).setLayString("ore:itemRubber|materialRubber");
        allChickens.add(rubberChicken);

        uraniumChicken = addChicken(
            "UraniumChicken",
            nextID(),
            "UraniumChicken.png",
            0x91d76d,
            0x9ce26c,
            SpawnType.NONE,
            new String[] { "en_US:Uranium Chicken", "ja_JP:ウランのニワトリ" }).setLayString("ore:ingotUranium");
        allChickens.add(uraniumChicken);

        return allChickens;
    }

    @Override
    public void registerAllParents(List<ChickensRegistryItem> allChickens) {
        setParents(brassChicken, copperChicken, zincChicken);
        setParents(bronzeChicken, copperChicken, tinChicken);
        setParents(invarChicken, BaseChickens.ironChicken, nickelChicken);
        setParents(cupronickelChicken, copperChicken, nickelChicken);
        setParents(electrumChicken, silverOreChicken, BaseChickens.goldChicken);
        setParents(steelChicken, BaseChickens.ironChicken, BaseChickens.coalChicken);
        setParents(copperChicken, BaseChickens.yellowChicken, BaseChickens.brownChicken);
        setParents(leadChicken, BaseChickens.ironChicken, BaseChickens.cyanChicken);
        setParents(tinChicken, BaseChickens.whiteChicken, BaseChickens.clayChicken);
        setParents(nickelChicken, BaseChickens.whiteChicken, BaseChickens.greenChicken);
        setParents(zincChicken, BaseChickens.whiteChicken, BaseChickens.clayChicken);
        setParents(silverOreChicken, BaseChickens.ironChicken, BaseChickens.whiteChicken);
        setParents(platinumChicken, nickelChicken, silverOreChicken);
        setParents(sulfurChicken, BaseChickens.gunpowderChicken, BaseChickens.flintChicken);
        setParents(saltpeterChicken, sulfurChicken, BaseChickens.redstoneChicken);
        setParents(siliconChicken, BaseChickens.clayChicken, BaseChickens.sandChicken);
        setParents(aluminumChicken, BaseChickens.flintChicken, BaseChickens.ironChicken);
        setParents(amberChicken, BaseChickens.waterChicken, BaseChickens.logChicken);
        setParents(amethystChicken, BaseChickens.ghastChicken, BaseChickens.purpleChicken);
        if (this.getFirstOreDictionary("ingotCopper") != null) {
            setParents(malachiteChicken, copperChicken, BaseChickens.coalChicken);
        } else {
            setParents(malachiteChicken, BaseChickens.greenChicken, BaseChickens.coalChicken);
        }
        if (this.getFirstOreDictionary("itemSilicon") != null) {
            setParents(peridotChicken, siliconChicken, BaseChickens.greenChicken);
        } else {
            setParents(peridotChicken, BaseChickens.quartzChicken, BaseChickens.greenChicken);
        }
        if (this.getFirstOreDictionary("ingotAluminum") != null
            || this.getFirstOreDictionary("ingotAluminium") != null) {
            setParents(sapphireChicken, BaseChickens.blueChicken, aluminumChicken);
            setParents(rubyChicken, BaseChickens.redChicken, aluminumChicken);
        } else {
            setParents(sapphireChicken, BaseChickens.blueChicken, BaseChickens.snowballChicken);
            setParents(rubyChicken, BaseChickens.redChicken, BaseChickens.snowballChicken);
        }
        if (this.getFirstOreDictionary("gemRuby") != null && this.getFirstOreDictionary("gemSapphire") != null) {
            setParents(garnetChicken, sapphireChicken, rubyChicken);
        } else {
            setParents(garnetChicken, BaseChickens.ironChicken, BaseChickens.redChicken);
        }
        setParents(tanzaniteChicken, BaseChickens.quartzChicken, BaseChickens.purpleChicken);
        setParents(topazChicken, BaseChickens.quartzChicken, BaseChickens.orangeChicken);
        setParents(saltChicken, BaseChickens.waterChicken, BaseChickens.lavaChicken);
        setParents(rubberChicken, BaseChickens.logChicken, BaseChickens.orangeChicken);
        setParents(uraniumChicken, BaseChickens.redstoneChicken, BaseChickens.enderChicken);
    }

}
