package ruiseki.omoshiroikamo.plugin.chicken;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import de.ellpeck.actuallyadditions.mod.items.InitItems;
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;

public class ActuallyAdditionsChickens extends BaseChickenHandler {

    public static ChickensRegistryItem blackQuartzChicken;
    public static ChickensRegistryItem voidCrystalChicken;
    public static ChickensRegistryItem palisCrystalChicken;
    public static ChickensRegistryItem enoriCrystalChicken;
    public static ChickensRegistryItem restoniaCrystalChicken;
    public static ChickensRegistryItem emeradicCrystalChicken;
    public static ChickensRegistryItem diamantineCrystalChicken;

    public ActuallyAdditionsChickens() {
        super("ActuallyAdditions", "Actually Additions", "textures/entity/chicken/actuallyadditions/");
        this.setStartID(2140);
    }

    @Override
    public List<ChickensRegistryItem> registerChickens() {
        List<ChickensRegistryItem> allChickens = new ArrayList<>();
        Item itemCrystal = InitItems.itemCrystal;

        ItemStack restoniaCrystal = new ItemStack(itemCrystal, 1, 0);
        restoniaCrystalChicken = addChicken(
            "RestoniaCrystalChicken",
            this.nextID(),
            "RestoniaCrystalChicken.png",
            0xCA0000,
            0x8C0000,
            SpawnType.NONE).setLayItem(restoniaCrystal)
                .setLang("en_US", "Restonia Crystal Chicken")
                .setLang("ja_JP", "レストニアクリスタルのニワトリ");
        allChickens.add(restoniaCrystalChicken);

        ItemStack palisCrystal = new ItemStack(itemCrystal, 1, 1);
        palisCrystalChicken = addChicken(
            "PalisCrystalChicken",
            this.nextID(),
            "PalisCrystalChicken.png",
            0x0E0E84,
            0x000048,
            SpawnType.NONE).setLayItem(palisCrystal)
                .setLang("en_US", "Palis Crystal Chicken")
                .setLang("ja_JP", "パリスクリスタルのニワトリ");
        allChickens.add(palisCrystalChicken);

        ItemStack diamantineCrystal = new ItemStack(itemCrystal, 1, 2);
        diamantineCrystalChicken = addChicken(
            "DiamantineCrystalChicken",
            this.nextID(),
            "DiamantineCrystalChicken.png",
            0xAFB1FF,
            0x797CE5,
            SpawnType.NONE).setLayItem(diamantineCrystal)
                .setLang("en_US", "Diamantine Crystal Chicken")
                .setLang("ja_JP", "ディアマンティンクリスタルのニワトリ");
        allChickens.add(diamantineCrystalChicken);

        ItemStack voidCrystal = new ItemStack(itemCrystal, 1, 3);
        voidCrystalChicken = addChicken(
            "VoidCrystalChicken",
            this.nextID(),
            "VoidCrystalChicken.png",
            0x1F1F1F,
            0x000000,
            SpawnType.NONE).setLayItem(voidCrystal)
                .setLang("en_US", "Void Crystal Chicken")
                .setLang("ja_JP", "ヴォイドクリスタルのニワトリ");
        allChickens.add(voidCrystalChicken);

        ItemStack emeradicCrystal = new ItemStack(itemCrystal, 1, 4);
        emeradicCrystalChicken = addChicken(
            "EmeradicCrystalChicken",
            this.nextID(),
            "EmeradicCrystalChicken.png",
            0x06D306,
            0x159A0E,
            SpawnType.NONE).setLayItem(emeradicCrystal)
                .setLang("en_US", "Emeradic Crystal Chicken")
                .setLang("ja_JP", "エメラディッククリスタルのニワトリ");
        allChickens.add(emeradicCrystalChicken);

        ItemStack enoriCrystal = new ItemStack(itemCrystal, 1, 5);
        enoriCrystalChicken = addChicken(
            "EnoriCrystalChicken",
            this.nextID(),
            "EnoriCrystalChicken.png",
            0xECE3FF,
            0xB5B5B5,
            SpawnType.NONE).setLayItem(enoriCrystal)
                .setLang("en_US", "Enori Crystal Chicken")
                .setLang("ja_JP", "エノリクリスタルのニワトリ");
        allChickens.add(enoriCrystalChicken);

        blackQuartzChicken = addChicken(
            "BlackQuartzChicken",
            this.nextID(),
            "BlackQuartzChicken.png",
            0x1F1F1F,
            0x535353,
            SpawnType.NONE).setLayString("ore:gemQuartzBlack")
                .setLang("en_US", "Black Quartz Chicken")
                .setLang("ja_JP", "ブラッククォーツのニワトリ");
        allChickens.add(blackQuartzChicken);

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
