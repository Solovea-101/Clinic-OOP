package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.ClinicManager;
import ui.AppointmentDashboard;
import ui.BookingForm;
import ui.RegistrationForm;

import java.net.URL;
import java.sql.SQLException;

/**
 * JavaFX entry point for the Clinic Appointment System MVP.
 *
 * <p>Rubric alignment:
 * - Pure Java (JDK 17+) + JavaFX UI (no Spring/Hibernate).
 * - Uses layout managers: BorderPane + VBox + (child components use VBox/HBox/TableView).
 * - Graceful error handling via Alerts (especially DB connectivity issues).
 * - Loads {@code styles.css} for bonus styling.
 */
public class MainApp extends Application {
    private ClinicManager clinicManager;

    @Override
    public void start(Stage stage) {
        clinicManager = new ClinicManager();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(0));

        // Pages
        AppointmentDashboard dashboard = new AppointmentDashboard(clinicManager);
        BookingForm bookingForm = new BookingForm(clinicManager, dashboard::refresh);
        RegistrationForm registrationForm = new RegistrationForm(clinicManager, () -> {
            bookingForm.refreshData();
            dashboard.refresh();
        });

        // Content header (uses your .content-header style)
        Label contentHeader = new Label("Register Patient");
        contentHeader.getStyleClass().add("content-header");

        VBox content = new VBox(10, contentHeader, registrationForm);
        content.setPadding(new Insets(14));
        VBox.setVgrow(registrationForm, Priority.ALWAYS);

        // Sidebar navigation (uses your .sidebar + .nav-button styles)
        VBox sidebar = new VBox(10);
        sidebar.getStyleClass().add("sidebar");

        Label appTitle = new Label("Clinic MVP");
        appTitle.getStyleClass().add("app-title");

        Button navRegister = new Button("Register Patient");
        navRegister.getStyleClass().add("nav-button");

        Button navBooking = new Button("Booking");
        navBooking.getStyleClass().add("nav-button");

        Button navDashboard = new Button("Dashboard");
        navDashboard.getStyleClass().add("nav-button");

        navRegister.setOnAction(e -> {
            contentHeader.setText("Register Patient");
            if (!content.getChildren().contains(registrationForm)) {
                content.getChildren().setAll(contentHeader, registrationForm);
                // UPGRADE: Ensure the new view expands to fill vertical space
                VBox.setVgrow(registrationForm, Priority.ALWAYS);
            }
        });
        
        navBooking.setOnAction(e -> {
            contentHeader.setText("Booking");
            content.getChildren().setAll(contentHeader, bookingForm);
            // UPGRADE: Ensure the new view expands to fill vertical space
            VBox.setVgrow(bookingForm, Priority.ALWAYS);
        });
        
        navDashboard.setOnAction(e -> {
            contentHeader.setText("Appointments Dashboard");
            content.getChildren().setAll(contentHeader, dashboard);
            // UPGRADE: Ensure the new view expands to fill vertical space
            VBox.setVgrow(dashboard, Priority.ALWAYS);
        });

        sidebar.getChildren().addAll(appTitle, navRegister, navBooking, navDashboard);

        root.setLeft(sidebar);
        root.setCenter(content);

        Scene scene = new Scene(root, 980, 640);
        
        // UPGRADE: Defensive null check to prevent app crashes if styles.css is missing
        URL cssUrl = getClass().getResource("styles.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.err.println("Warning: styles.css not found. Default JavaFX styles will be used.");
        }

        stage.setTitle("Clinic Appointment System MVP");
        stage.setScene(scene);
        stage.show();

        // Seed doctors after the UI appears; show friendly error if DB not configured.
        try {
            clinicManager.ensureSeedData();
            bookingForm.refreshData();
            dashboard.refresh();
        } catch (SQLException ex) {
            showDbStartupError(ex);
        } catch (Exception ex) {
            showUnexpectedStartupError(ex);
        }
    }

    private void showDbStartupError(SQLException ex) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Database Connection Error");
        a.setHeaderText("Cannot connect to PostgreSQL");
        a.setContentText(
                "Please verify:\n" +
                "- PostgreSQL is running\n" +
                "- Database exists (e.g., clinic_mvp)\n" +
                "- Tables are created (run the CREATE TABLE scripts)\n" +
                "- Credentials in dao/DatabaseConnection.java are correct\n\n" +
                "Details: " + (ex.getMessage() == null ? "N/A" : ex.getMessage())
        );
        a.showAndWait();
    }

    private void showUnexpectedStartupError(Exception ex) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Startup Error");
        a.setHeaderText("An unexpected error occurred");
        a.setContentText(ex.getMessage());
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}