package ui;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.Appointment;
import service.ClinicManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Feature 4 UI: Appointment dashboard.
 *
 * <p>Rubric alignment:
 * - JavaFX TableView displays joined Doctor + Patient data (composition mapping from DAO).
 * - Exception handling: SQL errors shown via Alerts.
 */
public class AppointmentDashboard extends VBox {
    private final ClinicManager clinicManager;

    private final TableView<AppointmentRow> table = new TableView<>();
    private final Button refreshButton = new Button("Refresh");

    public AppointmentDashboard(ClinicManager clinicManager) {
        this.clinicManager = clinicManager;
        buildUi();
        refresh();
    }

    private void buildUi() {
        getStyleClass().add("card");
        setSpacing(12);
        setPadding(new Insets(14));

        Label title = new Label("Appointments Dashboard");
        title.getStyleClass().add("section-title");

        refreshButton.getStyleClass().add("primary-button");
        refreshButton.setOnAction(e -> refresh());

        HBox header = new HBox(10, title, spacer(), refreshButton);

        TableColumn<AppointmentRow, Integer> idCol = new TableColumn<>("Appt ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        idCol.setPrefWidth(80);

        TableColumn<AppointmentRow, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(120);

        TableColumn<AppointmentRow, String> doctorCol = new TableColumn<>("Doctor");
        doctorCol.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        doctorCol.setPrefWidth(180);

        TableColumn<AppointmentRow, String> specCol = new TableColumn<>("Specialization");
        specCol.setCellValueFactory(new PropertyValueFactory<>("doctorSpecialization"));
        specCol.setPrefWidth(160);

        TableColumn<AppointmentRow, String> patientCol = new TableColumn<>("Patient");
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        patientCol.setPrefWidth(180);

        TableColumn<AppointmentRow, String> genderCol = new TableColumn<>("Gender");
        genderCol.setCellValueFactory(new PropertyValueFactory<>("patientGender"));
        genderCol.setPrefWidth(90);

        table.getColumns().addAll(idCol, dateCol, doctorCol, specCol, patientCol, genderCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        getChildren().addAll(header, table);
    }

    private VBox spacer() {
        VBox v = new VBox();
        HBox.setHgrow(v, Priority.ALWAYS);
        return v;
    }

    public void refresh() {
        try {
            List<Appointment> appointments = clinicManager.getAllAppointments();
            table.setItems(FXCollections.observableArrayList(
                    appointments.stream().map(AppointmentRow::from).toList()
            ));
        } catch (SQLException ex) {
            showError("Database Error", safeSqlMessage(ex));
        }
    }

    private void showError(String header, String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(header);
        a.setContentText(message);
        a.showAndWait();
    }

    private String safeSqlMessage(SQLException ex) {
        String msg = ex.getMessage();
        if (msg == null || msg.isBlank()) {
            msg = "A database error occurred.";
        }
        return msg;
    }

    /**
     * TableView-friendly row adapter for Appointment.
     *
     * <p>Why this exists:
     * - Keeps TableView columns simple (String/LocalDate/Integer properties),
     * while the domain model remains cleanly composed (Appointment -> Doctor/Patient).
     */
    public static class AppointmentRow {
        private final int appointmentId;
        private final LocalDate date;
        private final String doctorName;
        private final String doctorSpecialization;
        private final String patientName;
        private final String patientGender;

        public AppointmentRow(int appointmentId, LocalDate date, String doctorName, String doctorSpecialization,
                              String patientName, String patientGender) {
            this.appointmentId = appointmentId;
            this.date = date;
            this.doctorName = doctorName;
            this.doctorSpecialization = doctorSpecialization;
            this.patientName = patientName;
            this.patientGender = patientGender;
        }

        public static AppointmentRow from(Appointment a) {
            return new AppointmentRow(
                    a.getAppointmentId(),
                    a.getAppointmentDate(),
                    a.getDoctor().getName(),
                    a.getDoctor().getSpecialization(),
                    a.getPatient().getName(),
                    a.getPatient().getGender().name()
            );
        }

        public int getAppointmentId() {
            return appointmentId;
        }

        public LocalDate getDate() {
            return date;
        }

        public String getDoctorName() {
            return doctorName;
        }

        public String getDoctorSpecialization() {
            return doctorSpecialization;
        }

        public String getPatientName() {
            return patientName;
        }

        public String getPatientGender() {
            return patientGender;
        }
    }
}