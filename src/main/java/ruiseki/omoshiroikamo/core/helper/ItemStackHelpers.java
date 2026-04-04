package ruiseki.omoshiroikamo.core.helper;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.GameData;
import ruiseki.omoshiroikamo.core.datastructure.BlockPos;
import ruiseki.omoshiroikamo.core.inventory.PlayerExtendedInventoryIterator;

/**
 * Contains helper methods for various itemstack specific things.
 *
 * @author rubensworks
 */
public final class ItemStackHelpers {

    private static final Random RANDOM = new Random();

    /**
     * Get the tag compound from an item safely.
     * If it does not exist yet, it will create and save a new tag compound.
     *
     * @param itemStack The item to get the tag compound from.
     * @return The tag compound.
     */
    public static NBTTagCompound getSafeTagCompound(ItemStack itemStack) {
        if (!itemStack.hasTagCompound()) {
            itemStack.setTagCompound(new NBTTagCompound());
        }
        return itemStack.getTagCompound();
    }

    /**
     * Spawn an itemstack into the world.
     *
     * @param world     The world
     * @param pos       The position
     * @param itemStack the item stack
     */
    public static void spawnItemStack(World world, BlockPos pos, ItemStack itemStack) {
        spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
    }

    /**
     * Spawn an itemstack into the world.
     *
     * @param world     The world
     * @param x         X
     * @param y         Y
     * @param z         Z
     * @param itemStack the item stack
     */
    public static void spawnItemStack(World world, double x, double y, double z, ItemStack itemStack) {
        float offsetX = RANDOM.nextFloat() * 0.8F + 0.1F;
        float offsetY = RANDOM.nextFloat() * 0.8F + 0.1F;
        float offsetZ = RANDOM.nextFloat() * 0.8F + 0.1F;

        while (itemStack.stackSize > 0) {
            int i = RANDOM.nextInt(21) + 10;

            if (i > itemStack.stackSize) {
                i = itemStack.stackSize;
            }

            itemStack.stackSize -= i;
            EntityItem entityitem = new EntityItem(
                world,
                x + (double) offsetX,
                y + (double) offsetY,
                z + (double) offsetZ,
                new ItemStack(itemStack.getItem(), i, itemStack.getItemDamage()));

            if (itemStack.hasTagCompound()) {
                entityitem.getEntityItem()
                    .setTagCompound(
                        (NBTTagCompound) itemStack.getTagCompound()
                            .copy());
            }

            float motion = 0.05F;
            entityitem.motionX = RANDOM.nextGaussian() * (double) motion;
            entityitem.motionY = RANDOM.nextGaussian() * (double) motion + 0.2D;
            entityitem.motionZ = RANDOM.nextGaussian() * (double) motion;
            world.spawnEntityInWorld(entityitem);
        }
    }

    /**
     * Spawn an entity in the direction of a player without setting a pickup delay.
     *
     * @param world  The world
     * @param pos    The position to spawn an
     * @param stack  The stack to spawn
     * @param player The player to direct the motion to
     */
    public static void spawnItemStackToPlayer(World world, BlockPos pos, ItemStack stack, EntityPlayer player) {
        if (!world.isRemote) {
            float f = 0.5F;

            double xo = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double yo = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double zo = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(
                world,
                (double) pos.getX() + xo,
                (double) pos.getY() + yo,
                (double) pos.getZ() + zo,
                stack);

            double d0 = 8.0D;
            double d1 = (player.posX - entityitem.posX) / d0;
            double d2 = (player.posY + (double) player.getEyeHeight() - entityitem.posY) / d0;
            double d3 = (player.posZ - entityitem.posZ) / d0;

            entityitem.motionX += d1;
            entityitem.motionY += d2;
            entityitem.motionZ += d3;

            entityitem.delayBeforeCanPickup = 0;
            world.spawnEntityInWorld(entityitem);
        }
    }

