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

import com.gtnewhorizon.gtnhlib.client.model.color.BlockColor;
import com.gtnewhorizon.gtnhlib.client.model.color.IBlockColor;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import ruiseki.omoshiroikamo.api.modular.IModularBlockTint;
import ruiseki.omoshiroikamo.api.modular.ModularTier;
import ruiseki.omoshiroikamo.config.backport.MachineryConfig;
import ruiseki.omoshiroikamo.core.block.BlockOK;
import ruiseki.omoshiroikamo.core.helper.LangHelpers;
import ruiseki.omoshiroikamo.core.item.ItemBlockOK;
import ruiseki.omoshiroikamo.core.lib.LibResources;
import ruiseki.omoshiroikamo.module.machinery.common.tier.TierManager;
import ruiseki.omoshiroikamo.module.machinery.common.tile.StructureTintCache;

/**
 * Machine Casing block.
 * Block ID corresponds to Design (Plain, Reinforced, etc.)
 * Metadata corresponds to Tier (0: Basic, 1: Advanced, 2: Elite, 3: Ultimate)
 * TODO: Add crafting recipe
 */
public class BlockMachineCasing extends BlockOK implements IModularBlockTint, IBlockColor {

    private static final int TIERS = 16;

    private String designName = "";
    private final IIcon[] tierIcons = new IIcon[TIERS];

    protected BlockMachineCasing() {
        super("modularMachineCasing");
        setHardness(5.0F);
        setResistance(10.0F);
        this.hasSubtypes = false;
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

    public String getDesignName() {
        return designName;
    }

    @Override
    protected void registerBlockColor() {
        BlockColor.registerBlockColors(this, this);
    }

    @Override
    public int getRenderType() {
        return AbstractPortBlock.portRendererId;
    }

    @Override
    public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
        Integer structureColor = StructureTintCache.get(world, x, y, z);
        if (structureColor != null) {
            return structureColor;
        }
        return MachineryConfig.getDefaultTintColorInt();
    }

    @Override
    public int colorMultiplier(IBlockAccess world, int x, int y, int z, int tintIndex) {
        if (tintIndex == 0) {
            Integer structureColor = StructureTintCache.get(world, x, y, z);
            if (structureColor != null) {
                return structureColor & 0xFFFFFF;
            }
            return MachineryConfig.getDefaultTintColorInt() & 0xFFFFFF;
        }
        return 0xFFFFFF;
    }

    @Override
    public int colorMultiplier(ItemStack stack, int tintIndex) {
        if (tintIndex == 0) {
            return MachineryConfig.getDefaultTintColorInt() & 0xFFFFFF;
        }
        return 0xFFFFFF;
    }

    @Override
    public int getRenderColor(int meta) {
        return MachineryConfig.getDefaultTintColorInt();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        // JSON model rendering handles textures via blockstates JSON,
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
            String tier = LangHelpers.localize("machinery.tier." + modularTier.getUnlocalizedName());

            // Handle backward compatibility: MACHINE_CASING has no design name
            if (block.designName.isEmpty()) {
                String casingName = LangHelpers.localize("machinery.casing.modular");
                return LangHelpers.localize("machinery.casing.format", tier, casingName);
            }

            String design = LangHelpers.localize("machinery.design." + block.designName);
            return LangHelpers.localize("machinery.casing.format", tier, design);
        }

        @Override
        public String getUnlocalizedName(ItemStack stack) {
            return super.getUnlocalizedName() + ".tier_" + stack.getItemDamage();
        }
    }
}
