// package ruiseki.omoshiroikamo.module.ids.common.network;
//
// import java.util.Collections;
// import java.util.Iterator;
// import java.util.List;
// import java.util.Map;
//
// import net.minecraft.block.Block;
// import net.minecraft.world.World;
//
// import org.jetbrains.annotations.Nullable;
//
// import com.google.common.base.Function;
// import com.google.common.collect.ImmutableList;
// import com.google.common.collect.Iterables;
// import com.google.common.collect.Lists;
// import com.google.common.collect.Maps;
//
// import ruiseki.omoshiroikamo.api.block.BlockPos;
// import ruiseki.omoshiroikamo.api.block.DimPos;
// import ruiseki.omoshiroikamo.api.datastructure.CompositeMap;
// import ruiseki.omoshiroikamo.api.ids.block.IEnergyBattery;
// import ruiseki.omoshiroikamo.api.ids.block.IEnergyBatteryFacade;
// import ruiseki.omoshiroikamo.api.ids.block.IVariableContainerFacade;
// import ruiseki.omoshiroikamo.api.ids.block.cable.ICable;
// import ruiseki.omoshiroikamo.api.ids.evaluate.variable.IValue;
// import ruiseki.omoshiroikamo.api.ids.evaluate.variable.IVariable;
// import ruiseki.omoshiroikamo.api.ids.item.IVariableFacade;
// import ruiseki.omoshiroikamo.api.ids.network.IEnergyConsumingNetworkElement;
// import ruiseki.omoshiroikamo.api.ids.network.IEnergyNetwork;
// import ruiseki.omoshiroikamo.api.ids.network.INetwork;
// import ruiseki.omoshiroikamo.api.ids.network.INetworkElement;
// import ruiseki.omoshiroikamo.api.ids.network.IPartNetwork;
// import ruiseki.omoshiroikamo.api.ids.part.IPartContainer;
// import ruiseki.omoshiroikamo.api.ids.part.IPartState;
// import ruiseki.omoshiroikamo.api.ids.part.IPartType;
// import ruiseki.omoshiroikamo.api.ids.part.PartPos;
// import ruiseki.omoshiroikamo.api.ids.part.PartTarget;
// import ruiseki.omoshiroikamo.api.ids.part.aspect.IAspectRead;
// import ruiseki.omoshiroikamo.api.ids.path.ICablePathElement;
// import ruiseki.omoshiroikamo.core.common.util.Logger;
// import ruiseki.omoshiroikamo.core.lib.LibMisc;
// import ruiseki.omoshiroikamo.module.ids.common.path.Cluster;
// import ruiseki.omoshiroikamo.module.ids.common.path.PathFinder;
// import ruiseki.omoshiroikamo.module.ids.common.persist.world.NetworkWorldStorage;
//
/// **
// * A network that can hold parts.
// * Note that this network only contains references to the relevant data, it does not contain the actual information.
// *
// * @author rubensworks
// */
// public class PartNetwork extends Network<IPartNetwork> implements IPartNetwork, IEnergyNetwork {
//
// private Map<Integer, PartPos> partPositions;
// private List<DimPos> variableContainerPositions;
// private Map<Integer, IVariableFacade> compositeVariableCache;
// private Map<Integer, IValue> lazyExpressionValueCache;
// private Map<DimPos, IEnergyBatteryFacade> energyBatteryPositions;
// private Map<Integer, DimPos> proxyPositions;
//
// private volatile boolean partsChanged = false;
//
// /**
// * This constructor should not be called, except for the process of constructing networks from NBT.
// */
// public PartNetwork() {
// super();
// }
//
// /**
// * Create a new network from a given cluster of cables.
// * Each cable will be checked if it is an instance of {@link INetworkElementProvider} and will add all its
// * elements to the network in that case.
// * Each cable that is an instance of {@link IPartContainerFacade}
// * will have the network stored in its part container.
// *
// * @param cables The cables that make up the connections in the network which can potentially provide network
// * elements.
// */
// public PartNetwork(Cluster<ICablePathElement> cables) {
// super(cables);
// }
//
// @Override
// protected void onConstruct() {
// super.onConstruct();
// partPositions = Maps.newHashMap();
// variableContainerPositions = Lists.newLinkedList();
// compositeVariableCache = null;
// lazyExpressionValueCache = Maps.newHashMap();
// energyBatteryPositions = Maps.newHashMap();
// proxyPositions = Maps.newHashMap();
// }
//
// @Override
// public boolean addPart(int partId, PartPos partPos) {
// if (partPositions.containsKey(partId)) {
// return false;
// }
// partPositions.put(partId, partPos);
// return true;
// }
//
// @Override
// public IPartState getPartState(int partId) {
// PartPos partPos = partPositions.get(partId);
// return TileMultipartTicking.get(partPos.getPos())
// .getPartState(partPos.getSide());
// }
//
// @Override
// public IPartType getPartType(int partId) {
// PartPos partPos = partPositions.get(partId);
// return TileMultipartTicking.get(partPos.getPos())
// .getPart(partPos.getSide());
// }
//
// @Override
// public void removePart(int partId) {
// partPositions.remove(partId);
// }
//
// @Override
// public boolean hasPart(int partId) {
// if (!partPositions.containsKey(partId)) {
// return false;
// }
// PartPos partPos = partPositions.get(partId);
// IPartContainer partContainer = TileMultipartTicking.get(partPos.getPos());
// return partContainer != null && partContainer.hasPart(partPos.getSide());
// }
//
// @Override
// public <V extends IValue> boolean hasPartVariable(int partId, IAspectRead<V, ?> aspect) {
// if (!hasPart(partId)) {
// return false;
// }
// IPartState partState = getPartState(partId);
// if (!(partState instanceof IPartStateReader)) {
// return false;
// }
// IPartType partType = getPartType(partId);
// if (!(partType instanceof IPartTypeReader)) {
// return false;
// }
// return ((IPartTypeReader) getPartType(partId))
// .getVariable(PartTarget.fromCenter(partPositions.get(partId)), (IPartStateReader) partState, aspect)
// != null;
// }
//
// @Override
// public <V extends IValue> IVariable<V> getPartVariable(int partId, IAspectRead<V, ?> aspect) {
// return ((IPartStateReader) getPartState(partId)).getVariable(aspect);
// }
//
// protected Map<Integer, IVariableFacade> getVariableCache() {
// if (compositeVariableCache == null) {
// // Create a new composite map view on the existing variable containers in this network.
// CompositeMap<Integer, IVariableFacade> compositeMap = new CompositeMap<>();
// for (Iterator<DimPos> it = variableContainerPositions.iterator(); it.hasNext();) {
// DimPos dimPos = it.next();
// World world = dimPos.getWorld();
// BlockPos pos = dimPos.getBlockPos();
// Block block = pos.getBlock(world);
// if (block instanceof IVariableContainerFacade) {
// compositeMap.addElement(
// ((IVariableContainerFacade) block).getVariableContainer(world, pos)
// .getVariableCache());
// } else {
// Logger.error("The variable container at " + dimPos + " was invalid, skipping.");
// it.remove();
// }
// }
// compositeVariableCache = compositeMap;
// }
// return compositeVariableCache;
// }
//
// @Override
// public boolean hasVariableFacade(int variableId) {
// return getVariableCache().containsKey(variableId);
// }
//
// @Override
// public IVariableFacade getVariableFacade(int variableId) {
// return getVariableCache().get(variableId);
// }
//
// @Override
// public void setValue(int id, IValue value) {
// lazyExpressionValueCache.put(id, value);
// }
//
// @Override
// public boolean hasValue(int id) {
// return lazyExpressionValueCache.containsKey(id);
// }
//
// @Override
// public IValue getValue(int id) {
// return lazyExpressionValueCache.get(id);
// }
//
// @Override
// public boolean equals(Object object) {
// return object instanceof PartNetwork && areNetworksEqual(this, (PartNetwork) object);
// }
//
// @Override
// public boolean addVariableContainer(DimPos dimPos) {
// compositeVariableCache = null;
// return variableContainerPositions.add(dimPos);
// }
//
// @Override
// public void removeVariableContainer(DimPos dimPos) {
// compositeVariableCache = null;
// variableContainerPositions.remove(dimPos);
// }
//
// @Override
// public boolean addProxy(int proxyId, DimPos dimPos) {
// if (proxyPositions.containsKey(proxyId)) {
// return false;
// }
// proxyPositions.put(proxyId, dimPos);
// return true;
// }
//
// @Override
// public void removeProxy(int proxyId) {
// proxyPositions.remove(proxyId);
// }
//
// @Override
// public DimPos getProxy(int proxyId) {
// return proxyPositions.get(proxyId);
// }
//
// @Override
// public void notifyPartsChanged() {
// this.partsChanged = true;
// }
//
// private void onPartsChanged() {
// System.out.println("Parts of network " + this + " are changed.");
// }
//
// @Override
// protected boolean canUpdate(INetworkElement<IPartNetwork> element) {
// if (!super.canUpdate(element)) return false;
// if (!(element instanceof IEnergyConsumingNetworkElement)) return true;
// // TODO: Add GeneralConfig.energyConsumptionMultiplier;
// int multiplier = 1;
// if (multiplier == 0) return true;
// int consumptionRate = ((IEnergyConsumingNetworkElement) element).getConsumptionRate() * multiplier;
// return consume(consumptionRate, true) == consumptionRate;
// }
//
// @Override
// protected void onSkipUpdate(INetworkElement<IPartNetwork> element) {
// super.onSkipUpdate(element);
// if (element instanceof IEnergyConsumingNetworkElement) {
// ((IEnergyConsumingNetworkElement) element).postUpdate(this, false);
// }
// }
//
// @Override
// protected void postUpdate(INetworkElement<IPartNetwork> element) {
// super.postUpdate(element);
// if (element instanceof IEnergyConsumingNetworkElement) {
// // TODO: Add GeneralConfig.energyConsumptionMultiplier;
// int multiplier = 1;
// if (multiplier > 0) {
// int consumptionRate = ((IEnergyConsumingNetworkElement) element).getConsumptionRate() * multiplier;
// consume(consumptionRate, false);
// }
// ((IEnergyConsumingNetworkElement) element).postUpdate(this, true);
// }
// }
//
// @Override
// protected void onUpdate() {
// super.onUpdate();
// // Reset lazy variable cache
// lazyExpressionValueCache.clear();
//
// // Signal parts of any changes
// if (partsChanged) {
// this.partsChanged = false;
// onPartsChanged();
// }
// }
//
// @Override
// public boolean removeCable(ICable cable, ICablePathElement cablePathElement) {
// if (super.removeCable(cable, cablePathElement)) {
// notifyPartsChanged();
// return true;
// }
// return false;
// }
//
// /**
// * Initiate a full network from the given start position.
// *
// * @param connectable The cable to start the network from.
// * @param world The world.
// * @param pos The position.
// * @return The newly formed network.
// */
// public static PartNetwork initiateNetworkSetup(ICable<ICablePathElement> connectable, World world, BlockPos pos) {
// PartNetwork network = new PartNetwork(
// PathFinder.getConnectedCluster(connectable.createPathElement(world, pos)));
// NetworkWorldStorage.getInstance(LibMisc.MOD_ID)
// .addNewNetwork(network);
// return network;
// }
//
// protected synchronized List<IEnergyBattery> getMaterializedEnergyBatteries() {
// return ImmutableList.copyOf(
// Iterables.transform(
// energyBatteryPositions.entrySet(),
// new Function<Map.Entry<DimPos, IEnergyBatteryFacade>, IEnergyBattery>() {
//
// @Nullable
// @Override
// public IEnergyBattery apply(Map.Entry<DimPos, IEnergyBatteryFacade> input) {
// return input.getValue()
// .getEnergyBattery(
// input.getKey()
// .getWorld(),
// input.getKey()
// .getBlockPos());
// }
//
// @Override
// public boolean equals(@Nullable Object object) {
// return false;
// }
// }));
// }
//
// protected int addSafe(int a, int b) {
// int add = a + b;
// if (add < a || add < b) return Integer.MAX_VALUE;
// return add;
// }
//
// @Override
// public synchronized int getStoredEnergy() {
// int energy = 0;
// for (IEnergyBattery energyBattery : getMaterializedEnergyBatteries()) {
// energy = addSafe(energy, energyBattery.getStoredEnergy());
// }
// return energy;
// }
//
// @Override
// public synchronized int getMaxStoredEnergy() {
// int maxEnergy = 0;
// for (IEnergyBattery energyBattery : getMaterializedEnergyBatteries()) {
// maxEnergy = addSafe(maxEnergy, energyBattery.getMaxStoredEnergy());
// }
// return maxEnergy;
// }
//
// @Override
// public int addEnergy(int energy, boolean simulate) {
// int toAdd = energy;
// for (IEnergyBattery energyBattery : getMaterializedEnergyBatteries()) {
// int maxAdd = Math.min(energyBattery.getMaxStoredEnergy() - energyBattery.getStoredEnergy(), toAdd);
// if (maxAdd > 0) {
// energyBattery.addEnergy(maxAdd, simulate);
// }
// toAdd -= maxAdd;
// }
// return energy - toAdd;
// }
//
// @Override
// public synchronized int consume(int energy, boolean simulate) {
// int toConsume = energy;
// for (IEnergyBattery energyBattery : getMaterializedEnergyBatteries()) {
// int consume = Math.min(energyBattery.getStoredEnergy(), toConsume);
// if (consume > 0) {
// toConsume -= energyBattery.consume(consume, simulate);
// }
// }
// return energy - toConsume;
// }
//
// @Override
// public boolean addEnergyBattery(DimPos dimPos) {
// Block block = dimPos.getBlockPos()
// .getBlock(dimPos.getWorld());
// if (block instanceof IEnergyBatteryFacade) {
// return energyBatteryPositions.put(dimPos, (IEnergyBatteryFacade) block) == null;
// }
// return false;
// }
//
// @Override
// public void removeEnergyBattery(DimPos pos) {
// energyBatteryPositions.remove(pos);
// }
//
// @Override
// public Map<DimPos, IEnergyBatteryFacade> getEnergyBatteries() {
// return Collections.unmodifiableMap(energyBatteryPositions);
// }
//
// @Override
// public int getConsumptionRate() {
// // TODO: Add GeneralConfig.energyConsumptionMultiplier
// int multiplier = 1;
// if (multiplier == 0) return 0;
// int consumption = 0;
// for (INetworkElement element : getElements()) {
// consumption += ((IEnergyConsumingNetworkElement<INetwork>) element).getConsumptionRate() * multiplier;
// }
// return consumption;
// }
// }
