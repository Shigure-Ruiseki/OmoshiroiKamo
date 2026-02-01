package ruiseki.omoshiroikamo.api.block;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class BlockStack {

    private Block block;
    private int meta;

    public BlockStack(Block block, int meta) {
        this.block = block;
        this.meta = meta;
    }

    public BlockStack(Block block) {
        this.block = block;
        this.meta = 0;
    }

    private BlockStack() {}

    public Block getBlock() {
        return block;
    }

    public int getMeta() {
        return meta;
    }

    public boolean isEmpty() {
        return block == null;
    }

    public int getBlockId() {
        return block != null ? Block.getIdFromBlock(block) : -1;
    }

    public Item getItem() {
        return block != null ? Item.getItemFromBlock(block) : null;
    }

    public ItemStack toItemStack(int amount) {
        Item item = getItem();
        if (item == null) return null;
        return new ItemStack(item, amount, meta);
    }

    public ItemStack toItemStack() {
        return toItemStack(1);
    }

    public FluidStack toFluidStack(int amount) {
        Fluid fluid = FluidRegistry.lookupFluidForBlock(getBlock());
        if (fluid == null) return null;
        return new FluidStack(fluid, amount);
    }

    public FluidStack toFluidStack() {
        return toFluidStack(1000);
    }

    public static BlockStack fromWorld(World world, int x, int y, int z) {
        if (world == null) return empty();

        Block block = world.getBlock(x, y, z);
        if (block == null) return empty();

        int meta = world.getBlockMetadata(x, y, z);
        return new BlockStack(block, meta);
    }

    public static BlockStack fromItemStack(ItemStack stack) {
        if (stack == null) return empty();

        Block block = Block.getBlockFromItem(stack.getItem());
        if (block == null) return empty();

        return new BlockStack(block, stack.getItemDamage());
    }

    public static BlockStack empty() {
        return new BlockStack(null, 0);
    }

    public boolean matches(BlockStack other) {
        if (other == null) return false;
        if (this.block != other.block) return false;
        return this.meta == other.meta;
    }

    public boolean matchesIgnoreMeta(BlockStack other) {
        if (other == null) return false;
        return this.block == other.block;
    }

    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();

        if (block != null) {
            String name = Block.blockRegistry.getNameForObject(block);
            if (name != null) {
                tag.setString("block", name);
                tag.setInteger("meta", meta);
            }
        }

        return tag;
    }

    public static BlockStack deserializeNBT(NBTTagCompound tag) {
        if (tag == null || !tag.hasKey("block")) return BlockStack.empty();

        Block block = (Block) Block.blockRegistry.getObject(tag.getString("block"));
        if (block == null) return BlockStack.empty();

        int meta = tag.hasKey("meta") ? tag.getInteger("meta") : 0;
        return new BlockStack(block, meta);
    }

    public String getRegistryName() {
        if (block == null) return "";
        String name = Block.blockRegistry.getNameForObject(block);
        return name != null ? name : "";
    }

    public String getDisplayName() {
        if (block == null) return "null";

        Item item = Item.getItemFromBlock(block);
        if (item == null) return getRegistryName();

        ItemStack stack = new ItemStack(item, 1, meta);
        return stack.getDisplayName();
    }

    @Override
    public String toString() {
        if (block == null) return "null";

        String name = Block.blockRegistry.getNameForObject(block);
        return (name != null ? name : "unknown") + ":" + meta;
    }

    @Override
    public int hashCode() {
        int result = block != null ? block.hashCode() : 0;
        result = 31 * result + meta;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BlockStack other)) return false;
        return this.block == other.block && this.meta == other.meta;
    }
}
