package ruiseki.omoshiroikamo.module.multiblock.common.block.quantumBeacon;

import static com.gtnewhorizon.gtnhlib.client.model.ModelISBRH.JSON_ISBRH_ID;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.QuantumBeaconConfig;
import ruiseki.omoshiroikamo.config.backport.QuantumBeaconConfig.BeaconTierConfig;
import ruiseki.omoshiroikamo.config.backport.QuantumBeaconConfig.BeaconTierRangeConfig;
import ruiseki.omoshiroikamo.core.common.block.ItemBlockOK;
import ruiseki.omoshiroikamo.core.common.block.abstractClass.AbstractTieredMBBlock;
import ruiseki.omoshiroikamo.core.lib.LibMisc;

public class BlockQuantumBeacon extends AbstractTieredMBBlock<TEQuantumBeacon> {

    protected BlockQuantumBeacon() {
        super(
            ModObject.blockQuantumBeacon.unlocalisedName,
            TEQuantumBeaconT1.class,
            TEQuantumBeaconT2.class,
            TEQuantumBeaconT3.class,
            TEQuantumBeaconT4.class,
            TEQuantumBeaconT5.class,
            TEQuantumBeaconT6.class);
        this.setLightLevel(1.0F);
    }

    public static BlockQuantumBeacon create() {
        return new BlockQuantumBeacon();
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
        list.add(new ItemStack(this, 1, 2));
        list.add(new ItemStack(this, 1, 3));
        list.add(new ItemStack(this, 1, 4));
        list.add(new ItemStack(this, 1, 5));
    }

    @Override
    public int getRenderType() {
        return JSON_ISBRH_ID;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockQuantumBeacon.class;
    }

    public static class ItemBlockQuantumBeacon extends ItemBlockOK {

        public ItemBlockQuantumBeacon(Block block) {
            super(block, block);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            int tier = stack.getItemDamage() + 1;
            return super.getUnlocalizedName() + ".tier_" + tier;
        }

        @Override
        @SideOnly(Side.CLIENT)
        @SuppressWarnings({ "unchecked", "rawtypes" })
        public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
            super.addInformation(stack, player, list, advanced);

            if (GuiScreen.isShiftKeyDown()) {
                BeaconTierConfig config = getTierConfig(stack.getItemDamage());
                list.add(EnumChatFormatting.GOLD + LibMisc.LANG.localize("tooltip.beacon.effect_list"));

                addEffectLine(list, "speed", config.speedLevel);
                addEffectLine(list, "haste", config.hasteLevel);
                addEffectLine(list, "strength", config.strengthLevel);
                addEffectLine(list, "resistance", config.resistanceLevel);
                addEffectLine(list, "regeneration", config.regenerationLevel);
                addEffectLine(list, "saturation", config.saturationLevel);
                addEffectLine(list, "jump_boost", config.jumpBoostLevel);
                addEffectLine(list, "night_vision", config.nightVisionLevel);
                addEffectLine(list, "water_breathing", config.waterBreathingLevel);
                addEffectLine(list, "fire_resistance", config.fireResistanceLevel);

            } else {
                // Range information
                int tier = stack.getItemDamage() + 1;
                BeaconTierRangeConfig rangeConfig = QuantumBeaconConfig.getRangeConfig(tier);
                list.add("");
                list.add(EnumChatFormatting.GOLD + LibMisc.LANG.localize("tooltip.beacon.range"));
                list.add(
                    "  " + EnumChatFormatting.AQUA
                        + LibMisc.LANG.localize("tooltip.beacon.range.horizontal")
                        + ": "
                        + EnumChatFormatting.WHITE
                        + rangeConfig.horizontalRange);
                list.add(
                    "  " + EnumChatFormatting.AQUA
                        + LibMisc.LANG.localize("tooltip.beacon.range.upward")
                        + ": "
                        + EnumChatFormatting.WHITE
                        + rangeConfig.upwardRange);
                list.add(
                    "  " + EnumChatFormatting.AQUA
                        + LibMisc.LANG.localize("tooltip.beacon.range.downward")
                        + ": "
                        + EnumChatFormatting.WHITE
                        + rangeConfig.downwardRange);
                list.add(EnumChatFormatting.GRAY + LibMisc.LANG.localize("tooltip.beacon.shift_for_effects"));
            }
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        private void addEffectLine(List list, String effectKey, int level) {
            if (level > 0) {
                String effectName = LibMisc.LANG.localize("tooltip.beacon.effect." + effectKey);
                list.add("  " + EnumChatFormatting.AQUA + effectName + ": " + EnumChatFormatting.WHITE + level);
            }
        }

        private BeaconTierConfig getTierConfig(int meta) {
            switch (meta) {
                case 0:
                    return QuantumBeaconConfig.tier1;
                case 1:
                    return QuantumBeaconConfig.tier2;
                case 2:
                    return QuantumBeaconConfig.tier3;
                case 3:
                    return QuantumBeaconConfig.tier4;
                case 4:
                    return QuantumBeaconConfig.tier5;
                case 5:
                    return QuantumBeaconConfig.tier6;
                default:
                    return QuantumBeaconConfig.tier1;
            }
        }
    }

}
