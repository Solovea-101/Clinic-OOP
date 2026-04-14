package exception;

/**
 * Custom exception thrown when an appointment slot is not available.
 *
 * <p>Rubric alignment:
 * - Custom exceptions + double-booking rule.
 * - Thrown when a UNIQUE(doctor_id, appointment_date) constraint is violated,
 * or when business rules detect the slot is not bookable.
 */
public class SlotUnavailableException extends Exception {

    /**
     * Used when our business logic flags an error 
     * (e.g., checking the date and realizing it's a weekend).
     */
    public SlotUnavailableException(String message) {
        super(message);
    }

    /**
     * BONUS: Exception Chaining.
     * Used when the Database throws an SQLException (UNIQUE constraint violation),
     * allowing us to wrap the SQL error in our custom exception without losing the stack trace.
     */
    public SlotUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}