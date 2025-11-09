package ruiseki.omoshiroikamo.plugin.chicken;

import java.util.List;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;

public class MetalsChickens extends BaseChickenHandler {

    public MetalsChickens() {
        super("Metals", "Metals", "textures/entity/chicken/metals/");
        this.setNeedsModPresent(false);
        this.setStartID(200);
    }

    public static ChickensRegistryItem brassChicken = null;
    public static ChickensRegistryItem cupronickelChicken = null;
    public static ChickensRegistryItem electrumChicken = null;
    public static ChickensRegistryItem invarChicken = null;
    public static ChickensRegistryItem bronzeChicken = null;
    public static ChickensRegistryItem copperChicken = null;
    public static ChickensRegistryItem leadChicken = null;
    public static ChickensRegistryItem nickelChicken = null;
    public static ChickensRegistryItem platinumChicken = null;
    public static ChickensRegistryItem silverOreChicken = null;
    public static ChickensRegistryItem tinChicken = null;
    public static ChickensRegistryItem zincChicken = null;
    public static ChickensRegistryItem steelChicken = null;
    public static ChickensRegistryItem siliconChicken = null;
    public static ChickensRegistryItem sulfurChicken = null;
    public static ChickensRegistryItem saltpeterChicken = null;
    public static ChickensRegistryItem aluminumChicken = null;
    public static ChickensRegistryItem amberChicken = null;
    public static ChickensRegistryItem amethystChicken = null;
    public static ChickensRegistryItem malachiteChicken = null;
    public static ChickensRegistryItem peridotChicken = null;
    public static ChickensRegistryItem rubyChicken = null;
    public static ChickensRegistryItem sapphireChicken = null;
    public static ChickensRegistryItem tanzaniteChicken = null;
    public static ChickensRegistryItem topazChicken = null;
    public static ChickensRegistryItem garnetChicken = null;
    public static ChickensRegistryItem saltChicken = null;
    public static ChickensRegistryItem rubberChicken = null;
    public static ChickensRegistryItem uraniumChicken = null;

