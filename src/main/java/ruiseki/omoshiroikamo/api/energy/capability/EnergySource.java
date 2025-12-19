package ruiseki.omoshiroikamo.api.energy.capability;

public interface EnergySource {

    int extract(int amount, boolean simulate);

    boolean canConnect();
}
