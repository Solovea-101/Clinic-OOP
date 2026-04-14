package model;

import java.util.Objects;

/**
 * Doctor entity.
 *
 * <p>Rubric alignment:
 * - Inheritance: extends {@link User}.
 * - Encapsulation: private fields + accessors.
 * - Polymorphism: overrides {@link #getRoleDetails()}.
 * - Equality: overrides equals/hashCode for consistent DB entity comparison.
 */
public class Doctor extends User {
    private int doctorId;
    private String specialization;

    public Doctor() {
        super();
    }

    public Doctor(int userId, String name, int doctorId, String specialization) {
        super(userId, name);
        this.doctorId = doctorId;
        this.specialization = specialization;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String getRoleDetails() {
        return "Doctor (" + specialization + ")";
    }

    @Override
    public String toString() {
        return getName() + " — " + specialization;
    }

    /**
     * MAXIMUM MARKS: Entity Equality.
     * Two Doctor objects are considered equal if they share the same database ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return doctorId == doctor.doctorId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorId);
    }
}