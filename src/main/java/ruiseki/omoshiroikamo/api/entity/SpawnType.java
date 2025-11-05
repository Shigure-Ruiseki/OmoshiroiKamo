package ruiseki.omoshiroikamo.api.entity;

public enum SpawnType {

    NORMAL,
    SNOW,
    NONE,
    HELL;

    public static String[] names() {
        SpawnType[] states = values();
        String[] names = new String[states.length];

        for (int i = 0; i < states.length; i++) {
            names[i] = states[i].name();
        }

        return names;
    }

    public static SpawnType fromName(String name) {
        if (name == null || name.isEmpty()) {
            return NONE;
        }

        try {
            return SpawnType.valueOf(
                name.trim()
                    .toUpperCase());
        } catch (Exception e) {
            return NONE;
        }
    }

}