    /**
     * Check if the given player has at least one of the given item.
     *
     * @param player The player.
     * @param item   The item to search in the inventory.
     * @return If the player has the item.
     */
    public static boolean hasPlayerItem(EntityPlayer player, Item item) {
        for (PlayerExtendedInventoryIterator it = new PlayerExtendedInventoryIterator(player); it.hasNext();) {
            ItemStack itemStack = it.next();
            if (itemStack != null && itemStack.getItem() == item) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get a list of variants from the given stack if its damage value is the wildcard value,
     * otherwise the list will only contain the given itemstack.
     *
     * @param itemStack The itemstack
     * @return The list of variants.
     */
    public static List<ItemStack> getVariants(ItemStack itemStack) {
        List<ItemStack> output = Lists.newLinkedList();
        if (itemStack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
            itemStack.getItem()
                .getSubItems(itemStack.getItem(), null, output);
        } else {
            output.add(itemStack);
        }
        return output;
    }

    /**
     * Parse a string to an itemstack.
     * Expects the format "domain:itemname:amount:meta"
     * The domain and itemname are mandatory, the rest is optional.
     *
     * @param itemStackString The string to parse.
     * @return The itemstack.
     * @throws IllegalArgumentException If the string was incorrectly formatted.
     */
    public static ItemStack parseItemStack(String itemStackString) {
        String[] split = itemStackString.split(":");
        String itemName = split[0] + ":" + split[1];
        Item item = GameData.getItemRegistry()
            .getObject(itemName);
        if (item == null) {
            throw new IllegalArgumentException("Invalid ItemStack item: " + itemName);
        }
        int amount = 1;
        int meta = 0;
        if (split.length > 2) {
            try {
                amount = Integer.parseInt(split[2]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid ItemStack amount: " + split[2]);
            }
            if (split.length > 3) {
                try {
                    meta = Integer.parseInt(split[3]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid ItemStack meta: " + split[3]);
                }
            }
        }
        return new ItemStack(item, amount, meta);
    }

    /**
     * If the given itemstacks are completely identical, including their stack size.
     *
     * @param a The first itemstack.
     * @param b The second itemstack.
     * @return If they are completely equal.
     */
    public static boolean areItemStacksIdentical(ItemStack a, ItemStack b) {
        return ItemStack.areItemStacksEqual(a, b)
            && ((a == null && b == null) || (a != null && a.stackSize == b.stackSize));
    }

    public static boolean areItemsEqualIgnoreDurability(ItemStack stack1, ItemStack stack2) {
        if (stack1 != null && stack2 != null) {
            return stack1.getItem() != stack2.getItem() ? false
                : Objects.equals(stack1.getTagCompound(), stack2.getTagCompound());
        } else {
            return false;
        }
    }

    public static int getStackMeta(ItemStack stack) {
        return stack.getItemDamage();
    }

    public static boolean isStackEmpty(ItemStack stack) {
        return stack == null || stack.getItem() == null || stack.stackSize <= 0;
    }

    public static boolean isStackInvalid(ItemStack stack) {
        return stack == null || stack.getItem() == null || stack.stackSize < 0;
    }

    public static boolean areStacksEqual(ItemStack stack1, ItemStack stack2) {
        return areStacksEqual(stack1, stack2, false);
    }

    public static boolean areStacksEqual(ItemStack stack1, ItemStack stack2, boolean ignoreNBT) {
        return stack1 != null && stack2 != null
            && stack1.getItem() == stack2.getItem()
            && doStackMetasMatch(getStackMeta(stack1), getStackMeta(stack2))
            && (ignoreNBT || Objects.equals(stack1.getTagCompound(), stack2.getTagCompound()));
    }

    public static boolean doStackMetasMatch(int meta1, int meta2) {
        if (meta1 == 32767) {
            return true;
        } else if (meta2 == 32767) {
            return true;
        } else {
            return meta1 == meta2;
        }
    }

    public static boolean areStackMergable(ItemStack s1, ItemStack s2) {
        if (s1 != null && s2 != null && s1.isStackable() && s2.isStackable()) {
            return !s1.isItemEqual(s2) ? false : areStacksEqual(s1, s2);
        } else {
            return false;
        }
    }

    public static NBTTagCompound saveAllSlotsExtended(NBTTagCompound nbt, List<ItemStack> inventory) {
        NBTTagList list = new NBTTagList();

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);

            if (stack != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("Slot", i);
                writeToNBTExtended(stack, tag);
                list.appendTag(tag);
            }
        }

        nbt.setTag("Items", list);
        return nbt;
    }

    public static NBTTagCompound writeToNBTExtended(ItemStack stack, NBTTagCompound nbt) {
        stack.writeToNBT(nbt);
        nbt.setInteger("Count", stack.stackSize);
        return nbt;
    }

    public static void loadAllItemsExtended(NBTTagCompound nbt, List<ItemStack> inventory) {
        NBTTagList list = nbt.getTagList("Items", 10);

        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            int j;
            if (tag.hasKey("Slot", 3)) {
                j = tag.getInteger("Slot"); // since 1.1.5
            } else if (tag.hasKey("Slot", 1)) {
                j = tag.getByte("Slot") & 255; // pre 1.1.5
            } else {
                j = 0; // fallback
            }

            if (j < inventory.size()) {
                inventory.set(j, loadItemStackExtended(tag));
            }
        }
    }

    public static ItemStack loadItemStackExtended(NBTTagCompound nbt) {
        ItemStack stack = ItemStack.loadItemStackFromNBT(nbt);
        if (stack != null) {
            stack.stackSize = nbt.getInteger("Count");
        }
        return stack;
    }
}
