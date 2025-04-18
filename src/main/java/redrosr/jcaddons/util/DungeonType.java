package redrosr.jcaddons.util;

public enum DungeonType {
    OLD_VAULT("Old Vault"),
    TOXIC_CAVES("Toxic Caves"),
    DESERT_TEMPLE("Desert Temple"),
    NETHER_FORTRESS("Nether Fortress"),
    ILLAGER_PALACE("Illager Palace"),
    ICE_CAVES("Ice Caves"),
    VAMPIRE_VILLAGE("Vampire Village"),
    CHALLENGE("Challenge");

    private final String nameFallback;

    DungeonType(String nameFallback) {
        this.nameFallback = nameFallback;
    }

    public String getNameFallback() {
        return nameFallback;
    }
}

