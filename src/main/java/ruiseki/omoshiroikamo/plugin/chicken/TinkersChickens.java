package ruiseki.omoshiroikamo.plugin.chicken;

import java.util.List;

import net.minecraft.item.ItemStack;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import tconstruct.world.TinkerWorld;

public class TinkersChickens extends BaseChickenHandler {

    public static ChickensRegistryItem arditeChicken = null;
    public static ChickensRegistryItem colbaltChicken = null;
    public static ChickensRegistryItem manyullynChicken = null;
    public static ChickensRegistryItem pigIronChicken = null;
    public static ChickensRegistryItem bloodSlimeChicken = null;
    public static ChickensRegistryItem blueSlimeChicken = null;

    public TinkersChickens() {
        super("TConstruct", "Tinkers Construct", "textures/entity/chicken/tinkers/");
        this.setStartID(500);
    }

    @Override
    public List<ChickensRegistryItem> registerChickens(List<ChickensRegistryItem> allChickens) {

        arditeChicken = addChicken(
            allChickens,
            "ArditeChicken",
            this.nextID(),
            "ArditeChicken.png",
            this.getFirstOreDictionary("ingotArdite"),
            0xdc3e00,
            0xffb20b,
            SpawnType.NONE);

        colbaltChicken = addChicken(
            allChickens,
            "CobaltChicken",
            this.nextID(),
            "CobaltChicken.png",
            this.getFirstOreDictionary("ingotCobalt"),
            0x0c5abe,
            0x03d94f1,
            SpawnType.NONE);

        manyullynChicken = addChicken(
            allChickens,
            "ManyullynChicken",
            this.nextID(),
            "ManyullynChicken.png",
            this.getFirstOreDictionary("ingotManyullyn"),
            0x652e87,
            0xbc8fe7,
            SpawnType.NONE);

        pigIronChicken = addChicken(
            allChickens,
            "PigIronChicken",
            this.nextID(),
            "PigIronChicken.png",
            this.getFirstOreDictionary("ingotPigIron"),
            0xe6b8b8,
            0xdba9a9,
            SpawnType.NONE);

        bloodSlimeChicken = addChicken(
            allChickens,
            "BloodSlimeChicken",
            this.nextID(),
            "BloodSlimeChicken.png",
            new ItemStack(TinkerWorld.strangeFood, 1, 1),
            0xc50616,
            0xee0316,
            SpawnType.NONE);

        blueSlimeChicken = addChicken(
            allChickens,
            "BlueSlimeChicken",
            this.nextID(),
            "BlueSlimeChicken.png",
            new ItemStack(TinkerWorld.strangeFood, 1, 0),
            0x67b4c4,
            0x30717f,
            SpawnType.NONE);

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
