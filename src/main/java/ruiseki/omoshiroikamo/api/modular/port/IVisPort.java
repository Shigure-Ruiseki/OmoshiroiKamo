package ruiseki.omoshiroikamo.api.modular.port;

/**
 * Exposes capacity information for vis ports.
 * Implemented by AbstractVisPortTE; used by VisOutput
 * to determine per-aspect vis capacity without a direct api → machinery dependency.
 */
public interface IVisPort {

    int getMaxVisPerAspect();
}
