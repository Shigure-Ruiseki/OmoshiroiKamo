package ruiseki.omoshiroikamo.module.machinery.common.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.modular.IModularBlockTint;
import ruiseki.omoshiroikamo.api.modular.ModularTier;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.block.BlockOK;
import ruiseki.omoshiroikamo.core.item.ItemBlockOK;
import ruiseki.omoshiroikamo.core.lib.LibMisc;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.machinery.common.tile.StructureTintCache;
import ruiseki.omoshiroikamo.module.machinery.common.tier.TierManager;

/**
 * Machine Casing block.
 * Block ID corresponds to Design (Plain, Reinforced, etc.)
 * Metadata corresponds to Tier (0: Basic, 1: Advanced, 2: Elite, 3: Ultimate)
 * TODO: Add crafting recipe
 */
public class BlockMachineCasing extends BlockOK implements IModularBlockTint {

    private static final int TIERS = 16;

    private String designName="";
    private final IIcon[] tierIcons = new IIcon[TIERS];

    protected BlockMachineCasing() {
        super("modularMachineCasing");
        setHardness(5.0F);
        setResistance(10.0F);
    }

    public static BlockMachineCasing create() {
        return new BlockMachineCasing();
    }

    protected BlockMachineCasing(String designName) {
        super("casing_" + designName);
        this.designName = designName;
        setHardness(5.0F);
        setResistance(10.0F);
        this.hasSubtypes = true;
    }

    public static BlockMachineCasing create(String designName) {
        return new BlockMachineCasing(designName);
    }

    @Override
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        // Get color from cache
        Integer structureColor = StructureTintCache.get(world, x, y, z);
        if (structureColor != null) {
            return structureColor;
        }
        // Fall back to config color
        return MachineryConfig.getDefaultTintColorInt();
    }

    @Override
    public int getRenderColor(int meta) {
        return MachineryConfig.getDefaultTintColorInt();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        // Casing blocks always use ModelISBRH.JSON_ISBRH_ID via getRenderType,
        // so we don't register standard icons via super.
        // Instead, we register tier-based base textures for use in JSON models
        for (int i = 0; i < TIERS; i++) {
            tierIcons[i] = reg.registerIcon(LibResources.PREFIX_MOD + "modular/tier_" + i + "_base");
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (meta < 0 || meta >= tierIcons.length || tierIcons[meta] == null) {
            return null;
        }
        return tierIcons[meta];
    }

    @Override
    public String getTextureName() {
        if (designName == "") {
            // Backward compatibility
            return "modular_machine_casing";
        }
        return "modular/casing_" + designName;
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    protected Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockMachineCasing.class;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        // Only show enabled tiers in creative tab (default: 6)
        int enabledTiers = TierManager.getEnabledTierCount();
        for (int i = 0; i < enabledTiers && i < TIERS; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    public static class ItemBlockMachineCasing extends ItemBlockOK {

        public ItemBlockMachineCasing(Block block) {
            super(block);
        }

        @Override
        public String getItemStackDisplayName(ItemStack stack) {
            BlockMachineCasing block = (BlockMachineCasing) field_150939_a;
            int meta = stack.getItemDamage();
            ModularTier modularTier = ModularTier.fromMeta(meta);

            // Format: %s (Tier) %s (Design)
            // e.g. "Revolutionary" "Machine Casing"
            String tier = LibMisc.LANG.localizeExact("machinery.tier." + modularTier.getUnlocalizedName());

            // Handle backward compatibility: MACHINE_CASING has no design name
            if (block.designName.isEmpty()) {
                String casingName = LibMisc.LANG.localizeExact("machinery.casing.modular");
                return LibMisc.LANG.localizeExact("machinery.casing.format", tier, casingName);
            }

            String design = LibMisc.LANG.localizeExact("machinery.design." + block.designName);
            return LibMisc.LANG.localizeExact("machinery.casing.format", tier, design);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            return super.getUnlocalizedName() + ".tier_" + stack.getItemDamage();
        }
    }
}
