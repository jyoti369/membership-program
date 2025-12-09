package com.firstclub.membership.enums;

public enum TierLevel {
    SILVER(1),
    GOLD(2),
    PLATINUM(3);

    private final int level;

    TierLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public boolean isHigherThan(TierLevel other) {
        return this.level > other.level;
    }

    public boolean isLowerThan(TierLevel other) {
        return this.level < other.level;
    }
}
