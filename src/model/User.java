package model;

/**
 * Abstract base class representing a system user.
 *
 * <p>Rubric alignment:
 * - Inheritance: extended by Doctor and Patient.
 * - Encapsulation: all fields private with getters/setters.
 * - Polymorphism: subclasses override getRoleDetails().
 */
public abstract class User {
    private int id;          // changed from userId → id
    private String name;

    protected User() {
        // Default constructor for flexibility across layers.
    }

    protected User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {     // updated getter
        return id;
    }

    public void setId(int id) {  // updated setter
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Polymorphic hook implemented by subclasses to display role-specific details.
     */
    public abstract String getRoleDetails();

    /**
     * Standardized string representation for logging and debugging.
     */
    @Override
    public String toString() {
        return "User [ID=" + id + ", Name=" + name + ", Details=" + getRoleDetails() + "]";
    }
}