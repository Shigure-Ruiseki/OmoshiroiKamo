package ruiseki.omoshiroikamo.core.energy.capability;

public interface EnergySink {

    int insert(int amount, boolean simulate);

    boolean canConnect();
}
