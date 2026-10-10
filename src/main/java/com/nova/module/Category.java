package com.nova.module;

public enum Category {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    RENDER("Render"),
    MISC("Misc"),
    CLIENT("Client");

    public final String label;

    Category(String label) {
        this.label = label;
    }
}
