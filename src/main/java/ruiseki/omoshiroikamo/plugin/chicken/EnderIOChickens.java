package ruiseki.omoshiroikamo.plugin.chicken;

import java.util.ArrayList;
import java.util.List;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;

public class EnderIOChickens extends BaseChickenHandler {

    public static ChickensRegistryItem electricalSteelChicken;
    public static ChickensRegistryItem energeticAlloyChicken;
    public static ChickensRegistryItem vibrantAlloyChicken;
    public static ChickensRegistryItem redstoneAlloyChicken;
    public static ChickensRegistryItem conductiveIronChicken;
    public static ChickensRegistryItem pulsatingIronChicken;
    public static ChickensRegistryItem darkSteelChicken;
    public static ChickensRegistryItem endSteelChicken;
    public static ChickensRegistryItem soulariumChicken;

    public EnderIOChickens() {
        super("EnderIO", "EnderIO", "textures/entity/chicken/enderio/");
        setNeedsModPresent(true);
        this.setStartID(300);
    }

    @Override
    public List<ChickensRegistryItem> registerChickens() {
        List<ChickensRegistryItem> allChickens = new ArrayList<>();

        electricalSteelChicken = addChicken(
            "ElectricalSteelChicken",
            this.nextID(),
            "ElectricalSteelChicken.png",
            0x939393,
            0x474747,
            SpawnType.NONE,
            new String[] { "en_US:Electrical Steel Chicken", "ja_JP:電気鋼のニワトリ" })
                .setLayString("ore:ingotElectricalSteel");
        allChickens.add(electricalSteelChicken);

        energeticAlloyChicken = addChicken(
            "EnergeticAlloyChicken",
            this.nextID(),
            "EnergeticAlloyChicken.png",
            0xea6c05,
            0x65321b,
            SpawnType.NONE,
            new String[] { "en_US:Energetic Alloy Chicken", "ja_JP:エナジェティック合金のニワトリ" })
                .setLayString("ore:ingotEnergeticAlloy");
        allChickens.add(energeticAlloyChicken);

        vibrantAlloyChicken = addChicken(
            "VibrantAlloyChicken",
            this.nextID(),
            "VibrantAlloyChicken.png",
            0xbcf239,
            0x779c1d,
            SpawnType.NONE,
            new String[] { "en_US:Vibrant Alloy Chicken", "ja_JP:ヴァイブラント合金のニワトリ" })
                .setLayString("ore:ingotVibrantAlloy");
        allChickens.add(vibrantAlloyChicken);

        redstoneAlloyChicken = addChicken(
            "RedstoneAlloyChicken",
            this.nextID(),
            "RedstoneAlloyChicken.png",
            0xd03939,
            0x621919,
            SpawnType.NONE,
            new String[] { "en_US:Redstone Alloy Chicken", "ja_JP:レッドストーン合金のニワトリ" })
                .setLayString("ore:ingotRedstoneAlloy");
        allChickens.add(redstoneAlloyChicken);

        conductiveIronChicken = addChicken(
            "ConductiveIronChicken",
            this.nextID(),
            "ConductiveIronChicken.png",
            0xCC9D96,
            0x7E6764,
            SpawnType.NONE,
            new String[] { "en_US:Conductive Iron Chicken", "ja_JP:導電性鉄のニワトリ" })
                .setLayString("ore:ingotConductiveIron");
        allChickens.add(conductiveIronChicken);

        pulsatingIronChicken = addChicken(
            "PulsatingIronChicken",
            this.nextID(),
            "PulsatingIronChicken.png",
            0x6FE78B,
            0x406448,
            SpawnType.NONE,
            new String[] { "en_US:Pulsating Iron Chicken", "ja_JP:鼓動する鉄のニワトリ" }).setLayString("ore:ingotPulsatingIron");
        allChickens.add(pulsatingIronChicken);

        darkSteelChicken = addChicken(
            "DarkSteelChicken",
            this.nextID(),
            "DarkSteelChicken.png",
            0x4D4D4E,
            0x242424,
            SpawnType.NONE,
            new String[] { "en_US:Dark Steel Chicken", "ja_JP:ダークスチールのニワトリ" }).setLayString("ore:ingotDarkSteel");
        allChickens.add(darkSteelChicken);

        endSteelChicken = addChicken(
            "EndSteelChicken",
            this.nextID(),
            "EndSteelChicken.png",
            0x6F6935,
            0xF5EFBB,
            SpawnType.NONE,
            new String[] { "en_US:End Steel Chicken", "ja_JP:エンドスチールのニワトリ" }).setLayString("ore:ingotEndSteel");
        allChickens.add(endSteelChicken);

        soulariumChicken = addChicken(
            "SoulariumChicken",
            this.nextID(),
            "SoulariumChicken.png",
            0x6F5C36,
            0x4E371A,
            SpawnType.NONE,
            new String[] { "en_US:Soularium Chicken", "ja_JP:ソウラリウムのニワトリ" }).setLayString("ore:ingotSoularium");
        allChickens.add(soulariumChicken);

        return allChickens;
    }

    @Override
    public void registerAllParents(List<ChickensRegistryItem> allChickens) {
        setParents(electricalSteelChicken, BaseChickens.ironChicken, MetalsChickens.siliconChicken);
        setParents(energeticAlloyChicken, BaseChickens.goldChicken, BaseChickens.glowstoneChicken);
        setParents(vibrantAlloyChicken, energeticAlloyChicken, BaseChickens.enderChicken);
        setParents(redstoneAlloyChicken, BaseChickens.redstoneChicken, MetalsChickens.siliconChicken);
        setParents(conductiveIronChicken, BaseChickens.redstoneChicken, BaseChickens.ironChicken);
        setParents(pulsatingIronChicken, BaseChickens.ironChicken, BaseChickens.enderChicken);
        setParents(darkSteelChicken, BaseChickens.ironChicken, BaseChickens.obsidianChicken);
        setParents(endSteelChicken, darkSteelChicken, BaseChickens.obsidianChicken);
        setParents(soulariumChicken, BaseChickens.soulsandChicken, BaseChickens.goldChicken);
    }
}
