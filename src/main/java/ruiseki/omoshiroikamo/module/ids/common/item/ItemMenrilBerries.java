package ruiseki.omoshiroikamo.module.ids.common.item;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

import ruiseki.okcore.item.ItemFoodOK;
import ruiseki.omoshiroikamo.Reference;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.IDsConfig;

public class ItemMenrilBerries extends ItemFoodOK {

    public ItemMenrilBerries() {
        super(ModObject.MENRIL_BERRIES.name, 4, 0.3f, false);
        if (IDsConfig.nightVision) {
            setPotionEffect(Potion.nightVision.id, 20, 1, 1.0F);
        }
        setTextureName(Reference.PREFIX_MOD + "ids/menril_berries");
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 10;
    }
}
