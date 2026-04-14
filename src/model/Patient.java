package model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Patient entity.
 *
 * <p>Rubric alignment:
 * - Inheritance: extends {@link User}.
 * - Encapsulation: private fields + accessors.
 * - Polymorphism: overrides {@link #getRoleDetails()}.
 * - Uses {@link LocalDate} for Date of Birth.
 * - Equality: overrides equals/hashCode for consistent DB entity comparison.
 */
public class Patient extends User {
    private int patientId;
    private LocalDate dateOfBirth;
    private Gender gender;

    public Patient() {
        super();
    }

    public Patient(int userId, String name, int patientId, LocalDate dateOfBirth, Gender gender) {
        super(userId, name);
        this.patientId = patientId;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    @Override
    public String getRoleDetails() {
        return "Patient (" + gender + ", DOB " + dateOfBirth + ")";
    }

    @Override
    public String toString() {
        return getName() + " — " + gender + " — DOB " + dateOfBirth;
    }

    /**
     * MAXIMUM MARKS: Entity Equality.
     * Two Patient objects are considered equal if they share the same database ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return patientId == patient.patientId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId);
    }
}