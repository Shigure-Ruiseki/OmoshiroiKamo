package ruiseki.omoshiroikamo.api.enums;

import net.minecraft.block.Block;

import ruiseki.omoshiroikamo.core.lib.LibMisc;

public enum ModObject {

    // spotless: off

    blockBlockCrystal,
    blockQuantumOreExtractor,
    blockQuantumResExtractor,
    blockQuantumBeacon,
    blockLaserCore,
    blockLens,
    blockColoredLens,
    blockSolarArray,
    blockSolarCell,
    blockStructureFrame,
    blockBasaltStructure,
    blockHardenedStructure,
    blockAlabasterStructure,
    blockMachineBase,
    blockModifierNull,
    blockModifierAccuracy,
    blockModifierPiezo,
    blockModifierSpeed,
    blockModifierFlight,
    blockModifierNightVision,
    blockModifierHaste,
    blockModifierStrength,
    blockModifierWaterBreathing,
    blockModifierRegeneration,
    blockModifierSaturation,
    blockModifierResistance,
    blockModifierJumpBoost,
    blockModifierFireResistance,
    blockBasalt,
    blockAlabaster,
    blockHardenedStone,
    blockMica,
    backpackLeather,
    blockBackpackIron,
    blockBackpackGold,
    blockBackpackDiamond,
    blockBackpackObsidian,

    blockStall,
    blockRoost,
    blockBreeder,
    blockRoostCollector,

    blockLootFabricator,
    blockSimulationChamber,
    blockMachineCasing,

    blockModularItemInput,
    blockModularItemOutput,
    blockModularItemOutputME,
    blockModularEnergyInput,
    blockModularEnergyOutput,
    blockModularFluidInput,
    blockModularFluidOutput,
    blockModularFluidOutputME,
    blockModularManaInput,
    blockModularManaOutput,
    blockModularGasInput,
    blockModularGasOutput,
    blockModularEssentiaInput,
    blockModularEssentiaOutput,
    blockModularEssentiaInputME,
    blockModularVisInput,
    blockModularVisOutput,

    blockVisBridge,

    blockCable,
    blockProgrammer,
    blockMenrilSapling,
    blockMenrilLog,
    blockMenrilLeaves,
    blockMenrilDoor,
    blockMenrilPlanks,

    itemBackPack,
    itemUpgrade,
    itemStackUpgrade,
    itemCraftingUpgrade,
    itemMagnetUpgrade,
    itemAdvancedMagnetUpgrade,
    itemFeedingUpgrade,
    itemAdvancedFeedingUpgrade,
    itemPickupUpgrade,
    itemAdvancedPickupUpgrade,
    itemBatteryUpgrade,
    itemEverlastingUpgrade,
    itemInceptionUpgrade,
    itemFilterUpgrade,
    itemAdvancedFilterUpgrade,
    itemVoidUpgrade,
    itemAdvancedVoidUpgrade,

    itemCrystal,
    itemStabilizedEnderPear,
    itemPhotovoltaicCell,
    itemAssembler,

    itemCowHalter,
    itemChickenCatcher,
    itemChickenSpawnEgg,
    itemChicken,
    itemColoredEgg,
    itemLiquidEgg,
    itemEmptyEgg,
    itemAnalyzer,
    itemSolidXp,
    itemChickenFood,
    itemCowSpawnEgg,

    itemCreativeModelLearner,
    itemDeepLearner,
    itemDataModel,
    itemDataModelBlank,
    itemPristineMatter,
    itemLivingMatter,
    itemPolymerClay,
    itemSootCoveredPlate,
    itemSootCoveredRedstone,

    itemStructureWand,

    itemWrench,

    itemVariableCard,
    itemMenrilBerries,

    itemEnergyInterface,
    itemEnergyFilterInterface,
    itemEnergyImporter,
    itemEnergyExporter,

    itemItemInterface,
    itemItemFilterInterface,
    itemItemImporter,
    itemItemExporter,

    itemCraftingInterface,

    itemRedstoneReader,
    itemBlockReader,
    itemFluidReader,
    itemInventoryReader,

    itemRedstoneWriter,

    itemStorageTerminal,;
    // spotless: on

    public final String unlocalisedName;
    private Block blockInstance;

    ModObject() {
        String raw = name();

        if (raw.startsWith("block")) {
            raw = raw.substring(5);
        } else if (raw.startsWith("item")) {
            raw = raw.substring(4);
        }

        this.unlocalisedName = Character.toLowerCase(raw.charAt(0)) + raw.substring(1);
    }

    public String getRegistryName() {
        return LibMisc.MOD_ID + ":" + unlocalisedName;
    }
}
