package ruiseki.omoshiroikamo.module.ids.common.item;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.IDsConfig;
import ruiseki.omoshiroikamo.core.item.ItemFoodOK;

public class ItemMenrilBerries extends ItemFoodOK {

    public ItemMenrilBerries() {
        super(ModObject.itemMenrilBerries.unlocalisedName, 4, 0.3f, false);
        if (IDsConfig.nightVision) {
            setPotionEffect(Potion.nightVision.id, 20, 1, 1.0F);
        }
        setTextureName("ids/menril_berries");
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 10;
    }
}