    @Override
    public List<ChickensRegistryItem> registerChickens(List<ChickensRegistryItem> allChickens) {
        invarChicken = addChicken(
            allChickens,
            "InvarChicken",
            this.nextID(),
            "InvarChicken.png",
            this.getFirstOreDictionary("ingotInvar"),
            0x989585,
            0xd1ccb6,
            SpawnType.NONE);

        bronzeChicken = addChicken(
            allChickens,
            "BronzeChicken",
            this.nextID(),
            "BronzeChicken.png",
            this.getFirstOreDictionary("ingotBronze"),
            0x9a6731,
            0xf6a44e,
            SpawnType.NONE);

        zincChicken = addChicken(
            allChickens,
            "ZincChicken",
            this.nextID(),
            "ZincChicken.png",
            this.getFirstOreDictionary("ingotZinc"),
            0xb7b7b7,
            0x868686,
            SpawnType.NONE);

        tinChicken = addChicken(
            allChickens,
            "TinChicken",
            this.nextID(),
            "TinChicken.png",
            this.getFirstOreDictionary("ingotTin"),
            0xfff7ee,
            0xbbb1a7,
            SpawnType.NONE);
        // TODO
        steelChicken = addChicken(
            allChickens,
            "SteelChicken",
            this.nextID(),
            "SteelChicken.png",
            this.getFirstOreDictionary("ingotSteel"),
            0xd3e1e3,
            0x8e9799,
            SpawnType.NONE);

        silverOreChicken = addChicken(
            allChickens,
            "SilverChicken",
            this.nextID(),
            "SilverChicken.png",
            this.getFirstOreDictionary("ingotSilver"),
            0xbebebe,
            0xffffff,
            SpawnType.NONE);

        platinumChicken = addChicken(
            allChickens,
            "PlatinumChicken",
            this.nextID(),
            "PlatinumChicken.png",
            this.getFirstOreDictionary("ingotPlatinum"),
            0xffffff,
            0x8d9a96,
            SpawnType.NONE);

        nickelChicken = addChicken(
            allChickens,
            "NickelChicken",
            this.nextID(),
            "NickelChicken.png",
            this.getFirstOreDictionary("ingotNickel"),
            0xefffec,
            0xa2b69f,
            SpawnType.NONE);

        leadChicken = addChicken(
            allChickens,
            "LeadChicken",
            this.nextID(),
            "LeadChicken.png",
            this.getFirstOreDictionary("ingotLead"),
            0x777777,
            0x383838,
            SpawnType.NONE);

        copperChicken = addChicken(
            allChickens,
            "CopperChicken",
            this.nextID(),
            "CopperChicken.png",
            this.getFirstOreDictionary("ingotCopper"),
            0xc06a48,
            0xff9d76,
            SpawnType.NONE);

        brassChicken = addChicken(
            allChickens,
            "BrassChicken",
            this.nextID(),
            "BrassChicken.png",
            this.getFirstOreDictionary("ingotBrass"),
            0xa99340,
            0xffe377,
            SpawnType.NONE);

        cupronickelChicken = addChicken(
            allChickens,
            "CupronickelChicken",
            this.nextID(),
            "CupronickelChicken.png",
            this.getFirstOreDictionary("ingotCupronickel"),
            0xd8ccb4,
            0x98896c,
            SpawnType.NONE);

        electrumChicken = addChicken(
            allChickens,
            "ElectrumChicken",
            this.nextID(),
            "ElectrumChicken.png",
            this.getFirstOreDictionary("ingotElectrum"),
            0xfff2b1,
            0xd4be50,
            SpawnType.NONE);

        siliconChicken = addChicken(
            allChickens,
            "SiliconChicken",
            this.nextID(),
            "SiliconChicken.png",
            this.getFirstOreDictionary("itemSilicon"),
            0x5f706b,
            0x424242,
            SpawnType.NONE);

        sulfurChicken = addChicken(
            allChickens,
            "SulfurChicken",
            this.nextID(),
            "SulfurChicken.png",
            this.getFirstOreDictionary("dustSulfur"),
            0xFFE782,
            0xAD9326,
            SpawnType.NONE);

        saltpeterChicken = addChicken(
            allChickens,
            "SaltpeterChicken",
            this.nextID(),
            "SaltpeterChicken.png",
            this.getFirstOreDictionary("dustSaltpeter"),
            0xDDD6D6,
            0xAC9E9D,
            SpawnType.NONE);

        ItemStack ingotAlu = this.getFirstOreDictionary("ingotAluminum");
        if (ingotAlu == null) {
            ingotAlu = this.getFirstOreDictionary("ingotAluminium");
        }

        aluminumChicken = addChicken(
            allChickens,
            "AluminiumChicken",
            this.nextID(),
            "AluminiumChicken.png",
            ingotAlu,
            0xd3dddc,
            0xcbd7d6,
            SpawnType.NONE);

        amberChicken = addChicken(
            allChickens,
            "AmberChicken",
            this.nextID(),
            "AmberChicken.png",
            this.getFirstOreDictionary("gemAmber"),
            0xFFAD21,
            0x7F5113,
            SpawnType.NONE);

        amethystChicken = addChicken(
            allChickens,
            "AmethystChicken",
            this.nextID(),
            "AmethystChicken.png",
            this.getFirstOreDictionary("gemAmethyst"),
            0xE051ED,
            0x841D8E,
            SpawnType.NONE);

        malachiteChicken = addChicken(
            allChickens,
            "MalachiteChicken",
            this.nextID(),
            "MalachiteChicken.png",
            this.getFirstOreDictionary("gemMalachite"),
            0x29B17F,
            0x085F50,
            SpawnType.NONE);

        peridotChicken = addChicken(
            allChickens,
            "PeridotChicken",
            this.nextID(),
            "PeridotChicken.png",
            this.getFirstOreDictionary("gemPeridot"),
            0x6CA127,
            0x29430B,
            SpawnType.NONE);

        rubyChicken = addChicken(
            allChickens,
            "RubyChicken",
            this.nextID(),
            "RubyChicken.png",
            this.getFirstOreDictionary("gemRuby"),
            0xB7002E,
            0x5A0116,
            SpawnType.NONE);

        sapphireChicken = addChicken(
            allChickens,
            "SapphireChicken",
            this.nextID(),
            "SapphireChicken.png",
            this.getFirstOreDictionary("gemSapphire"),
            0x19689A,
            0x0D4565,
            SpawnType.NONE);

        tanzaniteChicken = addChicken(
            allChickens,
            "TanzaniteChicken",
            this.nextID(),
            "TanzaniteChicken.png",
            this.getFirstOreDictionary("gemTanzanite"),
            0x7310C0,
            0x5A007F,
            SpawnType.NONE);

        topazChicken = addChicken(
            allChickens,
            "TopazChicken",
            this.nextID(),
            "TopazChicken.png",
            this.getFirstOreDictionary("gemTopaz"),
            0xD64D00,
            0x7C3400,
            SpawnType.NONE);

        ItemStack gemGarnet = this.getFirstOreDictionary("gemGarnet");
        if (gemGarnet == null) {
            gemGarnet = this.getFirstOreDictionary("gemRedGarnet"); // TechReborn
        }

        garnetChicken = addChicken(
            allChickens,
            "GarnetChicken",
            this.nextID(),
            "GarnetChicken.png",
            gemGarnet,
            0xA45962,
            0x44171A,
            SpawnType.NONE);

        ItemStack itemSalt = this.getFirstOreDictionary("itemSalt");
        if (itemSalt == null) {
            itemSalt = this.getFirstOreDictionary("dustSalt");
        }
        if (itemSalt == null) {
            itemSalt = this.getFirstOreDictionary("foodSalt");
        }

        saltChicken = addChicken(
            allChickens,
            "SaltChicken",
            this.nextID(),
            "SaltChicken.png",
            itemSalt,
            0xEAE8DA,
            0xDBD9CC,
            SpawnType.NONE);

        ItemStack itemRubber = this.getFirstOreDictionary("itemRubber");
        if (itemRubber == null) {
            itemRubber = this.getFirstOreDictionary("materialRubber");
        }

        rubberChicken = addChicken(
            allChickens,
            "RubberChicken",
            this.nextID(),
            "RubberChicken.png",
            itemRubber,
            0x895D02,
            0x4E3209,
            SpawnType.NONE);

        uraniumChicken = addChicken(
            allChickens,
            "UraniumChicken",
            this.nextID(),
            "UraniumChicken.png",
            this.getFirstOreDictionary("ingotUranium"),
            0x91d76d,
            0x9ce26c,
            SpawnType.NONE);

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
