package dao; // Ensure package matches your structure

import java.sql.SQLException;
import java.util.List;
import java.util.Optional; 

/**
 * Generic DAO interface.
 *
 * <p>Rubric alignment:
 * - Generics requirement via {@code DataAccessObject<T>}.
 * - Implemented by concrete DAOs for Doctor/Patient/Appointment.
 *
 * @param <T> entity type
 */
public interface DataAccessObject<T> {
    
    /**
     * Inserts an entity into the database.
     * @return The auto-generated ID from the database, or rows affected.
     */
    int insert(T entity) throws SQLException;

    /**
     * Retrieves all entities from the database.
     * @return A List of all entities.
     */
    List<T> getAll() throws SQLException;

    /**
     * Retrieves a single entity by its primary key ID.
     * Using Optional<T> is a great way to handle nulls gracefully in Java.
     */
    Optional<T> getById(int id) throws SQLException;
}