package ruiseki.omoshiroikamo.plugin.chicken;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import de.ellpeck.actuallyadditions.mod.items.InitItems;
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;

public class ActuallyAdditionsChickens extends BaseChickenHandler {

    public static ChickensRegistryItem blackQuartzChicken = null;
    public static ChickensRegistryItem voidCrystalChicken = null;
    public static ChickensRegistryItem palisCrystalChicken = null;
    public static ChickensRegistryItem enoriCrystalChicken = null;
    public static ChickensRegistryItem restoniaCrystalChicken = null;
    public static ChickensRegistryItem emeradicCrystalChicken = null;
    public static ChickensRegistryItem diamantineCrystalChicken = null;

    public ActuallyAdditionsChickens() {
        super("ActuallyAdditions", "Actually Additions", "textures/entity/chicken/actuallyadditions/");
        this.setStartID(2140);
    }

    @Override
    public List<ChickensRegistryItem> registerChickens(List<ChickensRegistryItem> allChickens) {
        Item itemCrystal = InitItems.itemCrystal;
        ItemStack restoniaCrystal = new ItemStack(itemCrystal, 1, 0);
        restoniaCrystalChicken = addChicken(
            allChickens,
            "RestoniaCrystalChicken",
            this.nextID(),
            "RestoniaCrystalChicken.png",
            restoniaCrystal,
            0xCA0000,
            0x8C0000,
            SpawnType.NONE);

        ItemStack palisCrystal = new ItemStack(itemCrystal, 1, 1);
        palisCrystalChicken = addChicken(
            allChickens,
            "PalisCrystalChicken",
            this.nextID(),
            "PalisCrystalChicken.png",
            palisCrystal,
            0x0E0E84,
            0x000048,
            SpawnType.NONE);

        ItemStack diamantineCrystal = new ItemStack(itemCrystal, 1, 2);
        diamantineCrystalChicken = addChicken(
            allChickens,
            "DiamantineCrystalChicken",
            this.nextID(),
            "DiamantineCrystalChicken.png",
            diamantineCrystal,
            0xAFB1FF,
            0x797CE5,
            SpawnType.NONE);

        ItemStack voidCrystal = new ItemStack(itemCrystal, 1, 3);
        voidCrystalChicken = addChicken(
            allChickens,
            "VoidCrystalChicken",
            this.nextID(),
            "VoidCrystalChicken.png",
            voidCrystal,
            0x1F1F1F,
            0x000000,
            SpawnType.NONE);

        ItemStack emeradicCrystal = new ItemStack(itemCrystal, 1, 4);
        emeradicCrystalChicken = addChicken(
            allChickens,
            "EmeradicCrystalChicken",
            this.nextID(),
            "EmeradicCrystalChicken.png",
            emeradicCrystal,
            0x06D306,
            0x159A0E,
            SpawnType.NONE);

        ItemStack enoriCrystal = new ItemStack(itemCrystal, 1, 5);
        enoriCrystalChicken = addChicken(
            allChickens,
            "EnoriCrystalChicken",
            this.nextID(),
            "EnoriCrystalChicken.png",
            enoriCrystal,
            0xECE3FF,
            0xB5B5B5,
            SpawnType.NONE);

        blackQuartzChicken = addChicken(
            allChickens,
            "BlackQuartzChicken",
            this.nextID(),
            "BlackQuartzChicken.png",
            this.getFirstOreDictionary("gemQuartzBlack"),
            0x1F1F1F,
            0x535353,
            SpawnType.NONE);

        return allChickens;
    }

    @Override
    public void registerAllParents(List<ChickensRegistryItem> allChickens) {

        setParents(blackQuartzChicken, BaseChickens.quartzChicken, BaseChickens.blackChicken);
        setParents(restoniaCrystalChicken, BaseChickens.redstoneChicken, blackQuartzChicken);
        setParents(palisCrystalChicken, BaseChickens.blueChicken, blackQuartzChicken);
        setParents(voidCrystalChicken, BaseChickens.quartzChicken, palisCrystalChicken);
        setParents(enoriCrystalChicken, voidCrystalChicken, BaseChickens.ironChicken);
        setParents(emeradicCrystalChicken, palisCrystalChicken, BaseChickens.emeraldChicken);
        setParents(diamantineCrystalChicken, emeradicCrystalChicken, enoriCrystalChicken);
    }
}
