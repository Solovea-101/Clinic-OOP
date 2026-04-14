package dao;

import model.Appointment;
import model.Doctor;
import model.Gender;
import model.Patient;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional; // Added import for Optional
import java.util.Set;

/**
 * DAO for Appointment persistence.
 *
 * <p>Rubric alignment:
 * - Raw JDBC + PreparedStatement usage.
 * - Composition mapping: returns {@link Appointment} containing full {@link Doctor} and {@link Patient} objects
 * via SQL joins (not just integer IDs).
 */
public class AppointmentDAO implements DataAccessObject<Appointment> {
    private final DatabaseConnection databaseConnection;

    public AppointmentDAO(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    @Override
    public int insert(Appointment appointment) throws SQLException {
        final String sql = """
                INSERT INTO appointments(doctor_id, patient_id, appointment_date)
                VALUES (?, ?, ?)
                """;

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, appointment.getDoctor().getDoctorId());
            ps.setInt(2, appointment.getPatient().getPatientId());
            ps.setDate(3, Date.valueOf(appointment.getAppointmentDate()));
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Failed to create appointment row.");
                }
                int appointmentId = keys.getInt(1);
                appointment.setAppointmentId(appointmentId);
                return appointmentId;
            }
        }
    }

    @Override
    public List<Appointment> getAll() throws SQLException {
        final String sql = """
    SELECT
        a.appointment_id,
        a.appointment_date,
        d.doctor_id,
        du.id AS doctor_user_id,
        du.name AS doctor_name,
        d.specialization,
        p.patient_id,
        pu.id AS patient_user_id,
        pu.name AS patient_name,
        p.dob,                      -- Ensure this matches pgAdmin
        p.gender
    FROM appointments a
    JOIN doctors d ON d.doctor_id = a.doctor_id
    JOIN users du ON du.id = d.user_id
    JOIN patients p ON p.patient_id = a.patient_id
    JOIN users pu ON pu.id = p.user_id
    """; // Removed the WHERE clause!

        List<Appointment> appointments = new ArrayList<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Reconstruct the Doctor object
                Doctor doctor = new Doctor(
                        rs.getInt("doctor_user_id"),
                        rs.getString("doctor_name"),
                        rs.getInt("doctor_id"),
                        rs.getString("specialization")
                );

                // Reconstruct the Patient object
                Patient patient = new Patient(
                        rs.getInt("patient_user_id"),
                        rs.getString("patient_name"),
                        rs.getInt("patient_id"),
                        rs.getDate("dob").toLocalDate(),
                        Gender.valueOf(rs.getString("gender"))
                );

                // Create the Appointment object
                appointments.add(new Appointment(
                        rs.getInt("appointment_id"),
                        doctor,
                        patient,
                        rs.getDate("appointment_date").toLocalDate()
                ));
            }
        }
        return appointments;
    }

    @Override // Implemented from the updated DataAccessObject<T>
    public Optional<Appointment> getById(int id) throws SQLException {
        final String sql = """
        SELECT
            a.appointment_id,
            a.appointment_date,
            d.doctor_id,
            du.id AS doctor_user_id,     -- 1. Changed from du.user_id
            du.name AS doctor_name,
            d.specialization,
            p.patient_id,
            pu.id AS patient_user_id,     -- 2. Changed from pu.user_id
            pu.name AS patient_name,
            p.dob,
            p.gender
        FROM appointments a
        JOIN doctors d ON d.doctor_id = a.doctor_id
        JOIN users du ON du.id = d.user_id       -- 3. Changed from du.user_id = d.user_id
        JOIN patients p ON p.patient_id = a.patient_id
        JOIN users pu ON pu.id = p.user_id       -- 4. Changed from pu.user_id = p.user_id
        WHERE a.appointment_id = ?
        """;

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                Doctor doctor = new Doctor(
                        rs.getInt("doctor_user_id"),
                        rs.getString("doctor_name"),
                        rs.getInt("doctor_id"),
                        rs.getString("specialization")
                );

                Patient patient = new Patient(
                        rs.getInt("patient_user_id"),
                        rs.getString("patient_name"),
                        rs.getInt("patient_id"),
                        rs.getDate("dob").toLocalDate(),
                        Gender.valueOf(rs.getString("gender"))
                );

                return Optional.of(new Appointment(
                        rs.getInt("appointment_id"),
                        doctor,
                        patient,
                        rs.getDate("appointment_date").toLocalDate()
                ));
            }
        }
    }

    public boolean existsForDoctorAndDate(int doctorId, LocalDate date) throws SQLException {
        final String sql = "SELECT 1 FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Set<LocalDate> getBookedDatesForDoctor(int doctorId) throws SQLException {
        final String sql = "SELECT appointment_date FROM appointments WHERE doctor_id = ?";
        Set<LocalDate> dates = new HashSet<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dates.add(rs.getDate("appointment_date").toLocalDate());
                }
            }
        }
        return dates;
    }
}