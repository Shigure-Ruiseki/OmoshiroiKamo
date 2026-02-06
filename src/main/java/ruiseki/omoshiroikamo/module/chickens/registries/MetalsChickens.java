package ruiseki.omoshiroikamo.module.chickens.registries;

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

        invarChicken = addChicken("InvarChicken", nextID(), "InvarChicken", 0x989585, 0xd1ccb6, SpawnType.NONE)
            .setLayString("ore:ingotInvar");
        allChickens.add(invarChicken);

        bronzeChicken = addChicken("BronzeChicken", nextID(), "BronzeChicken", 0x9a6731, 0xf6a44e, SpawnType.NONE)
            .setLayString("ore:ingotBronze");
        allChickens.add(bronzeChicken);

        zincChicken = addChicken("ZincChicken", nextID(), "ZincChicken", 0xb7b7b7, 0x868686, SpawnType.NONE)
            .setLayString("ore:ingotZinc");
        allChickens.add(zincChicken);

        tinChicken = addChicken("TinChicken", nextID(), "TinChicken", 0xfff7ee, 0xbbb1a7, SpawnType.NONE)
            .setLayString("ore:ingotTin");
        allChickens.add(tinChicken);

        steelChicken = addChicken("SteelChicken", nextID(), "SteelChicken", 0xd3e1e3, 0x8e9799, SpawnType.NONE)
            .setLayString("ore:ingotSteel");
        allChickens.add(steelChicken);

        silverOreChicken = addChicken("SilverChicken", nextID(), "SilverChicken", 0xbebebe, 0xffffff, SpawnType.NONE)
            .setLayString("ore:ingotSilver");
        allChickens.add(silverOreChicken);

        platinumChicken = addChicken("PlatinumChicken", nextID(), "PlatinumChicken", 0xffffff, 0x8d9a96, SpawnType.NONE)
            .setLayString("ore:ingotPlatinum");
        allChickens.add(platinumChicken);

        nickelChicken = addChicken("NickelChicken", nextID(), "NickelChicken", 0xefffec, 0xa2b69f, SpawnType.NONE)
            .setLayString("ore:ingotNickel");
        allChickens.add(nickelChicken);

        leadChicken = addChicken("LeadChicken", nextID(), "LeadChicken", 0x777777, 0x383838, SpawnType.NONE)
            .setLayString("ore:ingotLead");
        allChickens.add(leadChicken);

        copperChicken = addChicken("CopperChicken", nextID(), "CopperChicken", 0xc06a48, 0xff9d76, SpawnType.NONE)
            .setLayString("ore:ingotCopper");
        allChickens.add(copperChicken);

        brassChicken = addChicken("BrassChicken", nextID(), "BrassChicken", 0xa99340, 0xffe377, SpawnType.NONE)
            .setLayString("ore:ingotBrass");
        allChickens.add(brassChicken);

        cupronickelChicken = addChicken(
            "CupronickelChicken",
            nextID(),
            "CupronickelChicken",
            0xd8ccb4,
            0x98896c,
            SpawnType.NONE).setLayString("ore:ingotCupronickel");
        allChickens.add(cupronickelChicken);

        electrumChicken = addChicken("ElectrumChicken", nextID(), "ElectrumChicken", 0xfff2b1, 0xd4be50, SpawnType.NONE)
            .setLayString("ore:ingotElectrum");
        allChickens.add(electrumChicken);

        siliconChicken = addChicken("SiliconChicken", nextID(), "SiliconChicken", 0x5f706b, 0x424242, SpawnType.NONE)
            .setLayString("ore:itemSilicon");
        allChickens.add(siliconChicken);

        sulfurChicken = addChicken("SulfurChicken", nextID(), "SulfurChicken", 0xFFE782, 0xAD9326, SpawnType.NONE)
            .setLayString("ore:dustSulfur");
        allChickens.add(sulfurChicken);

        saltpeterChicken = addChicken(
            "SaltpeterChicken",
            nextID(),
            "SaltpeterChicken",
            0xDDD6D6,
            0xAC9E9D,
            SpawnType.NONE).setLayString("ore:dustSaltpeter");
        allChickens.add(saltpeterChicken);

        aluminumChicken = addChicken(
            "AluminiumChicken",
            nextID(),
            "AluminiumChicken",
            0xd3dddc,
            0xcbd7d6,
            SpawnType.NONE).setLayString("ore:ingotAluminum|ingotAluminium");
        allChickens.add(aluminumChicken);

        amberChicken = addChicken("AmberChicken", nextID(), "AmberChicken", 0xFFAD21, 0x7F5113, SpawnType.NONE)
            .setLayString("ore:gemAmber");
        allChickens.add(amberChicken);

        amethystChicken = addChicken("AmethystChicken", nextID(), "AmethystChicken", 0xE051ED, 0x841D8E, SpawnType.NONE)
            .setLayString("ore:gemAmethyst");
        allChickens.add(amethystChicken);

        malachiteChicken = addChicken(
            "MalachiteChicken",
            nextID(),
            "MalachiteChicken",
            0x29B17F,
            0x085F50,
            SpawnType.NONE).setLayString("ore:gemMalachite");
        allChickens.add(malachiteChicken);

        peridotChicken = addChicken("PeridotChicken", nextID(), "PeridotChicken", 0x6CA127, 0x29430B, SpawnType.NONE)
            .setLayString("ore:gemPeridot");
        allChickens.add(peridotChicken);

        rubyChicken = addChicken("RubyChicken", nextID(), "RubyChicken", 0xB7002E, 0x5A0116, SpawnType.NONE)
            .setLayString("ore:gemRuby");
        allChickens.add(rubyChicken);

        sapphireChicken = addChicken("SapphireChicken", nextID(), "SapphireChicken", 0x19689A, 0x0D4565, SpawnType.NONE)
            .setLayString("ore:gemSapphire");
        allChickens.add(sapphireChicken);

        tanzaniteChicken = addChicken(
            "TanzaniteChicken",
            nextID(),
            "TanzaniteChicken",
            0x7310C0,
            0x5A007F,
            SpawnType.NONE).setLayString("ore:gemTanzanite");
        allChickens.add(tanzaniteChicken);

        topazChicken = addChicken("TopazChicken", nextID(), "TopazChicken", 0xD64D00, 0x7C3400, SpawnType.NONE)
            .setLayString("ore:gemTopaz");
        allChickens.add(topazChicken);

        garnetChicken = addChicken("GarnetChicken", nextID(), "GarnetChicken", 0xA45962, 0x44171A, SpawnType.NONE)
            .setLayString("ore:gemGarnet|gemRedGarnet");
        allChickens.add(garnetChicken);

        saltChicken = addChicken("SaltChicken", nextID(), "SaltChicken", 0xEAE8DA, 0xDBD9CC, SpawnType.NONE)
            .setLayString("ore:itemSalt|dustSalt|foodSalt");
        allChickens.add(saltChicken);

        rubberChicken = addChicken("RubberChicken", nextID(), "RubberChicken", 0x895D02, 0x4E3209, SpawnType.NONE)
            .setLayString("ore:itemRubber|materialRubber");
        allChickens.add(rubberChicken);

        uraniumChicken = addChicken("UraniumChicken", nextID(), "UraniumChicken", 0x91d76d, 0x9ce26c, SpawnType.NONE)
            .setLayString("ore:ingotUranium");
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
