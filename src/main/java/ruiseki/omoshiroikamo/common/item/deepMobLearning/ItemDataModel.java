package ruiseki.omoshiroikamo.common.item.deepMobLearning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.entity.model.DataModel;
import ruiseki.omoshiroikamo.api.entity.model.DataModelExperience;
import ruiseki.omoshiroikamo.api.entity.model.ModelRegistry;
import ruiseki.omoshiroikamo.api.entity.model.ModelRegistryItem;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.util.KeyboardUtils;
import ruiseki.omoshiroikamo.common.util.TooltipUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;

public class ItemDataModel extends ItemOK {

    private final Map<Integer, IIcon> icons = new HashMap<>();

    public ItemDataModel() {
        super(ModObject.itemDataModel.unlocalisedName);
        setMaxStackSize(1);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
        for (ModelRegistryItem model : ModelRegistry.INSTANCE.getItems()) {
            list.add(new ItemStack(this, 1, model.getId()));
        }
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        ModelRegistryItem model = DataModel.getDataFromStack(stack);
        if (model != null) {
            NBTTagCompound tag = DataModel.createTagCompound(stack);
            stack.setTagCompound(tag);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack) {
        ModelRegistryItem model = ModelRegistry.INSTANCE.getByType(stack.getItemDamage());
        if (model == null) {
            return super.getItemStackDisplayName(stack);
        }
        return LibMisc.LANG.localize(model.getItemName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        for (ModelRegistryItem model : ModelRegistry.INSTANCE.getItems()) {
            int type = model.getId();

            ResourceLocation tex = model.getTexture();
            String path = tex.getResourcePath();
            String iconName = tex.getResourceDomain() + ":" + path;

            IIcon icon = reg.registerIcon(iconName);
            icons.put(type, icon);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        IIcon icon = icons.get(meta);
        if (icon == null) {
            icon = icons.get(0);
        }
        return icon;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
        TooltipUtils builder = TooltipUtils.builder();

        if (DataModel.hasExtraTooltip(stack)) {
            builder.add(DataModel.getExtraTooltip(stack));
        }

        if (!KeyboardUtils.isHoldingShift()) {
            builder.addLang("tooltip.holdshift");
        } else {
            int tier = DataModel.getTier(stack);
            builder.addLang("tooltip.data_model.tier", LibMisc.LANG.localize(DataModelExperience.getTierName(tier)));
            if (tier != DataModelExperience.getMaxTier()) {
                builder.addLang(
                    "tooltip.data_model.data.collected",
                    DataModel.getCurrentTierSimulationCountWithKills(stack),
                    DataModel.getTierRoof(stack));
                builder.addLang("tooltip.data_model.data.killmultiplier", DataModel.getKillMultiplier(stack));
            }
            builder.addLang(LibMisc.LANG.localize("data_model.rfcost", DataModel.getSimulationTickCost(stack)));
            // list.add(LibMisc.LANG.localize("data_model.type", DataModel.getMatterTypeName(stack)));
        }

        list.addAll(builder.build());
    }
}
