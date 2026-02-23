package ruiseki.omoshiroikamo.core.energy.capability;

public interface EnergySource {

    int extract(int amount, boolean simulate);

    boolean canConnect();
}
