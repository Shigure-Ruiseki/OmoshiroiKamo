package ruiseki.omoshiroikamo.api.modular.port;

/**
 * Exposes slot range information for item ports.
 * Implemented by AbstractItemIOPortTE; used by ItemInput and ItemOutput
 * to determine valid slot ranges without a direct api → machinery dependency.
 */
public interface IItemPort {

    int getMinItemInput();

    int getMaxItemInput();

    int getMinItemOutput();

    int getMaxItemOutput();
}
