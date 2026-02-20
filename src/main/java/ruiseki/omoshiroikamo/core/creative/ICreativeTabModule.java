package ruiseki.omoshiroikamo.core.creative;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public interface ICreativeTabModule {

    void fillTab(CreativeTabs tab, List<ItemStack> list);
}
