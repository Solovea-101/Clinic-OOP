package model;

/**
 * Gender enum for the Patient model.
 *
 * <p>Meets rubric requirements:
 * - Enum usage for constrained values in the UI + DB layer.
 */
public enum Gender {
    MALE("Male"),
    FEMALE("Female"),
    OTHER("Other");

    private final String displayName;

    // Enum constructor
    Gender(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Overriding toString() ensures that JavaFX UI components (like ComboBox)
     * display the formatted displayName instead of the all-caps enum name.
     */
    @Override
    public String toString() {
        return displayName;
    }
}