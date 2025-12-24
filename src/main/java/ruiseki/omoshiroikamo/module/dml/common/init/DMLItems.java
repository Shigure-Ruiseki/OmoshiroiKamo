package ruiseki.omoshiroikamo.module.dml.common.init;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import lombok.Getter;
import ruiseki.omoshiroikamo.core.common.util.Logger;
import ruiseki.omoshiroikamo.module.dml.common.item.ItemCreativeModelLearner;
import ruiseki.omoshiroikamo.module.dml.common.item.ItemDataModel;
import ruiseki.omoshiroikamo.module.dml.common.item.ItemDataModelBlank;
import ruiseki.omoshiroikamo.module.dml.common.item.ItemLivingMatter;
import ruiseki.omoshiroikamo.module.dml.common.item.ItemPolymerClay;
import ruiseki.omoshiroikamo.module.dml.common.item.ItemPristineMatter;
import ruiseki.omoshiroikamo.module.dml.common.item.ItemSootCoveredPlate;
import ruiseki.omoshiroikamo.module.dml.common.item.ItemSootCoveredRedstone;
import ruiseki.omoshiroikamo.module.dml.common.item.deepLearner.ItemDeepLearner;

public enum DMLItems {

    // spotless: off

    DEEP_LEARNER(new ItemDeepLearner()),
    CREATIVE_MODEL_LEARNER(new ItemCreativeModelLearner()),
    DATA_MODEL(new ItemDataModel()),
    DATA_MODEL_BLANK(new ItemDataModelBlank()),
    PRISTINE_MATTER(new ItemPristineMatter()),
    LIVING_MATTER(new ItemLivingMatter()),
    POLYMER_CLAY(new ItemPolymerClay()),
    SOOT_COVERED_PLATE(new ItemSootCoveredPlate()),
    SOOT_COVERED_REDSTONE(new ItemSootCoveredRedstone()),

    //
    ;
    // spotless: on

    public static final DMLItems[] VALUES = values();

    public static void preInit() {
        for (DMLItems item : VALUES) {
            try {
                GameRegistry.registerItem(item.getItem(), item.getName());
                Logger.info("Successfully initialized " + item.name());
            } catch (Exception e) {
                Logger.error("Failed to initialize item: +" + item.name());
            }
        }
    }

    @Getter
    private final Item item;

    DMLItems(Item item) {
        this.item = item;
    }

    public String getName() {
        return getItem().getUnlocalizedName()
            .replace("item.", "");
    }

    public ItemStack newItemStack() {
        return newItemStack(1);
    }

    public ItemStack newItemStack(int count) {
        return newItemStack(count, 0);
    }

    public ItemStack newItemStack(int count, int meta) {
        return new ItemStack(this.getItem(), count, meta);
    }
}
