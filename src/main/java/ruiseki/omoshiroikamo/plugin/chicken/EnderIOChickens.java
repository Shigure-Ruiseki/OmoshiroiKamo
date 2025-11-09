package ruiseki.omoshiroikamo.plugin.chicken;

import java.util.List;

import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;

public class EnderIOChickens extends BaseChickenHandler {

    public static ChickensRegistryItem electricalSteelChicken = null;
    public static ChickensRegistryItem energeticAlloyChicken = null;
    public static ChickensRegistryItem vibrantAlloyChicken = null;
    public static ChickensRegistryItem redstoneAlloyChicken = null;
    public static ChickensRegistryItem conductiveIronChicken = null;
    public static ChickensRegistryItem pulsatingIronChicken = null;
    public static ChickensRegistryItem darkSteelChicken = null;
    public static ChickensRegistryItem endSteelChicken = null;
    public static ChickensRegistryItem soulariumChicken = null;

    public EnderIOChickens() {
        super("EnderIO", "EnderIO", "textures/entity/chicken/enderio/");
        setNeedsModPresent(true);
        this.setStartID(300);
    }

    @Override
    public List<ChickensRegistryItem> registerChickens(List<ChickensRegistryItem> allChickens) {

        electricalSteelChicken = addChicken(
            allChickens,
            "ElectricalSteelChicken",
            this.nextID(),
            "ElectricalSteelChicken.png",
            this.getFirstOreDictionary("ingotElectricalSteel"),
            0x939393,
            0x474747,
            SpawnType.NONE);

        energeticAlloyChicken = addChicken(
            allChickens,
            "EnergeticAlloyChicken",
            this.nextID(),
            "EnergeticAlloyChicken.png",
            this.getFirstOreDictionary("ingotEnergeticAlloy"),
            0xea6c05,
            0x65321b,
            SpawnType.NONE);

        vibrantAlloyChicken = addChicken(
            allChickens,
            "VibrantAlloyChicken",
            this.nextID(),
            "VibrantAlloyChicken.png",
            this.getFirstOreDictionary("ingotVibrantAlloy"),
            0xbcf239,
            0x779c1d,
            SpawnType.NONE);

        redstoneAlloyChicken = addChicken(
            allChickens,
            "RedstoneAlloyChicken",
            this.nextID(),
            "RedstoneAlloyChicken.png",
            this.getFirstOreDictionary("ingotRedstoneAlloy"),
            0xd03939,
            0x621919,
            SpawnType.NONE);

        conductiveIronChicken = addChicken(
            allChickens,
            "ConductiveIronChicken",
            this.nextID(),
            "ConductiveIronChicken.png",
            this.getFirstOreDictionary("ingotConductiveIron"),
            0xCC9D96,
            0x7E6764,
            SpawnType.NONE);

        pulsatingIronChicken = addChicken(
            allChickens,
            "PulsatingIronChicken",
            this.nextID(),
            "PulsatingIronChicken.png",
            this.getFirstOreDictionary("ingotPulsatingIron"),
            0x6FE78B,
            0x406448,
            SpawnType.NONE);

        darkSteelChicken = addChicken(
            allChickens,
            "DarkSteelChicken",
            this.nextID(),
            "DarkSteelChicken.png",
            this.getFirstOreDictionary("ingotDarkSteel"),
            0x4D4D4E,
            0x242424,
            SpawnType.NONE);

        endSteelChicken = addChicken(
            allChickens,
            "EndSteelChicken",
            this.nextID(),
            "EndSteelChicken.png",
            this.getFirstOreDictionary("ingotEndSteel"),
            0x6F6935,
            0xF5EFBB,
            SpawnType.NONE);

        soulariumChicken = addChicken(
            allChickens,
            "SoulariumChicken",
            this.nextID(),
            "SoulariumChicken.png",
            this.getFirstOreDictionary("ingotSoularium"),
            0x6F5C36,
            0x4E371A,
            SpawnType.NONE);

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
