package com.cryptomanager.models;

public enum StrategyNames {
    AGGRESSIVE("Aggressive"),
    MODERATE("Moderate"),
    CONSERVATIVE("Conservative");

    private final String displayName;

    StrategyNames(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
