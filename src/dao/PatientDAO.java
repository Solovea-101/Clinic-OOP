package dao;

import model.Gender;
import model.Patient;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientDAO {
    private final DatabaseConnection databaseConnection;

    public PatientDAO(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public int insert(Patient patient) throws SQLException {
        // Note: 'dob' matches the column name we used in the pgAdmin script
        String insertUserSql = "INSERT INTO users(name, role) VALUES (?, 'PATIENT')";
        String insertPatientSql = "INSERT INTO patients(user_id, dob, gender) VALUES (?, ?, ?)";

        try (Connection conn = databaseConnection.getConnection()) {
            // 1. START TRANSACTION
            conn.setAutoCommit(false);

            try {
                int userId;
                // 2. INSERT INTO USERS TABLE
                try (PreparedStatement psUser = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                    psUser.setString(1, patient.getName());
                    psUser.executeUpdate();

                    try (ResultSet rs = psUser.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("Failed to create User record.");
                        userId = rs.getInt(1);
                    }
                }

                int patientId;
                // 3. INSERT INTO PATIENTS TABLE
                try (PreparedStatement psPatient = conn.prepareStatement(insertPatientSql, Statement.RETURN_GENERATED_KEYS)) {
                    psPatient.setInt(1, userId); // Link to the user we just created
                    psPatient.setDate(2, java.sql.Date.valueOf(patient.getDateOfBirth()));
                    psPatient.setString(3, patient.getGender().name());
                    psPatient.executeUpdate();

                    try (ResultSet rs = psPatient.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("Failed to create Patient record.");
                        patientId = rs.getInt(1);
                    }
                }

                // 4. COMMIT - Save everything to the database at once
                conn.commit();

                // Update the object in memory
                patient.setId(userId);
                patient.setPatientId(patientId);

                return patientId;

            } catch (SQLException e) {
                // 5. ROLLBACK - If anything goes wrong, undo the User insert
                conn.rollback();
                throw e;
            } finally {
                // Restore default behavior
                conn.setAutoCommit(true);
            }
        }
    }

    public List<Patient> getAll() throws SQLException {
        // FIX: Must JOIN with users to get the name and use 'dob'
        final String sql = """
            SELECT p.patient_id, u.id AS user_id, u.name, p.dob, p.gender 
            FROM patients p 
            JOIN users u ON p.user_id = u.id 
            ORDER BY u.name""";

        List<Patient> patients = new ArrayList<>();

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                patients.add(new Patient(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getInt("patient_id"),
                        rs.getDate("dob").toLocalDate(),
                        Gender.valueOf(rs.getString("gender"))
                ));
            }
        }
        return patients;
    }

    public Optional<Patient> getById(int patientId) throws SQLException {
        final String sql = """
        SELECT p.patient_id, u.id AS user_id, u.name, p.dob, p.gender 
        FROM patients p 
        JOIN users u ON p.user_id = u.id 
        WHERE p.patient_id = ?""";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { // Fixed: removed the '!'
                    return Optional.of(new Patient(
                            rs.getInt("user_id"), // Fixed: match the SELECT alias
                            rs.getString("name"),
                            rs.getInt("patient_id"),
                            rs.getDate("dob").toLocalDate(),
                            Gender.valueOf(rs.getString("gender"))
                    ));
                }
            }
        }
        return Optional.empty();
    }
}