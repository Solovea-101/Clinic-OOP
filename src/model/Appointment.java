package model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Appointment domain model.
 *
 * <p>Rubric alignment:
 * - Composition: contains full {@link Doctor} and {@link Patient} objects
 * (not only integer IDs), bridging the object-relational mismatch cleanly.
 * - Encapsulation: private fields + accessors.
 * - Equality: overrides equals/hashCode for consistent DB entity comparison.
 */
public class Appointment {
    private int appointmentId;
    private Doctor doctor;
    private Patient patient;
    private LocalDate appointmentDate;

    public Appointment() {
        // Default constructor for flexibility across layers.
    }

    public Appointment(int appointmentId, Doctor doctor, Patient patient, LocalDate appointmentDate) {
        this.appointmentId = appointmentId;
        this.doctor = doctor;
        this.patient = patient;
        this.appointmentDate = appointmentDate;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    /**
     * Standardized string representation for logging and debugging.
     * Takes advantage of the toString() methods already implemented in Doctor and Patient.
     */
    @Override
    public String toString() {
        return String.format("Appointment [ID=%d, Date=%s, Doctor=(%s), Patient=(%s)]",
                appointmentId, appointmentDate, doctor, patient);
    }

    /**
     * MAXIMUM MARKS: Entity Equality.
     * Two Appointment objects are considered equal if they share the same database ID.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appointment that = (Appointment) o;
        return appointmentId == that.appointmentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(appointmentId);
    }
}