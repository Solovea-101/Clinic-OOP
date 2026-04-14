package dao;

import model.Doctor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DoctorDAO {
    private final DatabaseConnection databaseConnection;

    public DoctorDAO(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public int insert(Doctor doctor) throws SQLException {
        // Path B: Two-step insert
        String insertUserSql = "INSERT INTO users(name, role) VALUES (?, 'DOCTOR')";
        String insertDoctorSql = "INSERT INTO doctors(user_id, specialization) VALUES (?, ?)";

        try (Connection conn = databaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Start Transaction
            try {
                int userId;
                // 1. Insert into Users
                try (PreparedStatement psUser = conn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                    psUser.setString(1, doctor.getName()); // Parameter 1 for User
                    psUser.executeUpdate();
                    try (ResultSet rs = psUser.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("User insert failed.");
                        userId = rs.getInt(1);
                    }
                }

                int doctorId;
                // 2. Insert into Doctors
                try (PreparedStatement psDoc = conn.prepareStatement(insertDoctorSql, Statement.RETURN_GENERATED_KEYS)) {
                    psDoc.setInt(1, userId); // Parameter 1 for Doctor
                    psDoc.setString(2, doctor.getSpecialization()); // Parameter 2 for Doctor
                    psDoc.executeUpdate();
                    try (ResultSet rs = psDoc.getGeneratedKeys()) {
                        if (!rs.next()) throw new SQLException("Doctor insert failed.");
                        doctorId = rs.getInt(1);
                    }
                }

                conn.commit();
                doctor.setId(userId);
                doctor.setDoctorId(doctorId);
                return doctorId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public List<Doctor> getAll() throws SQLException {
        // FIX: Added JOIN to get 'name' from users table
        final String sql = """
                SELECT d.doctor_id, u.id AS user_id, u.name, d.specialization 
                FROM doctors d 
                JOIN users u ON d.user_id = u.id 
                ORDER BY u.name""";

        List<Doctor> doctors = new ArrayList<>();
        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                doctors.add(new Doctor(
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getInt("doctor_id"),
                        rs.getString("specialization")
                ));
            }
        }
        return doctors;
    }

    public Optional<Doctor> getById(int doctorId) throws SQLException {
        // FIX: Added JOIN and Parameter setting
        final String sql = """
                SELECT d.doctor_id, u.id AS user_id, u.name, d.specialization 
                FROM doctors d 
                JOIN users u ON d.user_id = u.id 
                WHERE d.doctor_id = ?""";

        try (Connection conn = databaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, doctorId); // <--- This fixes the 'Parameter 1' error!

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Doctor(
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getInt("doctor_id"),
                            rs.getString("specialization")
                    ));
                }
            }
        }
        return Optional.empty();
    }

    public void ensureSeedDoctorsExist() throws SQLException {
        if (getAll().isEmpty()) {
            insert(new Doctor(0, "Dr. Amina Noor", 0, "General Practice"));
            insert(new Doctor(0, "Dr. David Kim", 0, "Dermatology"));
            insert(new Doctor(0, "Dr. Sara Ochieng", 0, "Pediatrics"));
        }
    }
}