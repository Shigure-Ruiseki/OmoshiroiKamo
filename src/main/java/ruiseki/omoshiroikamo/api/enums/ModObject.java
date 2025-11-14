package ruiseki.omoshiroikamo.api.enums;

import net.minecraft.block.Block;

import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

public enum ModObject {

    blockTest,
    blockMultiblock,
    blockBoiler,
    blockFluidInOut,
    blockItemInOut,
    blockEnergyInOut,
    blockElectrolyzer,

    blockQuantumOreExtractor,
    blockQuantumResExtractor,
    blockQuantumBeacon,
    blockLaserCore,
    blockLens,
    blockColoredLens,
    blockSolarArray,
    blockSolarCell,
    blockStructureFrame,
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

    blockBlockMaterial,
    blockConnectable,
    blockAnvil,
    blockFurnace,

    blockRoost,
    blockBreeder,
    blockRoostCollector,

    itemBackPack,
    itemUpgrade,
    itemStackUpgrade,
    itemCraftingUpgrade,
    itemMagnetUpgrade,
    itemFeedingUpgrade,
    itemBatteryUpgrade,
    itemEverlastingUpgrade,
    itemLightUpgrade,

    itemStabilizedEnderPear,
    itemPhotovoltaicCell,
    itemAssembler,

    itemChickenCatcher,
    itemChickenSpawnEgg,
    itemChicken,
    itemColoredEgg,
    itemLiquidEgg,
    itemAnalyzer,
    itemSolidXp,
    itemCowSpawnEgg,
    itemItemMaterial,
    itemBucketMaterial,
    itemBucketFluid,
    itemOre,
    itemHammer,
    itemWireCoil;

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

    public void setBlock(Block block) {
        this.blockInstance = block;
    }

    public Block getBlock() {
        return this.blockInstance;
    }
}
