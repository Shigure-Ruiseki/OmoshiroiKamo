package ruiseki.omoshiroikamo.api.recipe.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.recipe.core.ITieredMachine;
import ruiseki.omoshiroikamo.api.recipe.core.ModularRecipe;
import ruiseki.omoshiroikamo.api.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.datastructure.BlockPos;
import ruiseki.omoshiroikamo.core.datastructure.DimPos;

/**
 * Tier システムの統合テスト
 * 
 * マシン（コンポーネントごとの Tier）とレシピ（要求 Tier）の
 * 相互作用が正しく動作することを検証します（仕様の決定打）。
 */
@DisplayName("Tier システム統合テスト")
public class TierSystemIntegrationTest {

    /**
     * テスト用のスタブマシンの実装
     */
    private static class StubMachinePort implements IModularPort, ITieredMachine {

        private final int casingTier;
        private final int coreTier;

        public StubMachinePort(int casing, int core) {
            this.casingTier = casing;
            this.coreTier = core;
        }

        @Override
        public IPortType.Type getPortType() {
            return IPortType.Type.ITEM;
        }

        @Override
        public IPortType.Direction getPortDirection() {
            return IPortType.Direction.BOTH;
        }

        @Override
        public int getComponentTier(String componentName) {
            if ("casing".equals(componentName)) return casingTier;
            if ("core".equals(componentName)) return coreTier;
            return 0;
        }

        @Override
        public double getSpeedMultiplier() {
            return 1.0;
        }

        @Override
        public double getEnergyMultiplier() {
            return 1.0;
        }

        @Override
        public int getBatchMin() {
            return 1;
        }

        @Override
        public int getBatchMax() {
            return 1;
        }

        @Override
        public int getTier() {
            return Math.min(casingTier, coreTier);
        }

        @Override
        public void setTier(int tier) {
            // No-op for test stub
        }

        @Override
        public void accept(IRecipeVisitor visitor) {}

        @Override
        public EnumIO getSideIO(ForgeDirection side) {
            return EnumIO.BOTH;
        }

        @Override
        public void setSideIO(ForgeDirection side, EnumIO state) {}

        @Override
        public IIcon getTexture(ForgeDirection side, int renderPass) {
            return null;
        }

        // ITile methods
        @Override
        public BlockPos getPos() {
            return null;
        }

        @Override
        public DimPos getDimPos() {
            return null;
        }

        @Override
        public World getWorld() {
            return null;
        }

        @Override
        public int getX() {
            return 0;
        }

        @Override
        public int getY() {
            return 0;
        }

        @Override
        public int getZ() {
            return 0;
        }

        @Override
        public void mark() {}

        @Override
        public int getWorldID() {
            return 0;
        }

        @Override
        public TileEntity getTile() {
            return null;
        }

        @Override
        public int getMeta() {
            return 0;
        }

        @Override
        public void updateTEState() {}

        @Override
        public void updateTELight() {}

        @Override
        public Block getBlock() {
            return null;
        }

        public boolean isEnabled() {
            return true;
        }

        public void update() {}
    }

    @Test
    @DisplayName("全てのコンポーネントが要求 Tier を満たしていれば実行可能")
    public void testTierRequirementMet() {
        ModularRecipe recipe = ModularRecipe.builder()
            .registryName("tiered_recipe")
            .recipeGroup("test")
            .addRequiredComponentTier("casing", 2)
            .addRequiredComponentTier("core", 1)
            .build();

        // Casing Tier 2, Core Tier 1 のマシン
        StubMachinePort machine = new StubMachinePort(2, 1);
        List<IModularPort> ports = new ArrayList<>();
        ports.add(machine);

        assertTrue(recipe.matchesInput(ports), "要求 Tier を満たしている場合は実行可能なはず");
    }

    @Test
    @DisplayName("一つのコンポーネントでも Tier が不足していれば実行不可")
    public void testTierRequirementNotMet() {
        ModularRecipe recipe = ModularRecipe.builder()
            .registryName("tiered_recipe")
            .recipeGroup("test")
            .addRequiredComponentTier("casing", 3) // 3が必要
            .build();

        // Casing Tier 2 のマシン（不足）
        StubMachinePort machine = new StubMachinePort(2, 1);
        List<IModularPort> ports = new ArrayList<>();
        ports.add(machine);

        assertFalse(recipe.matchesInput(ports), "要求 Tier が不足している場合は実行できないはず");
    }

    @Test
    @DisplayName("要求 Tier が設定されていない場合は、どんな Tier のマシンでも実行可能")
    public void testNoTierRequirement() {
        ModularRecipe recipe = ModularRecipe.builder()
            .registryName("no_tier_recipe")
            .recipeGroup("test")
            .build();

        // どんな Tier でもOK
        StubMachinePort weakMachine = new StubMachinePort(0, 0);
        List<IModularPort> ports = new ArrayList<>();
        ports.add(weakMachine);

        assertTrue(recipe.matchesInput(ports), "要求がない場合は誰でも実行可能なはず");
    }
}
