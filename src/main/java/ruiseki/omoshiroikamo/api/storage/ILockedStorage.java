package ruiseki.omoshiroikamo.api.storage;

public interface ILockedStorage {

    boolean isSlotLocked(int slotIndex);

    void setSlotLocked(int slotIndex, boolean locked);
}
