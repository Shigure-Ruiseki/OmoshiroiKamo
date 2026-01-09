package ruiseki.omoshiroikamo.api.cable;

public interface ICablePartItem {

    ICablePart createPart();

    Class<? extends ICablePart> getBasePartType();
}
