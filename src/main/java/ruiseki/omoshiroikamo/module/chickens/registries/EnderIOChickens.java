package ruiseki.omoshiroikamo.module.chickens.registries;

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
            "ElectricalSteelChicken",
            0x939393,
            0x474747,
            SpawnType.NONE).setLayString("ore:ingotElectricalSteel");
        allChickens.add(electricalSteelChicken);

        energeticAlloyChicken = addChicken(
            "EnergeticAlloyChicken",
            this.nextID(),
            "EnergeticAlloyChicken",
            0xea6c05,
            0x65321b,
            SpawnType.NONE).setLayString("ore:ingotEnergeticAlloy");
        allChickens.add(energeticAlloyChicken);

        vibrantAlloyChicken = addChicken(
            "VibrantAlloyChicken",
            this.nextID(),
            "VibrantAlloyChicken",
            0xbcf239,
            0x779c1d,
            SpawnType.NONE).setLayString("ore:ingotVibrantAlloy");
        allChickens.add(vibrantAlloyChicken);

        redstoneAlloyChicken = addChicken(
            "RedstoneAlloyChicken",
            this.nextID(),
            "RedstoneAlloyChicken",
            0xd03939,
            0x621919,
            SpawnType.NONE).setLayString("ore:ingotRedstoneAlloy");
        allChickens.add(redstoneAlloyChicken);

        conductiveIronChicken = addChicken(
            "ConductiveIronChicken",
            this.nextID(),
            "ConductiveIronChicken",
            0xCC9D96,
            0x7E6764,
            SpawnType.NONE).setLayString("ore:ingotConductiveIron");
        allChickens.add(conductiveIronChicken);

        pulsatingIronChicken = addChicken(
            "PulsatingIronChicken",
            this.nextID(),
            "PulsatingIronChicken",
            0x6FE78B,
            0x406448,
            SpawnType.NONE).setLayString("ore:ingotPulsatingIron");
        allChickens.add(pulsatingIronChicken);

        darkSteelChicken = addChicken(
            "DarkSteelChicken",
            this.nextID(),
            "DarkSteelChicken",
            0x4D4D4E,
            0x242424,
            SpawnType.NONE).setLayString("ore:ingotDarkSteel");
        allChickens.add(darkSteelChicken);

        endSteelChicken = addChicken(
            "EndSteelChicken",
            this.nextID(),
            "EndSteelChicken",
            0x6F6935,
            0xF5EFBB,
            SpawnType.NONE).setLayString("ore:ingotEndSteel");
        allChickens.add(endSteelChicken);

        soulariumChicken = addChicken(
            "SoulariumChicken",
            this.nextID(),
            "SoulariumChicken",
            0x6F5C36,
            0x4E371A,
            SpawnType.NONE).setLayString("ore:ingotSoularium");
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
