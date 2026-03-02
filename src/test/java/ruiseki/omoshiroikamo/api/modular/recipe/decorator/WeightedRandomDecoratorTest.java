package ruiseki.omoshiroikamo.api.modular.recipe.decorator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;

import ruiseki.omoshiroikamo.api.condition.ConditionContext;
import ruiseki.omoshiroikamo.api.condition.ICondition;
import ruiseki.omoshiroikamo.api.enums.EnumIO;
import ruiseki.omoshiroikamo.api.modular.IModularPort;
import ruiseki.omoshiroikamo.api.modular.IPortType;
import ruiseki.omoshiroikamo.api.modular.recipe.core.IModularRecipe;
import ruiseki.omoshiroikamo.api.modular.recipe.io.IRecipeInput;
import ruiseki.omoshiroikamo.api.modular.recipe.io.IRecipeOutput;
import ruiseki.omoshiroikamo.api.modular.recipe.visitor.IRecipeVisitor;
import ruiseki.omoshiroikamo.core.datastructure.BlockPos;
import ruiseki.omoshiroikamo.core.datastructure.DimPos;

/**
 * WeightedRandomDecorator の統計的な検証テスト
 */
public class WeightedRandomDecoratorTest {

    @Test
    @DisplayName("重み付きランダム出力の統計的検証 (1000回試行)")
    public void testWeightedDistribution() {
        StubOutput outputA = new StubOutput();
        StubOutput outputB = new StubOutput();

        List<WeightedRandomDecorator.WeightedOutputEntry> pool = new ArrayList<>();
        pool.add(new WeightedRandomDecorator.WeightedOutputEntry(outputA, 10));
        pool.add(new WeightedRandomDecorator.WeightedOutputEntry(outputB, 90));

        WeightedRandomDecorator decorator = new WeightedRandomDecorator(new StubRecipe(), pool, 1);

        int trials = 1000;
        List<IModularPort> ports = new ArrayList<>();
        ports.add(new StubPort());

        for (int i = 0; i < trials; i++) {
            decorator.processOutputs(ports, false);
        }

        assertTrue(outputA.applyCount >= 50 && outputA.applyCount <= 150, "A picked " + outputA.applyCount);
        assertTrue(outputB.applyCount >= 850 && outputB.applyCount <= 950, "B picked " + outputB.applyCount);
    }

    private static class StubOutput implements IRecipeOutput {

        public int applyCount = 0;

        @Override
        public IPortType.Type getPortType() {
            return IPortType.Type.ITEM;
        }

        @Override
        public long getRequiredAmount() {
            return 1;
        }

        @Override
        public boolean checkCapacity(List<IModularPort> ports) {
            return true;
        }

        @Override
        public void apply(List<IModularPort> ports) {
            applyCount++;
        }

        @Override
        public void accept(IRecipeVisitor visitor) {}

        @Override
        public IRecipeOutput copy() {
            return this;
        }

        @Override
        public void writeToNBT(NBTTagCompound nbt) {}

        @Override
        public void readFromNBT(NBTTagCompound nbt) {}

        @Override
        public void write(JsonObject json) {}

        @Override
        public void read(JsonObject json) {}
    }

    private static class StubPort implements IModularPort {

        @Override
        public IPortType.Type getPortType() {
            return IPortType.Type.ITEM;
        }

        @Override
        public void accept(IRecipeVisitor visitor) {}

        @Override
        public IPortType.Direction getPortDirection() {
            return IPortType.Direction.BOTH;
        }

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

        @Override
        public BlockPos getPos() {
            return new BlockPos(0, 0, 0);
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
    }

    private static class StubRecipe implements IModularRecipe {

        @Override
        public boolean processOutputs(List<IModularPort> outputPorts, boolean simulate) {
            return true;
        }

        @Override
        public String getRegistryName() {
            return "stub";
        }

        @Override
        public String getRecipeGroup() {
            return "stub";
        }

        @Override
        public String getName() {
            return "stub";
        }

        @Override
        public int getDuration() {
            return 100;
        }

        @Override
        public int getPriority() {
            return 0;
        }

        @Override
        public List<IRecipeInput> getInputs() {
            return Collections.emptyList();
        }

        @Override
        public List<IRecipeOutput> getOutputs() {
            return Collections.emptyList();
        }

        @Override
        public List<ICondition> getConditions() {
            return Collections.emptyList();
        }

        @Override
        public boolean isConditionMet(ConditionContext context) {
            return true;
        }

        @Override
        public boolean processInputs(List<IModularPort> inputPorts, boolean simulate) {
            return true;
        }

        @Override
        public boolean matchesInput(List<IModularPort> inputPorts) {
            return true;
        }

        @Override
        public boolean canOutput(List<IModularPort> outputPorts) {
            return true;
        }

        @Override
        public IPortType.Type checkOutputCapacity(List<IModularPort> outputPorts) {
            return null;
        }

        @Override
        public void onTick(ConditionContext context) {}

        @Override
        public void accept(IRecipeVisitor visitor) {}
    }
}
