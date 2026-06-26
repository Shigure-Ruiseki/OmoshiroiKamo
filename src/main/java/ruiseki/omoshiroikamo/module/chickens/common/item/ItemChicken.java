package ruiseki.omoshiroikamo.module.chickens.common.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.okcore.datastructure.BlockPos;
import ruiseki.okcore.helper.LangHelpers;
import ruiseki.okcore.item.ItemOK;
import ruiseki.omoshiroikamo.Reference;
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.ChickensRegistryItem;
import ruiseki.omoshiroikamo.api.entity.chicken.DataChicken;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.config.backport.ChickenConfig;
import ruiseki.omoshiroikamo.core.compat.ModCompatInformation;

public class ItemChicken extends ItemOK {

    private final Map<Integer, IIcon> icons = new HashMap<>();
    private final Map<Integer, IIcon> overlayIcons = new HashMap<>();

    public ItemChicken() {
        super(ModObject.CHICKEN.name);
        setHasSubtypes(true);
        setMaxStackSize(64);
    }

    @Override
    public int getItemStackLimit() {
        return ChickenConfig.getChickenStackLimit();
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> list) {
        for (DataChicken chicken : DataChicken.getAllChickens()) {
            list.add(new ItemStack(this, 1, chicken.getId()));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack) {
        DataChicken chicken = DataChicken.getDataFromStack(stack);
        if (chicken == null) {
            return super.getItemStackDisplayName(stack);
        }
        return LangHelpers.localize(
            chicken.getItem()
                .getDisplayName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderPasses(int metadata) {
        return overlayIcons.containsKey(metadata) ? 2 : 1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        for (DataChicken chicken : DataChicken.getAllChickens()) {
            int type = chicken.getId();
            ChickensRegistryItem item = chicken.getItem();

            String iconName = item.getIconName();

            IIcon icon = reg.registerIcon(iconName);
            icons.put(type, icon);

            String overlayName = item.getIconOverlayName();
            if (overlayName != null) {
                IIcon overlayIcon = reg.registerIcon(overlayName);
                overlayIcons.put(type, overlayIcon);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int meta, int pass) {
        if (pass == 1) {
            return overlayIcons.get(meta);
        }
        return icons.get(meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int meta) {
        return getIconFromDamageForRenderPass(meta, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
        DataChicken chicken = DataChicken.getDataFromStack(stack);
        if (chicken != null) {
            ChickensRegistryItem item = chicken.getItem();
            if (pass == 0) {
                return item.getTintColor();
            } else {
                return 0xFFFFFF;
            }
        }
        return super.getColorFromItemStack(stack, pass);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            BlockPos pos = correctPosition(new BlockPos(x, y, z), side);
            activate(stack, pos, world);
            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
        }
        return true;
    }

    private static BlockPos correctPosition(BlockPos pos, int side) {
        final int[] offsetsXForSide = new int[] { 0, 0, 0, 0, -1, 1 };
        final int[] offsetsYForSide = new int[] { -1, 1, 0, 0, 0, 0 };
        final int[] offsetsZForSide = new int[] { 0, 0, -1, 1, 0, 0 };

        int posX = pos.x + offsetsXForSide[side];
        int posY = pos.y + offsetsYForSide[side];
        int posZ = pos.z + offsetsZForSide[side];

        return new BlockPos(posX, posY, posZ);
    }

    private void activate(ItemStack stack, BlockPos pos, World world) {
        DataChicken chicken = DataChicken.getDataFromStack(stack);
        if (chicken == null) {
            return;
        }
        chicken.spawnEntity(pos, world);
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        DataChicken chicken = DataChicken.getDataFromStack(stack);
        if (chicken != null) {
            NBTTagCompound tag = chicken.createTagCompound();
            tag.setInteger("Type", chicken.getId());
            stack.setTagCompound(tag);
        }
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer player, List<String> list, boolean flag) {
        DataChicken chicken = DataChicken.getDataFromStack(itemstack);
        if (chicken == null) {
            return;
        }

        ItemStack layItem = chicken.getItem()
            .createLayItem();
        SpawnType spawnType = chicken.getItem()
            .getSpawnType();

        list.add(
            LangHelpers.localize(
                Reference.TOOLTIP + "spawn_egg.tier",
                chicken.getItem()
                    .getTier()));

        if (chicken.getStatsInfoTooltip() != null) {
            list.addAll(chicken.getStatsInfoTooltip());
        }

        if (layItem != null && layItem.getItem() != null) {
            list.add(LangHelpers.localize(Reference.TOOLTIP + "spawn_egg.layitem", layItem.getDisplayName()));
        } else {
            list.add(LangHelpers.localize(Reference.TOOLTIP + "spawn_egg.nolayitem"));
        }

        EnumChatFormatting valueColor;
        switch (spawnType) {
            case NORMAL:
                valueColor = EnumChatFormatting.GREEN;
                break;
            case HELL:
                valueColor = EnumChatFormatting.RED;
                break;
            case SNOW:
                valueColor = EnumChatFormatting.AQUA;
                break;
            default:
                valueColor = EnumChatFormatting.WHITE;
                break;
        }

        EnumChatFormatting labelColor = EnumChatFormatting.GRAY;
        String labelKey = Reference.TOOLTIP + "spawn_egg.spawnType";
        String label = new ChatComponentTranslation(labelKey).getFormattedText();
        list.add(labelColor + label + ": " + valueColor + String.valueOf(spawnType) + EnumChatFormatting.RESET);

        if (!chicken.getItem()
            .isBreedable()) {
            list.add(EnumChatFormatting.RED + LangHelpers.localize(Reference.TOOLTIP + "spawn_egg.notbreedable"));
        }

        if (chicken.getItem()
            .isBreedable()
            && chicken.getItem()
                .getParent1() != null
            && chicken.getItem()
                .getParent2() != null) {
            String parent1 = new ChatComponentTranslation(
                chicken.getItem()
                    .getParent1()
                    .getDisplayName()).getFormattedText();
            String parent2 = new ChatComponentTranslation(
                chicken.getItem()
                    .getParent2()
                    .getDisplayName()).getFormattedText();

            String breedableLabel = new ChatComponentTranslation(Reference.TOOLTIP + "spawn_egg.breedable")
                .getFormattedText();

            list.add(
                EnumChatFormatting.YELLOW + breedableLabel
                    + ": "
                    + EnumChatFormatting.GOLD
                    + parent1
                    + " & "
                    + parent2
                    + EnumChatFormatting.RESET);
        }

        if (ModCompatInformation.TOOLTIP.containsKey(
            chicken.getItem()
                .getId())) {
            ModCompatInformation info = ModCompatInformation.TOOLTIP.get(
                chicken.getItem()
                    .getId());
            if (info != null && info.getToolTip() != null) {
                list.addAll(info.getToolTip());
            }
        }
    }

}
