// package ruiseki.omoshiroikamo.module.ids.common.network;
//
// import java.util.List;
//
// import net.minecraft.item.ItemStack;
//
// import lombok.Data;
// import lombok.EqualsAndHashCode;
// import ruiseki.omoshiroikamo.api.block.DimPos;
// import ruiseki.omoshiroikamo.api.ids.network.ConsumingNetworkElementBase;
// import ruiseki.omoshiroikamo.api.ids.network.INetworkElement;
// import ruiseki.omoshiroikamo.api.ids.network.IPartNetwork;
// import ruiseki.omoshiroikamo.api.util.InventoryHelpers;
// import ruiseki.omoshiroikamo.api.util.TileHelpers;
//
/// **
// * Network element for tile entities.
// *
// * @author rubensworks
// */
// @EqualsAndHashCode(callSuper = false)
// @Data
// public abstract class TileNetworkElement<T extends TileCableConnectableInventory>
// extends ConsumingNetworkElementBase<IPartNetwork> {
//
// private final DimPos pos;
//
// protected abstract Class<T> getTileClass();
//
// protected T getTile() {
// return TileHelpers.getSafeTile(getPos().getWorld(), getPos().getBlockPos(), getTileClass());
// }
//
// @Override
// public void addDrops(List<ItemStack> itemStacks, boolean dropMainElement) {
// T tile = getTile();
// if (tile != null) {
// InventoryHelpers.dropInventoryItems(getPos().getWorld(), getPos().getBlockPos(), tile.getInventory());
// }
// }
//
// @Override
// public int compareTo(INetworkElement o) {
// if (o instanceof TileNetworkElement) {
// return getPos().compareTo(((TileNetworkElement) o).getPos());
// }
// return Integer.compare(hashCode(), o.hashCode());
// }
//
// @Override
// public void afterNetworkReAlive(IPartNetwork network) {
// super.afterNetworkReAlive(network);
// getTile().afterNetworkReAlive();
// }
// }
