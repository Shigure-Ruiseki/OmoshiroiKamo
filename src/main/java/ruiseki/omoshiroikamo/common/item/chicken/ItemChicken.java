package ruiseki.omoshiroikamo.common.item.chicken;

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
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.DataChicken;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.util.BlockPos;
import ruiseki.omoshiroikamo.common.util.TooltipUtils;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.config.backport.ChickenConfig;
import ruiseki.omoshiroikamo.plugin.ModCompatInformation;

public class ItemChicken extends ItemOK {

    private final Map<Integer, IIcon> icons = new HashMap<>();

    public ItemChicken() {
        super(ModObject.itemChicken);
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
            list.add(new ItemStack(this, 1, chicken.getType()));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack) {
        DataChicken chicken = DataChicken.getDataFromStack(stack);
        if (chicken == null) {
            return super.getItemStackDisplayName(stack);
        }
        return LibMisc.LANG.localize(
            chicken.getItems()
                .getDisplayName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {

        for (DataChicken chicken : DataChicken.getAllChickens()) {
            int type = chicken.getType();
            IIcon icon = reg.registerIcon(LibResources.PREFIX_MOD + "chicken/" + chicken.getName());
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
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            BlockPos pos = correctPosition(new BlockPos(x, y, z, world), side);
            activate(stack, pos);
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

        return new BlockPos(posX, posY, posZ, pos.world);
    }

    private void activate(ItemStack stack, BlockPos pos) {
        DataChicken chicken = DataChicken.getDataFromStack(stack);
        if (chicken == null) {
            return;
        }
        chicken.spawnEntity(pos);
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        DataChicken chicken = DataChicken.getDataFromStack(stack);
        if (chicken != null) {
            NBTTagCompound tag = chicken.createTagCompound();
            tag.setInteger("Type", chicken.getType());
            stack.setTagCompound(tag);
        }
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer player, List<String> list, boolean flag) {
        DataChicken chicken = DataChicken.getDataFromStack(itemstack);
        if (chicken == null) {
            return;
        }

        ItemStack layItem = chicken.getItems()
            .createLayItem();
        SpawnType spawnType = chicken.getItems()
            .getSpawnType();

        TooltipUtils builder = TooltipUtils.builder();

        // Tier
        builder.addLang(
            LibResources.TOOLTIP + "spawn_egg.tier",
            chicken.getItems()
                .getTier());

        builder.addAll(chicken.getStatsInfoTooltip());

        // Lay item
        builder.addLangIf(
            layItem != null && layItem.getItem() != null,
            LibResources.TOOLTIP + "spawn_egg.layitem",
            layItem.getDisplayName());
        builder.addLangIf(layItem == null || layItem.getItem() == null, LibResources.TOOLTIP + "spawn_egg.nolayitem");

        // Spawn type (chỉ hiển thị nếu khác NONE)
        EnumChatFormatting labelColor = EnumChatFormatting.GRAY;
        EnumChatFormatting valueColor;

        if (spawnType == SpawnType.NORMAL) {
            valueColor = EnumChatFormatting.GREEN;
        } else if (spawnType == SpawnType.HELL) {
            valueColor = EnumChatFormatting.RED;
        } else if (spawnType == SpawnType.SNOW) {
            valueColor = EnumChatFormatting.AQUA;
        } else {
            valueColor = EnumChatFormatting.WHITE;
        }

        builder.addLabelWithLangValue(
            LibResources.TOOLTIP + "spawn_egg.spawnType",
            labelColor,
            spawnType.toString(),
            valueColor);

        // Not breedable
        builder.addColoredLangIf(
            !chicken.getItems()
                .isBreedable(),
            EnumChatFormatting.RED,
            LibResources.TOOLTIP + "spawn_egg.notbreedable");

        // Breedable with parents
        if (chicken.getItems()
            .isBreedable()
            && chicken.getItems()
                .getParent1() != null
            && chicken.getItems()
                .getParent2() != null) {
            String parent1 = new ChatComponentTranslation(
                chicken.getItems()
                    .getParent1()
                    .getDisplayName())
                .getFormattedText();
            String parent2 = new ChatComponentTranslation(
                chicken.getItems()
                    .getParent2()
                    .getDisplayName())
                .getFormattedText();

            builder.addLabelWithValue(
                new ChatComponentTranslation(LibResources.TOOLTIP + "spawn_egg.breedable").getFormattedText(),
                EnumChatFormatting.YELLOW,
                parent1 + " & " + parent2,
                EnumChatFormatting.GOLD);
        }

        // Mod compat tooltips
        if (ModCompatInformation.TOOLTIP.containsKey(
            chicken.getItems()
                .getId())) {
            ModCompatInformation info = ModCompatInformation.TOOLTIP.get(
                chicken.getItems()
                    .getId());
            builder.addAll(info.getToolTip());
        }

        list.addAll(builder.build());
    }

}
