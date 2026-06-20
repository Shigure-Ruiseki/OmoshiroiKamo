package ruiseki.omoshiroikamo.api.modular.port;

/**
 * Exposes capacity information for essentia ports.
 * Implemented by AbstractEssentiaPortTE; used by EssentiaOutput
 * to determine per-aspect capacity without a direct api → machinery dependency.
 */
public interface IEssentiaPort {

    int getMaxCapacityPerAspect();
}
