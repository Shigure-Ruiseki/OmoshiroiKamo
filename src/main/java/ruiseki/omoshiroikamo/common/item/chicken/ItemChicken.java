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

import com.enderio.core.common.util.BlockCoord;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.entity.SpawnType;
import ruiseki.omoshiroikamo.api.entity.chicken.DataChicken;
import ruiseki.omoshiroikamo.api.enums.ModObject;
import ruiseki.omoshiroikamo.common.item.ItemOK;
import ruiseki.omoshiroikamo.common.util.lib.LibMisc;
import ruiseki.omoshiroikamo.common.util.lib.LibResources;
import ruiseki.omoshiroikamo.plugin.ModCompatInformation;

public class ItemChicken extends ItemOK {

    @SideOnly(Side.CLIENT)
    private Map<Integer, IIcon> icons = new HashMap<>();

    public ItemChicken() {
        super(ModObject.itemChicken);
        setHasSubtypes(true);
        setMaxStackSize(16);
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
            BlockCoord blockCoord = correctPosition(new BlockCoord(x, y, z), side);
            activate(stack, world, blockCoord);
            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
        }
        return true;
    }

    private static BlockCoord correctPosition(BlockCoord pos, int side) {
        final int[] offsetsXForSide = new int[] { 0, 0, 0, 0, -1, 1 };
        final int[] offsetsYForSide = new int[] { -1, 1, 0, 0, 0, 0 };
        final int[] offsetsZForSide = new int[] { 0, 0, -1, 1, 0, 0 };

        int posX = pos.x + offsetsXForSide[side];
        int posY = pos.y + offsetsYForSide[side];
        int posZ = pos.z + offsetsZForSide[side];

        return new BlockCoord(posX, posY, posZ);
    }

    private void activate(ItemStack stack, World worldIn, BlockCoord pos) {
        DataChicken chicken = DataChicken.getDataFromStack(stack);
        if (chicken == null) {
            return;
        }
        chicken.spawnEntity(worldIn, pos);
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
    public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
        DataChicken chicken = DataChicken.getDataFromStack(itemstack);

        if (chicken == null) {
            return;
        }

        list.add(
            new ChatComponentTranslation(
                LibResources.TOOLTIP + "spawn_egg.tier",
                chicken.getItems()
                    .getTier()).getFormattedText());
        ItemStack layitem = chicken.getItems()
            .createLayItem();

        chicken.addStatsInfoToTooltip(list);

        if (layitem != null && layitem.getItem() != null) {
            list.add(
                new ChatComponentTranslation(LibResources.TOOLTIP + "spawn_egg.layitem", layitem.getDisplayName())
                    .getFormattedText());
        } else {
            list.add(
                new ChatComponentTranslation(LibResources.TOOLTIP + "spawn_egg.nolayitem", layitem.getDisplayName())
                    .getFormattedText());
        }

        if (chicken.getItems()
            .getSpawnType() != SpawnType.NONE) {
            EnumChatFormatting format = chicken.getItems()
                .getSpawnType() == SpawnType.NORMAL ? EnumChatFormatting.GREEN
                    : chicken.getItems()
                        .getSpawnType() == SpawnType.HELL ? EnumChatFormatting.RED
                            : chicken.getItems()
                                .getSpawnType() == SpawnType.SNOW ? EnumChatFormatting.AQUA : EnumChatFormatting.WHITE;
            list.add(
                new ChatComponentTranslation(LibResources.TOOLTIP + "spawn_egg.spawnType").getFormattedText() + ": "
                    + EnumChatFormatting.RESET
                    + format
                    + chicken.getItems()
                        .getSpawnType()
                        .toString());
        }

        if (!chicken.getItems()
            .isBreedable()) {
            list.add(
                EnumChatFormatting.RED
                    + new ChatComponentTranslation(LibResources.TOOLTIP + "spawn_egg.notbreedable").getFormattedText());
        } else {
            if (chicken.getItems()
                .getParent1() != null
                && chicken.getItems()
                    .getParent2() != null) {
                String parent1 = new ChatComponentTranslation(
                    chicken.getItems()
                        .getParent1()
                        .getDisplayName()).getFormattedText();

                String parent2 = new ChatComponentTranslation(
                    chicken.getItems()
                        .getParent2()
                        .getDisplayName()).getFormattedText();

                list.add(
                    EnumChatFormatting.YELLOW
                        + new ChatComponentTranslation(LibResources.TOOLTIP + "spawn_egg.breedable").getFormattedText()
                        + ": "
                        + EnumChatFormatting.RESET
                        + EnumChatFormatting.ITALIC
                        + EnumChatFormatting.GOLD
                        + parent1
                        + EnumChatFormatting.RESET
                        + " & "
                        + EnumChatFormatting.ITALIC
                        + EnumChatFormatting.GOLD
                        + parent2);
            }

        }

        if (ModCompatInformation.TOOLTIP.containsKey(chicken.getType())) {
            ModCompatInformation info = ModCompatInformation.TOOLTIP.get(chicken.getType());
            list.addAll(info.getToolTip());
        }
    }
}
