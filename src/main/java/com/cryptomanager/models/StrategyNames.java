package com.cryptomanager.models;

public enum StrategyNames {
    AGGRESSIVE("AGGRESSIVE"),
    MODERATE("MODERATE"),
    CONSERVATIVE("CONSERVATIVE");

    private final String displayName;

    StrategyNames(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
